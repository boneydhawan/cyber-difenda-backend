package com.cyber.difenda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyber.difenda.model.OpenPort;
import com.cyber.difenda.model.ScanTlsSecurity;

public interface OpenPortRepository extends JpaRepository<OpenPort, Long> {
	
	List<OpenPort> findByScanId(Long scanId);

}
