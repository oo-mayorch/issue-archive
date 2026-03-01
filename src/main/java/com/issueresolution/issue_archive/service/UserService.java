package com.issueresolution.issue_archive.service;

import com.issueresolution.issue_archive.entity.User;
import com.issueresolution.issue_archive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * UserService
 * Purpose: Handle user-related business logic
 * Methods:
 * - findByUsername: Get user by username
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Find user by username
     *
     * @param username - Username to search for
     * @return Optional<User> - User if found, empty if not
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}