package com.issueresolution.issue_archive.controller;

import com.issueresolution.issue_archive.dto.*;
import com.issueresolution.issue_archive.entity.User;
import com.issueresolution.issue_archive.enums.IssueStatus;
import com.issueresolution.issue_archive.exception.UserNotFoundException;
import com.issueresolution.issue_archive.repository.UserRepository;
import com.issueresolution.issue_archive.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final UserRepository userRepository;

    /**
     * POST /api/issues - Create a new issue
     * Authenticated user becomes the reporter
     */
    @PostMapping
    public ResponseEntity<IssueResponseDTO> createIssue(
            @Valid @RequestBody IssueCreateRequestDTO issueCreateRequestDTO) {

        Long userId = getAuthenticatedUserId();
        IssueResponseDTO createdIssue = issueService.createIssue(issueCreateRequestDTO, userId);

        return new ResponseEntity<>(createdIssue, HttpStatus.CREATED);
    }


    /**
     * GET /api/issues/{id} - Get a specific issue by ID
     * Allows viewing INVALID issues for archive/record keeping
     */
    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseDTO> getIssueById(@PathVariable Long id) {

        IssueResponseDTO issue = issueService.getIssueById(id);

        return ResponseEntity.ok(issue);
    }

    /**
     * GET /api/issues - Get all issues (excludes INVALID issues)
     * INVALID issues are soft-deleted and only shown in archive
     */
    @GetMapping
    public ResponseEntity<List<IssueResponseDTO>> getAllIssues() {

        List<IssueResponseDTO> issues = issueService.getAllIssues();

        return ResponseEntity.ok(issues);
    }
    /**
     * GET /api/issues/status/{status} - Get all issues by status
     * status can be: OPEN, RESOLVED, or INVALID
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<IssueResponseDTO>> getAllIssuesByStatus(
            @PathVariable IssueStatus status) {

        List<IssueResponseDTO> issues = issueService.getAllIssuesByStatus(status);

        return ResponseEntity.ok(issues);
    }

    /**
     * GET /api/issues/invalid - Get all invalid issues (archive view)
     * Shows soft-deleted issues for record keeping
     */
    @GetMapping("/invalid")
    public ResponseEntity<List<IssueResponseDTO>> getAllInvalidIssues() {

        List<IssueResponseDTO> invalidIssues = issueService.getAllInvalidIssues();

        return ResponseEntity.ok(invalidIssues);
    }

    /**
     * PUT /api/issues/{id} - Update an issue
     * Business rules:
     * - OPEN issues: Can update all fields
     * - RESOLVED issues: Can only update description (non-empty)
     * - INVALID issues: Cannot update at all
     */
    @PutMapping("/{id}")
    public ResponseEntity<IssueResponseDTO> updateIssue(
            @PathVariable Long id,
            @Valid @RequestBody IssueUpdateRequestDTO issueUpdateRequestDTO){

        IssueResponseDTO updatedIssue = issueService.updateIssue(issueUpdateRequestDTO, id);

        return ResponseEntity.ok(updatedIssue);
    }

    /**
     * PUT /api/issues/{id}/resolve - Mark an issue as resolved
     * Business rules:
     * - Only OPEN issues can be resolved
     * - Authenticated user becomes the resolver
     * - dateResolved is set automatically
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<IssueResponseDTO> resolveIssue(
            @PathVariable Long id,
            @Valid @RequestBody IssueResolveRequestDTO issueResolveRequestDTO) {

        // Extract authenticated user's ID (the resolver)
        Long resolvedById = getAuthenticatedUserId();

        IssueResponseDTO resolvedIssue = issueService.resolveIssue(issueResolveRequestDTO, id, resolvedById);

        return ResponseEntity.ok(resolvedIssue);
    }

    /**
     * PUT /api/issues/{id}/invalid - Mark an issue as invalid (soft delete)
     * Business rules:
     * - Only OPEN issues can be marked as invalid
     * - Authenticated user becomes the invalidator
     * - Requires a reason for invalidation
     * - invalidatedAt is set automatically
     */
    @PutMapping("/{id}/invalid")
    public ResponseEntity<IssueResponseDTO> markIssueAsInvalid(
            @PathVariable Long id,
            @Valid @RequestBody IssueInvalidRequestDTO issueInvalidRequestDTO) {

        // Extract authenticated user's ID (the invalidator)
        Long userId = getAuthenticatedUserId();

        IssueResponseDTO invalidatedIssue = issueService.markIssueAsInvalid(id, issueInvalidRequestDTO, userId);

        return ResponseEntity.ok(invalidatedIssue);
    }


    /**
     * Helper method to extract authenticated user's ID from Security Context
     */
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication exists and user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found: " + username));

        return user.getUserId();
    }


}
