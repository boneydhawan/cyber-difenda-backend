package com.cyber.difenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyber.difenda.model.DnsRecord;

public interface DnsRecordRepository extends JpaRepository<DnsRecord, Long> {

	List<DnsRecord> findByScanId(Long scanId);
}
