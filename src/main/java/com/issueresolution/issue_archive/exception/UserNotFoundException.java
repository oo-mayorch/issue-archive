package com.issueresolution.issue_archive.exception;

public class UserNotFoundException extends RuntimeException {

  // Constructor for userId (Long)
  public UserNotFoundException(Long userId) {
    super("User not found with id: " + userId);
  }

  // Constructor for username (String)
  public UserNotFoundException(String message) {
    super(message);
  }
}