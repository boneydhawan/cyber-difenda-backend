package com.cyber.difenda.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.cyber.difenda.model.Assessment;
import com.cyber.difenda.model.User;
import com.cyber.difenda.repository.AssessmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssessmentService {
	
	private final AssessmentRepository assessmentRepository;
	
	public Assessment createAssessment(Assessment newAssessment) throws Exception {
		Random rand = new Random();
		
		newAssessment.setActive(true);
		newAssessment.setIssues(rand.nextInt(10)+"");
		newAssessment.setLastScan(rand.nextInt(10) + "h ago");
		newAssessment.setStatus("Warning");
        return assessmentRepository.save(newAssessment);
    }

}
