package com.issueresolution.issue_archive.dto;

import com.issueresolution.issue_archive.enums.IssueType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IssueUpdateRequestDTO {


    @Size(max = 255, message = "Cannot exceed 255 characters")
    private String title;

    @Size(max = 3000, message = "Description too long")
    private String description;

    private IssueType issueType;

    private List<String> moduleNames;

    @Size(max = 3000, message = "Root Cause too long")
    private String rootCause;

    @Size(max = 100, message = "Cannot exceed 255 characters")
    private String team;

    @Size(max = 255, message = "Linked task too long")
    private String linkedTask;
}
