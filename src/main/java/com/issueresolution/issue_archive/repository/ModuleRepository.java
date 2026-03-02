package com.issueresolution.issue_archive.repository;


import com.issueresolution.issue_archive.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    //Find module by name
    Optional<Module> findByModuleName(String moduleName);

    // Check if module exists by name
    boolean existsByModuleName(String moduleName);
}
