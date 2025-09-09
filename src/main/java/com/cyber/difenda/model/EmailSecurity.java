package com.cyber.difenda.model;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_security")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSecurity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scan_id")
    private Long scanId;

    @Column(columnDefinition = "TEXT")
    private String spfRecord;

    private String dkimSelector;

    @Column(columnDefinition = "TEXT")
    private String dmarcRecord;

    private String adkim;
    private String aspf;
    private String rua;
    private String mtaSts;
    private String tlsRpt;

    // MX records stored as JSON string
    @Column(columnDefinition = "TEXT")
    private String mxRecords;  

    // SPF includes stored as JSON string
    @Column(columnDefinition = "TEXT")
    private String spfIncludes; 
    
    @Column(columnDefinition = "TEXT")
    private String findings; 
    
    public List<String> getFindings() {
    	if (findings == null || findings.isBlank()) {
            return List.of();
        }
        return Arrays.stream(findings.split(","))
                     .map(String::trim)
                     .toList();
    }

}
