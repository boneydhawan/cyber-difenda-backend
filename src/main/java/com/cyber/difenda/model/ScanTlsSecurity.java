package com.cyber.difenda.model;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scan_tls_security")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanTlsSecurity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scan_id")
    private Long scanId;

    private boolean tls_1_0;
    private boolean tls_1_1;
    private boolean tls_1_2;
    private boolean tls_1_3;

    @Column(name = "weak_ciphers")
    private String weakCiphers;
    
    @Column(name = "hsts_max_age")
    private Integer hstsMaxAge;

    @Column(name = "missing_headers")
    private String missingHeaders;
    
    private boolean http_to_https;
    
    @Transient
    private List<OpenPort> openPorts;
    
    @Transient
    private List<Subdomain> subdomains;

    public List<String> getMissingHeaders() {
    	if (missingHeaders == null || missingHeaders.isBlank()) {
            return List.of();
        }
        return Arrays.stream(missingHeaders.split(","))
                     .map(String::trim)
                     .toList();
    }
}
