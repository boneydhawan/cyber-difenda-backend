package com.cyber.difenda.controller;

import com.cyber.difenda.model.Assessment;
import com.cyber.difenda.repository.AssessmentRepository;
import com.cyber.difenda.service.AssessmentService;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
    public List<Assessment> getAllAssessment() {
        return assessmentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
    
    @GetMapping("/{id}")
    public Assessment getAssessmentById(@PathVariable Long id) throws Exception {
        return assessmentService.getAssessmentById(id);
    }

    @PostMapping
    public Assessment createAssessment(@RequestBody Assessment assessment) throws Exception {
        return assessmentService.createAssessment(assessment);
    }
}
