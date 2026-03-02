package com.issueresolution.issue_archive.service;

import com.issueresolution.issue_archive.dto.*;
import com.issueresolution.issue_archive.enums.IssueStatus;

import java.util.List;

/**
 * Service layer for Issue-related business operations.
 * This layer contains business rules and validations.
 * Controllers must NOT access repositories directly.
 */

public interface IssueService {

    /**
     * Create a new issue.
     * Business rules:
     * - Issue status must start as OPEN
     * - reportedDate is set automatically
     */

    IssueResponseDTO createIssue(IssueCreateRequestDTO issueCreateRequestDTO, Long userId);

    /**
     * Retrieve an issue by its ID.
     */
    IssueResponseDTO getIssueById(Long issueId);

    /**
     * Retrieve all issues.
     */
    List<IssueResponseDTO> getAllIssues();

    /**
     * Retrieve issues filtered by status (OPEN / RESOLVED).
     */
    List<IssueResponseDTO> getAllIssuesByStatus(IssueStatus issueStatus);

    /**
     * Mark an issue as resolved.
     * Business rules:
     * - Only OPEN issues can be resolved
     * - dateResolved must be set
     */
    IssueResponseDTO resolveIssue(IssueResolveRequestDTO issueResolveRequestDTO, Long issueId, Long resolvedById);

    /**
     * Update Issue
     */
    IssueResponseDTO updateIssue(IssueUpdateRequestDTO issueUpdateRequestDTO, Long issueId);

    /**
     * Marked an issue as invalid in case of false alarm
     */
    IssueResponseDTO markIssueAsInvalid(Long issueId, IssueInvalidRequestDTO requestDTO, Long userId);

    /**
     * Retrieve all invalid (soft-deleted) issues for archive view.
     */
    List<IssueResponseDTO> getAllInvalidIssues();
}
