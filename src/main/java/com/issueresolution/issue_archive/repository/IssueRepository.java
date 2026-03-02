package com.issueresolution.issue_archive.repository;

import com.issueresolution.issue_archive.entity.Issue;
import com.issueresolution.issue_archive.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByIssueStatus(IssueStatus issueStatus);
    List<Issue> findByIssueStatusNot(IssueStatus issueStatus);
    List<Issue> findByTeam(String team);
    List<Issue> findByLinkedTask(String linkedTask);
}
