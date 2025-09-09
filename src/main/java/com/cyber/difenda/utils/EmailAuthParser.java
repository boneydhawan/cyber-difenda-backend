package com.cyber.difenda.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.naming.directory.*;
import javax.naming.*;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import com.cyber.difenda.model.EmailSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmailAuthParser {

	public static Map<String, Object> parseSpf(List<String> txts) {
        if (txts == null || txts.isEmpty()) return null;

        String spf = txts.stream()
                .filter(t -> t.toLowerCase().startsWith("v=spf1"))
                .findFirst().orElse(null);

        if (spf == null) return null;

        List<String> findings = new ArrayList<>();
        if (spf.contains(" -all")) {
            // strict, do nothing
        } else if (spf.contains("~all") || spf.contains("?all")) {
            findings.add("SPF not strict (no -all)");
        } else {
            findings.add("SPF missing terminal all mechanism");
        }

        if (spf.contains(" +all")) {
            findings.add("SPF permits all (+all) — dangerous");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("raw", spf);
        result.put("findings", findings);
        return result;
	}
	
	public static Map<String, Object> parseDmarc(String raw) {
	    if (raw == null || raw.isEmpty()) return null;

	    String tag = raw.toLowerCase();
	    Map<String, String> parts = Arrays.stream(tag.split(";"))
	            .filter(p -> p.contains("="))
	            .map(p -> p.split("=", 2))
	            .collect(Collectors.toMap(a -> a[0].trim(), a -> a[1].trim()));

	    String p = parts.getOrDefault("p", "");
	    String pct = parts.getOrDefault("pct", "100");
	    String adkim = parts.getOrDefault("adkim", "r");
	    String aspf = parts.getOrDefault("aspf", "r");
	    String rua = parts.get("rua");

	    List<String> findings = new ArrayList<>();
	    if (p.isEmpty() || p.equals("none")) {
	        findings.add("DMARC policy is p=none (monitoring only)");
	    }

	    try {
	        int pctVal = Integer.parseInt(pct.replaceAll("\\D", ""));
	        if (pctVal < 100) findings.add("DMARC applies to only pct=" + pctVal + "% of mail");
	    } catch (Exception ignored) {}

	    if (!adkim.equals("s") || !aspf.equals("s")) {
	        findings.add("DMARC alignment not strict (adkim/aspf)");
	    }
	    if (rua == null || rua.isEmpty()) findings.add("DMARC missing rua= aggregate reporting");

	    Map<String, Object> result = new HashMap<>();
	    result.put("raw", raw);
	    result.put("policy", p);
	    result.put("pct", pct);
	    result.put("adkim", adkim);
	    result.put("aspf", aspf);
	    result.put("rua", rua);
	    result.put("findings", findings);

	    return result;
	}
	
	public static EmailSecurity fetchEmailSecurity(String domain, Long scanId) {
	    EmailSecurity emailSecurity = new EmailSecurity();
	    emailSecurity.setScanId(scanId); // assuming Domain entity constructor sets domainName

	    List<String> findings = new ArrayList<>();

	    ObjectMapper objectMapper = new ObjectMapper(); // for JSON string conversion

	    // 1️⃣ SPF
	    List<String> spfTxts = dnsTxt(domain);
	    Map<String, Object> spf = EmailAuthParser.parseSpf(spfTxts);
	    if (spf != null) {
	        emailSecurity.setSpfRecord((String) spf.get("raw"));
	        try {
	            emailSecurity.setFindings(objectMapper.writeValueAsString(spf.get("findings")));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    } else {
	        findings.add("No SPF record found");
	    }

	    // 2️⃣ DMARC
	    List<String> dmarcTxts = dnsTxt("_dmarc." + domain);
	    if (dmarcTxts != null && !dmarcTxts.isEmpty()) {
	        String raw = dmarcTxts.stream()
	                .filter(t -> t.toLowerCase().startsWith("v=dmarc1"))
	                .findFirst()
	                .orElse(dmarcTxts.get(0));

	        Map<String, Object> dmarc = EmailAuthParser.parseDmarc(raw);
	        emailSecurity.setDmarcRecord(raw);
	        emailSecurity.setAdkim((String) dmarc.get("adkim"));
	        emailSecurity.setAspf((String) dmarc.get("aspf"));
	        emailSecurity.setRua((String) dmarc.get("rua"));
	        findings.addAll((List<String>) dmarc.get("findings"));
	    } else {
	        findings.add("No DMARC record found");
	    }

	    // 3️⃣ DKIM (common selectors)
	    List<String> dkimList = new ArrayList<>();
	    List<String> selectors = Arrays.asList("default", "selector1", "mail");
	    for (String sel : selectors) {
	        List<String> recs = dnsTxt(sel + "._domainkey." + domain);
	        String hit = recs.stream().filter(t -> t.toLowerCase().startsWith("v=dkim1")).findFirst().orElse(null);
	        if (hit != null) {
	            dkimList.add(sel);
	        }
	    }
	    if (dkimList.isEmpty()) findings.add("DKIM selector not discovered (may still exist with non-common names)");

	    try {
	    	String selector = dkimList.stream()
	    	        .map(s -> "selector=" + s)
	    	        .collect(Collectors.joining(", "));
	        emailSecurity.setDkimSelector(String.join(", ", selector));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // 4️⃣ MTA-STS & TLS-RPT
	    List<String> mtaStsTxts = dnsTxt("_mta-sts." + domain);
	    List<String> tlsRptTxts = dnsTxt("_smtp._tls." + domain);

	    String mtaSts = mtaStsTxts.stream().filter(t -> t.toLowerCase().startsWith("v=stsv1")).findFirst().orElse(null);
	    String tlsRpt = tlsRptTxts.stream().filter(t -> t.toLowerCase().startsWith("v=tlsrptv1")).findFirst().orElse(null);

	    emailSecurity.setMtaSts(mtaSts);
	    emailSecurity.setTlsRpt(tlsRpt);

	    if (mtaSts == null) findings.add("No MTA-STS TXT record");
	    if (tlsRpt == null) findings.add("No TLS-RPT TXT record");

	    // 5️⃣ Aggregate findings
	    try {
	        emailSecurity.setFindings(String.join(", ", findings));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return emailSecurity;
	}
	
	public static List<String> dnsTxt(String domain) {
	    List<String> records = new ArrayList<>();
	    try {
	        Hashtable<String, String> env = new Hashtable<>();
	        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
	        DirContext ctx = new InitialDirContext(env);

	        Attributes attrs = ctx.getAttributes(domain, new String[]{"TXT"});
	        Attribute attr = attrs.get("TXT");
	        if (attr != null) {
	            for (Enumeration<?> vals = attr.getAll(); vals.hasMoreElements(); ) {
	                String txt = vals.nextElement().toString();
	                records.add(txt.replaceAll("^\"|\"$", "")); // remove quotes
	            }
	        }
	    } catch (NameNotFoundException e) {
	        // Record does not exist, just return empty list
	    } catch (NamingException e) {
	        e.printStackTrace(); // other unexpected DNS issues
	    }
	    return records;
	}

	
}
