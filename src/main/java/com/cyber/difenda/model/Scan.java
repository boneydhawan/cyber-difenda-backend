package com.cyber.difenda.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "scans")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scan extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    private LocalDateTime scanDate;
    private String status;

    @Column(columnDefinition = "TEXT")
    private String summaryFindings;

}
