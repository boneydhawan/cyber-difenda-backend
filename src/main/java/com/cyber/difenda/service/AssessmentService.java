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
import com.cyber.difenda.model.Scan;
import com.cyber.difenda.repository.AssessmentRepository;
import com.cyber.difenda.repository.DnsRecordRepository;
import com.cyber.difenda.repository.ScanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssessmentService {
	
	private final AssessmentRepository assessmentRepository;
	
	private final ScanRepository scanRepository;
	
	private final DnsService dnsService;
	private final DnsRecordRepository dnsRecordRepository;
	
	public Assessment createAssessment(Assessment newAssessment) throws Exception {
		Random rand = new Random();
		
		newAssessment.setActive(true);
		newAssessment.setIssues(rand.nextInt(10)+"");
		newAssessment.setLastScan(rand.nextInt(10) + "h ago");
		newAssessment.setStatus("Warning");
        return assessmentRepository.save(newAssessment);
    }
	
	public Assessment getAssessmentById(Long id) throws Exception {
		Optional<Assessment> assessment = assessmentRepository.findById(id);
		Optional<Scan> scan = scanRepository.findTopByAssessmentIdOrderByScanDateDesc(id);
		Assessment assessmentRes = assessment.get();
		if(scan.isPresent()) {
			assessmentRes.setLatestScanId(scan.get().getId());
		}
        return assessmentRes;
    }
	
	public List<Assessment> findAllAssessment(){
		return assessmentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
	
	public String performScan(Long assessmentId) throws Exception {
		
		Assessment assm = getAssessmentById(assessmentId);
		
		Scan scan = Scan.builder()
		        .assessment(assm)
		        .scanDate(LocalDateTime.now())
		        .status(ScanStatus.RUNNING.name())
		        .build();
		
		scanRepository.save(scan);
		
		List<DnsRecord> dnsRecords = dnsService.fetchDnsRecords(assm.getDomain(), scan);
        dnsRecordRepository.saveAll(dnsRecords);
        scan.setDnsRecords(dnsRecords);

        scan.setStatus(ScanStatus.COMPLETED.name());
		return "Completed";
	}

}
