package com.cyber.difenda.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cyber.difenda.model.Assessment;
import com.cyber.difenda.model.DnsRecord;
import com.cyber.difenda.service.AssessmentService;
import com.cyber.difenda.service.DnsService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

	private final DnsService dnsService;
	
	public DashboardController(DnsService dnsService) {
		this.dnsService = dnsService;
    }
	
	@GetMapping("/scan/{scanId}/dns")
    public Map<String, List<String>> getDNSData(@PathVariable String scanId) {
        List<DnsRecord> records = dnsService.findAllDNSforScanId(Long.parseLong(scanId));

        return records.stream().collect(
            Collectors.groupingBy(
                DnsRecord::getRecordType,
                Collectors.mapping(DnsRecord::getRecordValue, Collectors.toList())
            )
        );
    }

}
