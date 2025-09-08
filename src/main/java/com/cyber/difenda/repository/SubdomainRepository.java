package com.cyber.difenda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyber.difenda.model.Subdomain;

public interface SubdomainRepository extends JpaRepository<Subdomain, Long> {

	List<Subdomain> findByScanId(Long scanId);
}