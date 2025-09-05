package com.cyber.difenda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyber.difenda.model.Scan;

public interface ScanRepository extends JpaRepository<Scan, Long> {

    Optional<Scan> findTopByAssessmentIdOrderByScanDateDesc(Long assessmentId);

}
