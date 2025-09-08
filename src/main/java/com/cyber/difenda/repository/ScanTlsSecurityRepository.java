package com.cyber.difenda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyber.difenda.model.ScanTlsSecurity;

public interface ScanTlsSecurityRepository extends JpaRepository<ScanTlsSecurity, Long> {
    Optional<ScanTlsSecurity> findByScanId(Long scanId);
}
