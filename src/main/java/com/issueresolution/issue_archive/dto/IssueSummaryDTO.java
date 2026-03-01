package com.issueresolution.issue_archive.dto;

import com.issueresolution.issue_archive.enums.IssueStatus;
import com.issueresolution.issue_archive.enums.IssueType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueSummaryDTO {

    private Long issueId;

    private String title;
    private IssueStatus issueStatus;
    private IssueType issueType;
    private Long moduleId;
    private String moduleName;
}
