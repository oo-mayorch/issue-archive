package com.issueresolution.issue_archive.service.impl;

import com.issueresolution.issue_archive.dto.*;
import com.issueresolution.issue_archive.entity.*;
import com.issueresolution.issue_archive.entity.Module;
import com.issueresolution.issue_archive.enums.IssueSource;
import com.issueresolution.issue_archive.enums.IssueStatus;
import com.issueresolution.issue_archive.exception.*;
import com.issueresolution.issue_archive.repository.*;
import com.issueresolution.issue_archive.service.IssueService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    @Transactional
    @Override
    public IssueResponseDTO createIssue(IssueCreateRequestDTO issueCreateRequestDTO, Long userId) {

        // 1. Fetch the user/reporter
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. User reports the issue and fills up the important details
        Issue issue = new Issue();

        issue.setTitle(issueCreateRequestDTO.getTitle());
        issue.setDescription(issueCreateRequestDTO.getDescription());
        issue.setIssueType(issueCreateRequestDTO.getIssueType());
        issue.setIssueStatus(IssueStatus.OPEN);  // Automatically sets status to open
        issue.setIssueSource(IssueSource.MANUAL);  // Automatically sets source to manual
        issue.setRootCause(issueCreateRequestDTO.getRootCause());
        issue.setTeam(issueCreateRequestDTO.getTeam());
        issue.setLinkedTask(issueCreateRequestDTO.getLinkedTask());
        issue.setReportedBy(reporter);

        // 3 Module handling (Adding existing modules or creating new ones)
        List<Module> modules = Optional.ofNullable(issueCreateRequestDTO.getModuleNames())
                .orElse(Collections.emptyList())
                .stream()
                .distinct()
                .map(name -> moduleRepository.findByModuleName(name)
                        .orElseGet(() -> {
                            Module newModule = new Module();
                            newModule.setModuleName(name);
                            return moduleRepository.save(newModule);
                        }))
                .collect(Collectors.toList());

        issue.setModules(modules);

        // 4. Save Issue
        Issue savedIssue = issueRepository.save(issue);

        // 5. Map to Response DTO and return
        return mapToResponseDTO(savedIssue);

    }

    @Transactional(readOnly = true)  // ← CHANGE: Added transaction for lazy loading
    @Override
    public IssueResponseDTO getIssueById(Long issueId) {
        // Allows viewing INVALID issues for archive/record keeping
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        return mapToResponseDTO(issue);
    }

    @Transactional(readOnly = true)  // ← CHANGE: Added transaction for lazy loading
    @Override
    public List<IssueResponseDTO> getAllIssues() {
        // Fetch all non-invalid issues and map to DTOs
        return issueRepository.findByIssueStatusNot(IssueStatus.INVALID)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)  // ← CHANGE: Added transaction for lazy loading
    @Override
    public List <IssueResponseDTO> getAllIssuesByStatus(IssueStatus issueStatus) {
        return issueRepository.findByIssueStatus(issueStatus)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)  // ← CHANGE: Added transaction for lazy loading
    @Override
    public List<IssueResponseDTO> getAllInvalidIssues() {
        // Fetch only invalid issues for archive view
        return issueRepository.findByIssueStatus(IssueStatus.INVALID)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public IssueResponseDTO resolveIssue(IssueResolveRequestDTO issueResolveRequestDTO, Long issueId, Long resolvedById) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        // ← CHANGE: Now uses consistent custom exception
        if (issue.getIssueStatus() != IssueStatus.OPEN) {
            throw new InvalidIssueStateException(
                    "Cannot resolve issue #" + issueId +
                            ". Only OPEN issues can be resolved. Current status: " + issue.getIssueStatus()
            );
        }

        User resolver = userRepository.findById(resolvedById)
                .orElseThrow(() -> new UserNotFoundException(resolvedById));

        issue.setIssueStatus(IssueStatus.RESOLVED);
        issue.setDateResolved(LocalDateTime.now());
        issue.setResolutionSummary(issueResolveRequestDTO.getResolutionSummary());
        issue.setPrevention(issueResolveRequestDTO.getPrevention());
        issue.setResolvedBy(resolver);

        Issue saved = issueRepository.save(issue);

        return mapToResponseDTO(saved);
    }


    @Transactional
    @Override
    public IssueResponseDTO updateIssue(IssueUpdateRequestDTO issueUpdateRequestDTO, Long issueId){
        // 1. Find issue
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        // 2. Prevent updates on invalid (soft-deleted) issues
        // ← CHANGE: Now uses consistent custom exception
        if (issue.getIssueStatus() == IssueStatus.INVALID) {
            throw new InvalidIssueStateException(
                    "Cannot update issue #" + issueId + ". Issue is marked as INVALID (archived)."
            );
        }

        // 3. Check if issue is resolved and enforce restrictions
        if (issue.getIssueStatus() == IssueStatus.RESOLVED) {
            // Check if trying to update non-description fields
            boolean hasNonDescriptionUpdates = issueUpdateRequestDTO.getTitle() != null
                    || issueUpdateRequestDTO.getIssueType() != null
                    || issueUpdateRequestDTO.getTeam() != null
                    || issueUpdateRequestDTO.getLinkedTask() != null
                    || issueUpdateRequestDTO.getRootCause() != null  // ← CHANGE: Added rootCause check
                    || issueUpdateRequestDTO.getModuleNames() != null;

            if (hasNonDescriptionUpdates) {
                throw new InvalidIssueStateException(
                        "Only description updates are allowed for resolved issues. Issue #" + issueId + " is RESOLVED."
                );
            }

            // Prevent removing/clearing description
            if (issueUpdateRequestDTO.getDescription() != null) {
                // ← CHANGE: Better validation message
                if (issueUpdateRequestDTO.getDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "Description cannot be empty for resolved issues"
                    );
                }
                issue.setDescription(issueUpdateRequestDTO.getDescription());
            }

            Issue savedIssue = issueRepository.save(issue);
            return mapToResponseDTO(savedIssue);
        }

        // 4. For OPEN issues, update all provided fields with validation
        // ← CHANGE: Added validation for empty strings
        if (issueUpdateRequestDTO.getTitle() != null) {
            if (issueUpdateRequestDTO.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            issue.setTitle(issueUpdateRequestDTO.getTitle());
        }

        if (issueUpdateRequestDTO.getDescription() != null) {
            if (issueUpdateRequestDTO.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("Description cannot be empty");
            }
            issue.setDescription(issueUpdateRequestDTO.getDescription());
        }

        if (issueUpdateRequestDTO.getIssueType() != null) {
            issue.setIssueType(issueUpdateRequestDTO.getIssueType());
        }

        if (issueUpdateRequestDTO.getTeam() != null) {
            issue.setTeam(issueUpdateRequestDTO.getTeam());
        }

        if (issueUpdateRequestDTO.getRootCause() != null) {
            issue.setRootCause(issueUpdateRequestDTO.getRootCause());
        }

        if (issueUpdateRequestDTO.getLinkedTask() != null) {
            issue.setLinkedTask(issueUpdateRequestDTO.getLinkedTask());
        }

        // 5. Handle modules only if provided
        if (issueUpdateRequestDTO.getModuleNames() != null) {
            List<Module> modules = issueUpdateRequestDTO.getModuleNames()
                    .stream()
                    .distinct()
                    .map(name -> moduleRepository.findByModuleName(name)
                            .orElseGet(() -> {
                                Module newModule = new Module();
                                newModule.setModuleName(name);
                                return moduleRepository.save(newModule);
                            }))
                    .collect(Collectors.toList());

            issue.setModules(modules);
        }

        // 6. Save and return updated issue
        Issue savedIssue = issueRepository.save(issue);
        return mapToResponseDTO(savedIssue);
    }

    @Transactional
    @Override
    public IssueResponseDTO markIssueAsInvalid(Long issueId, IssueInvalidRequestDTO requestDTO, Long userId) {
        // 1. Find issue
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        // 2. Only allow marking OPEN issues as invalid
        // ← CHANGE: Now uses consistent custom exception with better message
        if (issue.getIssueStatus() != IssueStatus.OPEN) {
            throw new InvalidIssueStateException(
                    "Cannot mark issue #" + issueId + " as invalid. " +
                            "Only OPEN issues can be marked as invalid. Current status: " + issue.getIssueStatus()
            );
        }

        // 3. Fetch the user who is marking it as invalid
        User invalidator = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 4. Set invalid fields
        issue.setIssueStatus(IssueStatus.INVALID);
        issue.setInvalidReason(requestDTO.getInvalidReason());
        issue.setInvalidatedAt(LocalDateTime.now());
        issue.setInvalidatedBy(invalidator);

        // 5. Save and return
        Issue savedIssue = issueRepository.save(issue);
        return mapToResponseDTO(savedIssue);
    }

    private IssueResponseDTO mapToResponseDTO(Issue issue){
        IssueResponseDTO issueResponseDTO = new IssueResponseDTO();

        issueResponseDTO.setIssueId(issue.getIssueId());
        issueResponseDTO.setTitle(issue.getTitle());
        issueResponseDTO.setDescription(issue.getDescription());
        issueResponseDTO.setRootCause(issue.getRootCause());
        issueResponseDTO.setIssueStatus(issue.getIssueStatus());
        issueResponseDTO.setIssueType(issue.getIssueType());
        issueResponseDTO.setIssueSource(issue.getIssueSource());
        issueResponseDTO.setTeam(issue.getTeam());
        issueResponseDTO.setLinkedTask(issue.getLinkedTask());
        issueResponseDTO.setReportedDate(issue.getReportedDate());
        issueResponseDTO.setDateResolved(issue.getDateResolved());
        issueResponseDTO.setResolutionSummary(issue.getResolutionSummary());
        issueResponseDTO.setPrevention(issue.getPrevention());

        // ---------- INVALID FIELDS ----------
        issueResponseDTO.setInvalidReason(issue.getInvalidReason());
        issueResponseDTO.setInvalidatedAt(issue.getInvalidatedAt());

        // ---------- USER RELATIONSHIPS ----------
        issueResponseDTO.setReportedById(issue.getReportedBy() != null ? issue.getReportedBy().getUserId() : null);
        issueResponseDTO.setReportedByUserName(issue.getReportedBy() != null ? issue.getReportedBy().getUsername() : null);

        issueResponseDTO.setResolvedById(issue.getResolvedBy() != null ? issue.getResolvedBy().getUserId() : null);
        issueResponseDTO.setResolvedByUserName(issue.getResolvedBy() != null ? issue.getResolvedBy().getUsername() : null);

        issueResponseDTO.setInvalidatedById(issue.getInvalidatedBy() != null ? issue.getInvalidatedBy().getUserId() : null);
        issueResponseDTO.setInvalidatedByUserName(issue.getInvalidatedBy() != null ? issue.getInvalidatedBy().getUsername() : null);

        // ---------- MODULES  ----------
        List<Module> modules = issue.getModules();

        issueResponseDTO.setModuleIds(
                modules.stream()
                        .map(Module::getModuleId)
                        .collect(Collectors.toList())
        );

        issueResponseDTO.setModuleNames(
                modules.stream()
                        .map(Module::getModuleName)
                        .collect(Collectors.toList())
        );

        return issueResponseDTO;
    }
}