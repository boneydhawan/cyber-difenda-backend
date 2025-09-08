package com.cyber.difenda.service;

import java.io.IOException;

import javax.net.ssl.*;

public class TLSChecker {
	public static boolean isTlsSupported(String host, int port, String protocol) {
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
                socket.setEnabledProtocols(new String[]{protocol});
                socket.startHandshake();
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }
    
}
