package com.cyber.difenda.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class HttpHeaderScanner {

	private static final String STRICT_TRANSPORT_SECURITY = "strict-transport-security";
	private static final String HSTS_HEADER = "hstsHeader";
	private static final String HSTS_MAX_AGE = "hstsMaxAge";
	private static final String LOCATION = "Location";
	private static final String HTTPS_HEADERS = "https_headers";
	private static final String FINDINGS = "findings";
	private static final String MISSING_SECURITY_HEADERS = "missing_security_headers";
	
	  private static final List<String> WEAK_CIPHERS = Arrays.asList(
	            "NULL", "RC4", "DES", "3DES", "MD5", "EXPORT"
	    );

	public static Map<String, List<String>> getHeaders(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.connect();
		return conn.getHeaderFields();
	}

	public static Map<String, Object> checkHeaders(String host) {
		Map<String, Object> result = new HashMap<>();
		List<String> findings = new ArrayList<>();

		try {
			// HTTPS headers
			Map<String, List<String>> httpsHeaders = getHeaders("https://" + host + "/");
			Map<String, String> headers = new HashMap<>();
			httpsHeaders.forEach((k, v) -> {
				if (k != null)
					headers.put(k.toLowerCase(), v.get(0));
			});
			result.put(HTTPS_HEADERS, headers);

			// HTTP → HTTPS redirect
			Map<String, List<String>> httpHeaders = getHeaders("http://" + host + "/");
			boolean redirectToHttps = false;
			if (httpHeaders.containsKey(LOCATION)) {
				redirectToHttps = httpHeaders.get(LOCATION).get(0).startsWith("https://");
			}
			result.put("http_redirects_to_https", redirectToHttps);

			// Required headers
			List<String> required = Arrays.asList(STRICT_TRANSPORT_SECURITY, "content-security-policy",
					"x-frame-options", "x-content-type-options", "referrer-policy", "permissions-policy");

			List<String> missing = new ArrayList<>();
			for (String h : required) {
				if (!headers.containsKey(h)) {
					missing.add(h);
				}
			}
			result.put(MISSING_SECURITY_HEADERS, missing);

			/** HSTS_MAX_AGE **/
			Optional<Map.Entry<String, String>> entry = headers.entrySet().stream()
					.filter(e -> e.getKey().equalsIgnoreCase(STRICT_TRANSPORT_SECURITY)).findFirst();
								
			if (entry.isPresent()) {
				String hsts = entry.get().getValue(); 
				result.put(HSTS_HEADER, hsts);
				
				for (String part : hsts.split(";")) {
					String trimmed = part.trim().toLowerCase();
					if (trimmed.startsWith("max-age")) {
						String[] kv = trimmed.split("=", 2);
						if (kv.length == 2) {
							try {
								int maxAge = Integer.parseInt(kv[1].trim());
								result.put(HSTS_MAX_AGE, maxAge);
							} catch (NumberFormatException e) {
								result.put(HSTS_MAX_AGE, 0);
							}
						}
					}
				}
			} else {
				result.put(HSTS_MAX_AGE, 0); // not set
			}
			checkCipher(host, result);

		} catch (Exception e) {
			findings.add("Error fetching headers: " + e.getMessage());
		}

		result.put(FINDINGS, findings);
		return result;
	}

	private static void checkCipher(String host, Map<String, Object> result) throws Exception {
		try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, 443)) {
			socket.startHandshake();
			String cipher = socket.getSession().getCipherSuite();
			result.put("cipherSuite", cipher);

			if (isWeak(cipher)) {
				result.put("weakCiphers", cipher);
			} else {
				result.put("weakCiphers", null);
			}
		}
	}
	
	private static boolean isWeak(String cipher) {
        return WEAK_CIPHERS.stream().anyMatch(cipher::contains);
    }
}
