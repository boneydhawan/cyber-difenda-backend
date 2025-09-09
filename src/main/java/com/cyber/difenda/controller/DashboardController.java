package com.cyber.difenda.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cyber.difenda.model.DnsRecord;
import com.cyber.difenda.model.EmailSecurity;
import com.cyber.difenda.model.ScanTlsSecurity;
import com.cyber.difenda.service.ScanService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

	private final ScanService scanService;
	
	public DashboardController(ScanService dnsService) {
		this.scanService = dnsService;
    }
	
	@GetMapping("/scan/{scanId}/dns")
    public Map<String, List<String>> getDNSData(@PathVariable String scanId) {
        List<DnsRecord> records = scanService.findAllDNSforScanId(Long.parseLong(scanId));

        return records.stream().collect(
            Collectors.groupingBy(
                DnsRecord::getRecordType,
                Collectors.mapping(DnsRecord::getRecordValue, Collectors.toList())
            )
        );
    }
	
	@GetMapping("/scan/{scanId}/networkSecurity")
    public ScanTlsSecurity getTlsSecurity(@PathVariable Long scanId) {
        ScanTlsSecurity result = scanService.getTlsSecurityForScan(scanId);
        result.setOpenPorts(scanService.getOpenPortsForScan(scanId));
        result.setSubdomains(scanService.getSubdomainsForScan(scanId));
        return result;
    }
	
	@GetMapping("/scan/{scanId}/emailSecurity")
    public EmailSecurity getEmailSecurity(@PathVariable Long scanId) {
		EmailSecurity emailSecurity = scanService.getEmailSecurityForScan(scanId);
       
        return emailSecurity;
    }

}
