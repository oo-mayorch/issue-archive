package com.issueresolution.issue_archive.dto;

import com.issueresolution.issue_archive.enums.IssueSource;
import com.issueresolution.issue_archive.enums.IssueStatus;
import com.issueresolution.issue_archive.enums.IssueType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class IssueResponseDTO {

    private Long issueId;
    private String title;
    private String description;
    private IssueStatus issueStatus;
    private IssueType issueType;
    private IssueSource issueSource;

    private LocalDateTime reportedDate;
    private LocalDateTime dateResolved;

    private String invalidReason;
    private LocalDateTime invalidatedAt;
    private Long invalidatedById;
    private String invalidatedByUserName;


    private String team;
    private String linkedTask;
    private String rootCause;

    private String resolutionSummary;
    private String prevention;


    private Long reportedById;
    private String reportedByUserName;
    private Long resolvedById;
    private String resolvedByUserName;


    private List<Long> moduleIds;
    private List<String> moduleNames;
}
