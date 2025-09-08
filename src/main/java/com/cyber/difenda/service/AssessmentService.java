package com.cyber.difenda.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cyber.difenda.enums.ScanStatus;
import com.cyber.difenda.model.Assessment;
import com.cyber.difenda.model.DnsRecord;
import com.cyber.difenda.model.OpenPort;
import com.cyber.difenda.model.Scan;
import com.cyber.difenda.model.ScanTlsSecurity;
import com.cyber.difenda.model.Subdomain;
import com.cyber.difenda.repository.AssessmentRepository;
import com.cyber.difenda.repository.DnsRecordRepository;
import com.cyber.difenda.repository.OpenPortRepository;
import com.cyber.difenda.repository.ScanRepository;
import com.cyber.difenda.repository.ScanTlsSecurityRepository;
import com.cyber.difenda.repository.SubdomainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssessmentService {
	
	private final AssessmentRepository assessmentRepository;
	private final DnsRecordRepository dnsRecordRepository;
	private final ScanRepository scanRepository;
	private final OpenPortRepository openPortRepository;
	private final ScanTlsSecurityRepository scanTlsSecurityRepository;
	private final SubdomainRepository subdomainRepository;
	
	private final ScanService scanService;
	
	public Assessment createAssessment(Assessment newAssessment) throws Exception {
		Random rand = new Random();
		
		newAssessment.setActive(true);
		newAssessment.setIssues(rand.nextInt(10)+"");
        return assessmentRepository.save(newAssessment);
    }
	
	public Assessment getAssessmentById(Long id) throws Exception {
		Optional<Assessment> assessment = assessmentRepository.findById(id);
		Optional<Scan> scan = scanRepository.findTopByAssessmentIdOrderByScanDateDesc(id);
		Assessment assessmentRes = assessment.get();
		if(scan.isPresent()) {
			assessmentRes.setLatestScanId(scan.get().getId());
			assessmentRes.setLastScanDate(scan.get().getScanDate());
			assessmentRes.setStatus(scan.get().getStatus());
		}
        return assessmentRes;
    }
	
	public List<Assessment> findAllAssessment(){
		return assessmentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
	
	public void performScan(Long assessmentId) throws Exception {
		
		Assessment assm = getAssessmentById(assessmentId);
		
		Scan scan = Scan.builder()
		        .assessment(assm)
		        .scanDate(LocalDateTime.now())
		        .status(ScanStatus.RUNNING.name())
		        .build();
		
		scan = scanRepository.save(scan);
		Long scanId = scan.getId();
		
		List<DnsRecord> dnsRecords = scanService.scanDnsRecords(assm.getDomain(), scanId);
        dnsRecordRepository.saveAll(dnsRecords);
        
        ScanTlsSecurity tls = scanService.scanTlsSecurity(assm.getDomain(), scanId);
        scanTlsSecurityRepository.save(tls);  
        
        List<OpenPort> openPorts = PortScanner.scanPorts(assm.getDomain(), 200, scanId);
        openPortRepository.saveAll(openPorts);
        
        List<Subdomain> subDomains = SubdomainFetcher.fetchSubdomains(assm.getDomain(), scanId);
        subdomainRepository.saveAll(subDomains);
       
        
        scan.setStatus(ScanStatus.COMPLETED.name());
        
        scanRepository.save(scan);
	}

}
