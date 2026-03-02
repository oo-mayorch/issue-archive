package com.issueresolution.issue_archive.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Represents a module or component of the project.
 * Modules are created dynamically when users assign them to issues.

 * Examples: "Authentication", "Database", "Payment Gateway", "UI Components"

 * Business Rules:
 * - Module names must be unique (no duplicates)
 * - Modules are NOT predefined - created on-the-fly
 * - Timestamp tracks when module was first created
 */

@Entity
@Getter
@Setter
@Table(name = "modules")
@NoArgsConstructor
@AllArgsConstructor
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Long moduleId;


    @Column(nullable = false, unique = true, length = 200)
    private String moduleName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
