package com.turki.gamefyback.repository;

import com.turki.gamefyback.model.Role;
import com.turki.gamefyback.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page; // Add this import
import org.springframework.data.domain.Pageable; // Add this import

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUid(String uid);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);

    // --- NEW METHODS for Pagination and Filtering ---
    Page<User> findAll(Pageable pageable); // Overrides default to return Page
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    // You could add more methods for firstName, lastName etc.
    // Page<User> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    long countByRolesContaining(Role role);
}