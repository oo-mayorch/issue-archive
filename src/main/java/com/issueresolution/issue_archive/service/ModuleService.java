package com.issueresolution.issue_archive.service;
import com.issueresolution.issue_archive.dto.ModuleDTO;
import java.util.List;

public interface ModuleService {

    Module findOrCreateModule(String moduleName);

    List<ModuleDTO> getAllModules();
    List<String> getModuleNamesForAutoComplete(String prefix);
}
