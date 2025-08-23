package com.cyber.difenda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyber.difenda.model.Domain;

public interface DomainRepository extends JpaRepository<Domain, Long> {
}
