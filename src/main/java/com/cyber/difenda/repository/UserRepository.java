package com.cyber.difenda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cyber.difenda.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("select u from User u where lower(u.email) = :email")
	Optional<User> findByEmail(String email);
}
