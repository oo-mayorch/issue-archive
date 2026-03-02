package com.issueresolution.issue_archive.controller;
import com.issueresolution.issue_archive.entity.User;
import com.issueresolution.issue_archive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * UserController
 * Purpose: Handle user-related API endpoints.
 * Endpoints:
 * - GET /api/users/me - Get current authenticated user's details
 */

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current authenticated user's details
     *
     * Endpoint: GET /api/users/me
     * Auth: Required (uses Spring Security context)
     *
     * Returns:
     * - username: User's username
     * - name: User's display name
     * - role: User's role (e.g., "Programmer", "Software Engineer")
     *
     * @return ResponseEntity with user info map
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser() {
        // Get the authenticated user's username from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch user from database
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Build response map (don't send password!)
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("name", user.getName());
        userInfo.put("role", user.getRole() != null ? user.getRole() : "User");

        return ResponseEntity.ok(userInfo);
    }

}
