package com.cyber.difenda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyber.difenda.model.Assessment;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
}
