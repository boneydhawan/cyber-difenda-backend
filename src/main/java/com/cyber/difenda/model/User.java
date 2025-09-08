package com.cyber.difenda.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data                // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor   // No-args constructor
@AllArgsConstructor  // All-args constructor
@Builder             // Builder pattern
public class User extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uid; // Firebase UID

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;
    private String lastName;
    
    private Boolean isActive;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

}
