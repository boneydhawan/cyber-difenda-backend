package com.cyber.difenda.controller;

import com.cyber.difenda.model.Assessment;
import com.cyber.difenda.service.AssessmentService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessments")
public class AssessmentController extends BaseController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService
    		) {
        this.assessmentService = assessmentService;
    }

    @GetMapping
    public List<Assessment> getAllAssessment() {
        return assessmentService.findAllAssessment();
    }
    
    @GetMapping("/{id}")
    public Assessment getAssessmentById(@PathVariable Long id) throws Exception {
        return assessmentService.getAssessmentById(id);
    }

    @PostMapping
    public Assessment createAssessment(@RequestBody Assessment assessment) throws Exception {
    	Assessment as = assessmentService.createAssessment(assessment);
    	assessmentService.performScan(as.getId());
		return as;
    }
    
    @GetMapping("/triggerScan/{assessmentId}")
    public String getDNSAssessment(@PathVariable Long assessmentId) throws Exception {
        return "";//assessmentService.performScan(assessmentId);
    }
}
