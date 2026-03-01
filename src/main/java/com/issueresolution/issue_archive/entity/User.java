package com.issueresolution.issue_archive.entity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * Username for login - must be unique
     */

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /**
     * Hashed password for authentication
     * NEVER store plain text passwords!
     * Will be hashed using BCrypt in Service layer
     */

    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Display name of the user (e.g., "Juan Dela Cruz")
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * User's role in the team (e.g., "Programmer", "Software Engineer")
     * Optional field
     */

    @Column(length = 255)
    private String role;

    @Column(nullable = false)
    private Boolean isActive = true;

}


