package com.cyber.difenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "assessment")
public class Assessment extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "domain") 
    @JsonProperty("domain")
    private String domain;
    
    @Column(name = "status") 
    @JsonProperty("status")
    private String status;
    
    @Column(name = "last_scan") 
    @JsonProperty("lastScan")
    private String lastScan;
    
    @Column(name = "organization") 
    @JsonProperty("organization")
    private String organization;
    
    @Column(name = "issues") 
    @JsonProperty("issues")
    private String issues;
    
    @Column(name = "is_active")
    @JsonProperty("isActive")
    private boolean isActive;
    
}