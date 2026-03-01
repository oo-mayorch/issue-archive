package com.issueresolution.issue_archive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueInvalidRequestDTO {

    @NotBlank(message = "Reason is required when marking an issue as invalid")
    @Size(max = 2000, message = "Cannot exceed 2000 characters")
    private String invalidReason;
}
