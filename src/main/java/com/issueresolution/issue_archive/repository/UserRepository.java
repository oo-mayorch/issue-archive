package com.issueresolution.issue_archive.repository;

import com.issueresolution.issue_archive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>  {

    // Find user by username
    Optional<User> findByUsername (String username);

    // Find all active users
    List<User> findByIsActive(Boolean isActive);
}
