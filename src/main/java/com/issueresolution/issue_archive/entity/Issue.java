package com.issueresolution.issue_archive.entity;

import com.issueresolution.issue_archive.enums.IssueSource;
import com.issueresolution.issue_archive.enums.IssueStatus;
import com.issueresolution.issue_archive.enums.IssueType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "issue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long issueId;

    // ========== BASIC INFORMATION ==========

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ========== CATEGORIZATION ==========

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private IssueStatus issueStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 300)
    private IssueType issueType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private IssueSource issueSource;

    // ========== TIMESTAMPS ==========

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime reportedDate;

    @Column
    private LocalDateTime dateResolved;

    // ========== OPTIONAL INFORMATION ==========
    @Column(length = 100)
    private String team;

    @Column(length = 500)
    private String linkedTask;

    @Column(columnDefinition = "TEXT")
    private String rootCause;

    // ========== RESOLUTION DETAILS ==========

    @Column(columnDefinition = "TEXT")
    private String resolutionSummary;

    @Column(columnDefinition = "TEXT")
    private String prevention;


    // ========== FOR MARKING ISSUE AS INVALID ==========

    @Column(length = 2000)
    private String invalidReason;

    @Column
    private LocalDateTime invalidatedAt;


    // ========== FOR RELATIONSHIPS MODULE ID AND RESOLVED BY USER==========


    // ========== USER RELATIONSHIPS ==========

    @ManyToOne
    @JoinColumn(name = "invalidated_by")
    private User invalidatedBy;

    @ManyToOne
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @ManyToOne
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;

    // ========== MODULE RELATIONSHIP (Many-to-Many) ==========
    @ManyToMany
    @JoinTable(
            name = "issue_module",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id")
    )
    private List<Module> modules = new ArrayList<>();




}
