package com.issueresolution.issue_archive.exception;

/**
 * Exception thrown when attempting to perform an operation on an issue
 * that is not in the correct state for that operation.
 *
 * Examples:
 * - Trying to resolve an already RESOLVED issue
 * - Trying to mark a RESOLVED issue as INVALID
 * - Trying to update an INVALID issue
 */
public class InvalidIssueStateException extends RuntimeException {

    public InvalidIssueStateException(String message) {
      super(message);
    }
}