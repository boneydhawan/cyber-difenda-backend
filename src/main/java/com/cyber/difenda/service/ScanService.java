package com.cyber.difenda.service;

import org.springframework.stereotype.Service;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import com.cyber.difenda.enums.DnsConstants;
import com.cyber.difenda.model.DnsRecord;
import com.cyber.difenda.model.EmailSecurity;
import com.cyber.difenda.model.OpenPort;
import com.cyber.difenda.model.ScanTlsSecurity;
import com.cyber.difenda.model.Subdomain;
import com.cyber.difenda.repository.DnsRecordRepository;
import com.cyber.difenda.repository.EmailSecurityRepository;
import com.cyber.difenda.repository.OpenPortRepository;
import com.cyber.difenda.repository.ScanTlsSecurityRepository;
import com.cyber.difenda.repository.SubdomainRepository;
import com.cyber.difenda.utils.HttpHeaderScanner;
import com.cyber.difenda.utils.TLSChecker;

import lombok.RequiredArgsConstructor;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ScanService {

	private final DnsRecordRepository dnsRecordRepository;
	private final ScanTlsSecurityRepository tlsRepo;
	private final OpenPortRepository openPortRepository;
	private final SubdomainRepository subdomainRepository;
	private final EmailSecurityRepository emailSecurityRepo;

	public List<DnsRecord> findAllDNSforScanId(Long scanId) {
		return dnsRecordRepository.findByScanId(scanId);
	}

	public List<DnsRecord> scanDnsRecords(String domain, Long scanId) {
		List<DnsRecord> dnsRecords = new ArrayList<>();

		for (String type : DnsConstants.RECORD_TYPES) {
			try {
				Record[] records = new Lookup(domain, Type.value(type)).run();
				if (records != null) {
					for (Record r : records) {
						DnsRecord dnsRecord = new DnsRecord();
						dnsRecord.setScanId(scanId);
						dnsRecord.setRecordType(type);
						dnsRecord.setRecordValue(r.rdataToString());
						dnsRecords.add(dnsRecord);
					}
				}
			} catch (TextParseException e) {
				// log and continue
			}
		}

		return dnsRecords;
	}

	public Map<String, List<String>> lookupRecords(String domain) {
		Map<String, List<String>> results = new HashMap<>();

		String rootDomain = getRootDomain(domain);

		// Host-level lookups
		results.put("A", query(domain, Type.A));
		results.put("AAAA", query(domain, Type.AAAA));
		results.put("CNAME", query(domain, Type.CNAME));

		// Root-domain lookups
		results.put("NS", query(rootDomain, Type.NS));
		results.put("MX", query(rootDomain, Type.MX));
		results.put("SOA", query(rootDomain, Type.SOA));
		results.put("DS", query(rootDomain, Type.DS));
		results.put("CAA", query(rootDomain, Type.CAA));
		results.put("DNSKEY", query(rootDomain, Type.DNSKEY));
		results.put("RRSIG", query(rootDomain, Type.RRSIG));

		// TXT: merge both
		List<String> txtRecords = new ArrayList<>();
		txtRecords.addAll(query(domain, Type.TXT));
		txtRecords.addAll(query(rootDomain, Type.TXT));
		results.put("TXT", txtRecords);

		return results;
	}

	private List<String> query(String domain, int type) {
		List<String> records = new ArrayList<>();
		try {
			Resolver resolver = new SimpleResolver("8.8.8.8");
			resolver.setEDNS(0, 0, ExtendedFlags.DO, (List<EDNSOption>) null);

			Lookup lookup = new Lookup(domain, type);
			lookup.setResolver(resolver);
			Record[] recs = lookup.run();
			if (recs != null) {
				for (Record r : recs) {
					records.add(r.rdataToString());
				}
			}
		} catch (Exception e) {
			records.add("Error: " + e.getMessage());
		}
		return records;
	}

	private String getRootDomain(String domain) {
		String[] parts = domain.split("\\.");
		if (parts.length > 2) {
			return parts[parts.length - 2] + "." + parts[parts.length - 1];
		}
		return domain;
	}

	public ScanTlsSecurity getTlsSecurityForScan(Long scanId) {
		return tlsRepo.findByScanId(scanId).orElse(null);
	}
	
	public EmailSecurity getEmailSecurityForScan(Long scanId) {
		return emailSecurityRepo.findByScanId(scanId).orElse(null);
	}
	
	public List<OpenPort> getOpenPortsForScan(Long scanId) {
		return openPortRepository.findByScanId(scanId);
	}
	
	public List<Subdomain> getSubdomainsForScan(Long scanId) {
		return subdomainRepository.findByScanId(scanId);
	}

	public ScanTlsSecurity scanTlsSecurity(String domain, Long scanId) {
		ScanTlsSecurity result = new ScanTlsSecurity();

		// TLS support checks
		result.setTls_1_0(TLSChecker.isTlsSupported(domain, 443, "TLSv1"));
		result.setTls_1_1(TLSChecker.isTlsSupported(domain, 443, "TLSv1.1"));
		result.setTls_1_2(TLSChecker.isTlsSupported(domain, 443, "TLSv1.2"));
		result.setTls_1_3(TLSChecker.isTlsSupported(domain, 443, "TLSv1.3"));

		// Headers
		Map<String, Object> headersResult = HttpHeaderScanner.checkHeaders(domain);

		if (headersResult.containsKey("findings")) {
			String finding = headersResult.get("findings").toString();
			if (finding.contains("Missing")) {
				// crude extraction; better to parse directly
				result.setMissingHeaders(finding);
			}
		}
		
		result.setMissingHeaders(String.join(",", (List<String>) headersResult.get("missing_security_headers")));
		
		if (headersResult.containsKey("http_redirects_to_https")) {
			result.setHttp_to_https((Boolean) headersResult.get("http_redirects_to_https"));
		}

		result.setWeakCiphers(headersResult.get("weakCiphers") != null ? headersResult.get("weakCiphers").toString() : null); // placeholder if found
		result.setHstsMaxAge(Integer.parseInt(headersResult.get("hstsMaxAge").toString())); // placeholder from header parsing

		result.setScanId(scanId);
		return result;
	}
	
}
