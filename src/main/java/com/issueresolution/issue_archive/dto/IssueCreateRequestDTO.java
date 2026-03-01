package com.issueresolution.issue_archive.dto;

import com.issueresolution.issue_archive.enums.IssueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IssueCreateRequestDTO {

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 500, message = "Cannot exceed 255 characters")
    private String title;

    @NotBlank
    @Size(max = 3000, message = "Description too long")
    private String description;

    @NotNull(message = "Issue type is required")
    private IssueType issueType;

    private List<String> moduleNames;  // NOT moduleId

    @Size(max = 3000, message = "Root Cause too long")
    private String rootCause;

    @Size(max = 255, message = "Cannot exceed 255 characters")
    private String team;

    @Size(max = 500)
    private String linkedTask;


}
