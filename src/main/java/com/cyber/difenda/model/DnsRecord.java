package com.cyber.difenda.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dns_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DnsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scan_id")
    private Scan scan;

    private String recordType; // A, AAAA, NS, MX, TXT, SOA

    private String recordValue;

}