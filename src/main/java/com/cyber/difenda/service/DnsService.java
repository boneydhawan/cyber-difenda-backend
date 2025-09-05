package com.cyber.difenda.service;

import org.springframework.stereotype.Service;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import com.cyber.difenda.enums.DnsConstants;
import com.cyber.difenda.model.DnsRecord;
import com.cyber.difenda.model.Scan;
import com.cyber.difenda.repository.DnsRecordRepository;

import lombok.RequiredArgsConstructor;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DnsService {
	
	private final DnsRecordRepository dnsRecordRepository;
	
	public List<DnsRecord> findAllDNSforScanId(Long scanId){
		return dnsRecordRepository.findByScan_Id(scanId);
	}
	
	public List<DnsRecord> fetchDnsRecords(String domain, Scan scan) {
        List<DnsRecord> dnsRecords = new ArrayList<>();

        for (String type : DnsConstants.RECORD_TYPES) {
            try {
                Record[] records = new Lookup(domain, Type.value(type)).run();
                if (records != null) {
                    for (Record r : records) {
                        DnsRecord dnsRecord = new DnsRecord();
                        dnsRecord.setScan(scan);
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
            Lookup lookup = new Lookup(domain, type);
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
}
