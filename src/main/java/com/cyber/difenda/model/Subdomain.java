package com.cyber.difenda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subdomains")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subdomain extends Auditable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scan_id", nullable = false)
    private Long scanId;

    @Column(nullable = false, length = 255)
    private String host;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ips; // comma-separated IPs

    public Subdomain(Long scanId, String host, String ips) {
        this.scanId = scanId;
        this.host = host;
        this.ips = ips;
    }
   
}

