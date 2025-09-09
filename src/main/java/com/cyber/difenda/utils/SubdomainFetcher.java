package com.cyber.difenda.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.cyber.difenda.model.Subdomain;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SubdomainFetcher {

    private static final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    // ------------ crt.sh ------------
    public static Set<String> fetchFromCrtSh(String domain) {
        String url = "https://crt.sh/?q=%25." + domain + "&output=json";
        Set<String> results = new HashSet<>();
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .timeout(java.time.Duration.ofSeconds(20))
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                JSONArray arr = new JSONArray(resp.body());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    String nameVal = row.optString("name_value", "");
                    for (String n : nameVal.split("\n")) {
                        n = n.trim().toLowerCase();
                        if (n.startsWith("*.")) n = n.substring(2);
                        if (n.endsWith("." + domain) || n.equals(domain)) {
                            results.add(n);
                        }
                    }
                }
            } else {
                // fallback: HTML scrape
                results.addAll(scrapeCrtSh(domain));
            }
        } catch (Exception e) {
            results.addAll(scrapeCrtSh(domain));
        }
        return results;
    }

    private static Set<String> scrapeCrtSh(String domain) {
        Set<String> out = new HashSet<>();
        try {
            Document doc = Jsoup.connect("https://crt.sh/?q=%25." + domain)
                    .timeout(20000)
                    .get();
            String html = doc.html();
            Pattern p = Pattern.compile("(?i)(?:[\\w\\-\\*]+\\.)+" + Pattern.quote(domain));
            Matcher m = p.matcher(html);
            while (m.find()) {
                String c = m.group().toLowerCase().replaceFirst("^\\*\\.", "");
                if (c.endsWith(domain)) {
                    out.add(c);
                }
            }
        } catch (IOException ignored) {}
        return out;
    }

    // ------------ CertSpotter ------------
    public static Set<String> fetchFromCertSpotter(String domain) {
        String url = "https://api.certspotter.com/v1/issuances?domain=" + domain +
                "&include_subdomains=true&expand=dns_names&match_wildcards=true";
        Set<String> results = new HashSet<>();
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .timeout(java.time.Duration.ofSeconds(20))
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                JSONArray arr = new JSONArray(resp.body());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    JSONArray dns = obj.optJSONArray("dns_names");
                    if (dns != null) {
                        for (int j = 0; j < dns.length(); j++) {
                            String n = dns.getString(j).toLowerCase().replaceFirst("^\\*\\.", "").trim();
                            if (n.endsWith("." + domain) || n.equals(domain)) {
                                results.add(n);
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return results;
    }

    // ------------ RapidDNS ------------
    public static Set<String> fetchFromRapidDNS(String domain) {
        Set<String> out = new HashSet<>();
        try {
            Document doc = Jsoup.connect("https://rapiddns.io/subdomain/" + domain + "?full=1")
                    .timeout(20000)
                    .get();
            String html = doc.html();
            Pattern p = Pattern.compile("(?i)(?:[\\w\\-\\*]+\\.)+" + Pattern.quote(domain));
            Matcher m = p.matcher(html);
            while (m.find()) {
                String c = m.group().toLowerCase().replaceFirst("^\\*\\.", "");
                if (c.endsWith(domain)) {
                    out.add(c);
                }
            }
        } catch (IOException ignored) {}
        return out;
    }

    // ------------ Combine Sources ------------
    public static List<Subdomain> fetchSubdomains(String domain, Long scanId) {
        Set<Subdomain> all = new HashSet<>();
        Set<String> s1 = fetchFromCrtSh(domain);
        Set<String> s2 = fetchFromCertSpotter(domain);
        Set<String> s3 = fetchFromRapidDNS(domain);

        for (String hst : s1) {
            all.add(new Subdomain(scanId, hst, String.join(",", resolve(hst))));
        }
        for (String hst : s2) {
            all.add(new Subdomain(scanId, hst, String.join(",", resolve(hst))));
        }
        for (String hst : s3) {
            all.add(new Subdomain(scanId, hst, String.join(",", resolve(hst))));
        }
        return new ArrayList<>(all);
    }
    
    public static Set<String> resolve(String host) {
        Set<String> ips = new HashSet<>();
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress addr : addresses) {
                ips.add(addr.getHostAddress());
            }
        } catch (Exception e) {
            // unresolved host -> no IPs
        }
        return ips;
    }

}
