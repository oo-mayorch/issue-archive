package com.issueresolution.issue_archive.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ModuleDTO {

    private Long moduleId;

    private String moduleName;

    private LocalDateTime createdAt;
    
}
