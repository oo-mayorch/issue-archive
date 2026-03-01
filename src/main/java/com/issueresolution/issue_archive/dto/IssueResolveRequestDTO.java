package com.issueresolution.issue_archive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueResolveRequestDTO {


    @NotBlank(message = "Resolution summary cannot be blank")
    @Size(max = 3000, message = "Summary too long")
    private String resolutionSummary;

    @Size(max = 3000, message = "Prevention too long")
    private String prevention;
}
