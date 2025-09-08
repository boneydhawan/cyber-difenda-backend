package com.cyber.difenda.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class HttpHeaderScanner {
	
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
			result.put("https_headers", headers);

			// HTTP → HTTPS redirect
			Map<String, List<String>> httpHeaders = getHeaders("http://" + host + "/");
			boolean redirectToHttps = false;
			if (httpHeaders.containsKey("Location")) {
				redirectToHttps = httpHeaders.get("Location").get(0).startsWith("https://");
			}
			result.put("http_redirects_to_https", redirectToHttps);

			// Required headers
			List<String> required = Arrays.asList("strict-transport-security", "content-security-policy",
					"x-frame-options", "x-content-type-options", "referrer-policy", "permissions-policy");
			
			List<String> missing = new ArrayList<>();
			for (String h : required) {
				if (!headers.containsKey(h)) {
					missing.add(h);
				}
			}
			if (!missing.isEmpty()) {
				findings.add("Missing recommended security headers: " + String.join(", ", missing));
			}
			if (!redirectToHttps) {
				findings.add("HTTP does not redirect to HTTPS on root");
			}
			
			checkHsts(host, result);
			checkCipher(host, result);

		} catch (Exception e) {
			findings.add("Error fetching headers: " + e.getMessage());
		}

		result.put("findings", findings);
		return result;
	}
	
	private static void checkHsts(String host, Map<String, Object> result) throws Exception {
		
        Map<String, List<String>> headers = getHeaders("http://" + host + "/");
        if (headers.containsKey("Strict-Transport-Security")) {
            String hsts = headers.get("Strict-Transport-Security").get(0);
            result.put("hstsHeader", hsts);

            for (String part : hsts.split(";")) {
                if (part.trim().startsWith("max-age")) {
                    int maxAge = Integer.parseInt(part.split("=")[1].trim());
                    result.put("hstsMaxAge", maxAge);
                }
            }
        } else {
        	result.put("hstsMaxAge", 0); // not set
        }
    }

    private static void checkCipher(String host, Map<String, Object> result) throws Exception {
        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault()
                .createSocket(host, 443)) {
            socket.startHandshake();
            String cipher = socket.getSession().getCipherSuite();
            result.put("cipherSuite",cipher);

            if (cipher.contains("MD5") || cipher.contains("RC4")) {
                result.put("weakCiphers",cipher);
            } else {
            	result.put("weakCiphers",null);
            }
        }
    }
}
