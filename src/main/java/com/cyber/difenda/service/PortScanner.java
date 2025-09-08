package com.cyber.difenda.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.cyber.difenda.model.OpenPort;

public class PortScanner {
	
	private static final int[] COMMON_PORTS = { 21, 22, 25, 53, 80, 110, 143, 443, 465, 587, 993, 995, 1433, 1521, 3306,
			3389, 5432, 8080, 8443 };
	
	public static boolean tcpOpen(String host, int port, int timeoutMs) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, port), timeoutMs);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static List<Integer> scanPorts(String host, List<Integer> ports, int timeoutMs) {
		List<Integer> openPorts = new ArrayList<>();
		for (int port : ports) {
			if (tcpOpen(host, port, timeoutMs)) {
				openPorts.add(port);
			}
		}
		return openPorts;
	}


	public static List<OpenPort> scanPorts(String host, int timeout, Long scanId) {
		List<OpenPort> openPorts = new ArrayList<>();
		for (int port : COMMON_PORTS) {
			if (tcpOpen(host, port, timeout)) {
				OpenPort op = new OpenPort();
				op.setPort(port);
				op.setScanId(scanId);
				openPorts.add(op);
			}
		}
		return openPorts;
	}

}
