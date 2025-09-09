package com.cyber.difenda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyber.difenda.model.EmailSecurity;

public interface EmailSecurityRepository extends JpaRepository<EmailSecurity, Long> {

	Optional<EmailSecurity> findByScanId(Long scanId);
}
