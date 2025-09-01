package com.cyber.difenda.controller;

import com.cyber.difenda.model.Assessment;
import com.cyber.difenda.repository.AssessmentRepository;
import com.cyber.difenda.service.AssessmentService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessments")
public class AssessmentController extends BaseController {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentRepository assessmentRepository,
    		AssessmentService assessmentService) {
        this.assessmentRepository = assessmentRepository;
        this.assessmentService = assessmentService;
    }

    @GetMapping
    public List<Assessment> getAllDomains() {
        return assessmentRepository.findAll();
    }

    @PostMapping
    public Assessment createAssessment(@RequestBody Assessment assessment) throws Exception {
        return assessmentService.createAssessment(assessment);
    }
}
