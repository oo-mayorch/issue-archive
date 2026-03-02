package com.issueresolution.issue_archive.exception;

public class IssueNotFoundException extends RuntimeException{

  public IssueNotFoundException(Long issueId){
    super("Issue not found with id: " + issueId);
  }

}
