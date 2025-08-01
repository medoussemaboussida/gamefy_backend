package com.turki.gamefyback.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.turki.gamefyback.dto.CreateAdminDTO;
import com.turki.gamefyback.dto.SignupRequestDTO;
import com.turki.gamefyback.dto.UserDTO;
import com.turki.gamefyback.emailManager.EmailService;
import com.turki.gamefyback.model.Role;
import com.turki.gamefyback.model.User;
import com.turki.gamefyback.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.turki.gamefyback.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // Add RedisTemplate
    @Autowired
    private JavaMailSender mailSender;
    private static final String ROLE_CACHE_PREFIX = "user:roles:";
    private static final long CACHE_TTL_MINUTES = 60; // Align with Firebase token expiry

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    public User fromDTO(UserDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return user;
    }

    public User createUser(SignupRequestDTO signupRequest) {
        User user = new User();
        user.setUid(signupRequest.getUid());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        user.setUserName(signupRequest.getUserName());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setRoles(signupRequest.getRoles());
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUid(String uid) {
        return userRepository.findByUid(uid);
    }
    @Cacheable(value = "users", key = "#emailFilter != null ? #emailFilter + '_' + #pageable.pageNumber + '_' + #pageable.pageSize : 'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public List<UserDTO> getAllUsers(String emailFilter, Pageable pageable) {
        List<User> users;
        if (emailFilter != null && !emailFilter.isEmpty()) {
            users = userRepository.findByEmailContainingIgnoreCase(emailFilter, pageable).getContent();
        } else {
            users = userRepository.findAll(pageable).getContent();
        }
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public User updateUser(String id, UserDTO dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
        BeanUtils.copyProperties(dto, existing, "id", "uid", "createdAt", "updatedAt", "password");
        User updatedUser = userRepository.save(existing);
        // Invalidate cache if roles changed
        if (dto.getRoles() != null && !dto.getRoles().equals(existing.getRoles())) {
            redisTemplate.delete(ROLE_CACHE_PREFIX + existing.getUid());
        }
        return updatedUser;
    }
@CacheEvict(value = "users", allEntries = true)
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found for deletion."));
        userRepository.deleteById(id);
        // Invalidate cache
        redisTemplate.delete(ROLE_CACHE_PREFIX + user.getUid());
    }

    public UserDTO updateUserRoles(String userId, Set<Role> newRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        user.setRoles(newRoles);
        User updatedUser = userRepository.save(user);
        // Invalidate cache
        redisTemplate.delete(ROLE_CACHE_PREFIX + user.getUid());
        return toDTO(updatedUser);
    }

    public long countUsersByRole(Role role) {
        return userRepository.countByRolesContaining(role);
    }

    public long countAllUsers() {
        return userRepository.count();
    }


    /////////////oussema methods /////////////////////////////////////////
    private String generateRandomPassword(int length) {
    String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=<>?";
    StringBuilder password = new StringBuilder();
    SecureRandom random = new SecureRandom();
    for (int i = 0; i < length; i++) {
        int randomIndex = random.nextInt(charSet.length());
        password.append(charSet.charAt(randomIndex));
    }
    return password.toString();
}
@CacheEvict(value = "users", allEntries = true)

     public User createAdmin(CreateAdminDTO adminRequest) {
        System.out.println("Creating admin with email: " + adminRequest.getEmail());
        // Generate random password if not provided
        String password = adminRequest.getPassword() != null ? adminRequest.getPassword() : generateRandomPassword(12);
        System.out.println("Generated password for admin " + adminRequest.getEmail() + ": " + password);

        // Create Firebase user
        UserRecord userRecord;
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(adminRequest.getEmail())
                    .setPassword(password)
                    .setDisplayName(adminRequest.getFirstName() + " " + adminRequest.getLastName());
            userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Successfully created Firebase user with UID: " + userRecord.getUid());
        } catch (Exception e) {
            System.err.println("Failed to create Firebase user for email " + adminRequest.getEmail() + ": " + e.getMessage());
            throw new RuntimeException("Failed to create Firebase user: " + e.getMessage(), e);
        }

        // Create new user for MongoDB
        User user = new User();
        user.setUid(userRecord.getUid());
        user.setFirstName(adminRequest.getFirstName());
        user.setLastName(adminRequest.getLastName());
        user.setEmail(adminRequest.getEmail());
        user.setUserName(adminRequest.getUserName());
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(adminRequest.getPhoneNumber());
        Set<Role> adminRoles = adminRequest.getRoles() != null ? adminRequest.getRoles() : new HashSet<>();
        if (adminRoles.isEmpty()) {
            adminRoles.add(Role.ROLE_MANAGER); // Set to ROLE_MANAGER
        }
        user.setRoles(adminRoles);

        // Save to MongoDB
        User savedUser = userRepository.save(user);
        System.out.println("Successfully saved user to MongoDB with UID: " + savedUser.getUid());

        // Send email to admin
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("medoussemaboussida@gmail.com");
            helper.setTo(adminRequest.getEmail());
            helper.setSubject("Welcome to Gamefy Academy - Your Admin Credentials");
            String htmlBody = "<h2>Yoo! " + adminRequest.getUserName() + ",</h2>" +
                    "<p>You have been added as a Manager in Gamefy Academy platform!</p>" +
                    "<p>Your login credentials are:</p>" +
                    "<p><strong>Email:</strong> " + adminRequest.getEmail() + "</p>" +
                    "<p><strong>Password:</strong> " + password + "</p>" +
                    "<p>Thank you,<br>Gamefy Team</p>";
            helper.setText(htmlBody, true);
            mailSender.send(mimeMessage);
            System.out.println("Successfully sent admin email to: " + adminRequest.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send admin email to " + adminRequest.getEmail() + ": " + e.getMessage());
            // Log error but don't throw
        }

        // Return user with plain password for response (avoid in production)
        savedUser.setPassword(password);
        return savedUser;
    }


    public UserDTO getCurrentUser(String firebaseToken) {
        try {
            // Verify Firebase token and get UID
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String uid = decodedToken.getUid();

            // Fetch user from MongoDB by UID
            User user = userRepository.findByUid(uid)
                    .orElseThrow(() -> new UserNotFoundException("User with UID " + uid + " not found."));
            return toDTO(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch current user: " + e.getMessage(), e);
        }
    }

   public UserDTO updateUserConnecter(String id, UserDTO dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
        // Update only the fields provided by the frontend
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(dto.getPhoneNumber());
        // Update password in both MongoDB and Firebase if newPassword is provided
        if (dto.getNewPassword() != null && !dto.getNewPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            try {
                UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(existing.getUid())
                        .setPassword(dto.getNewPassword());
                FirebaseAuth.getInstance().updateUser(request);
                System.out.println("Successfully updated Firebase password for UID: " + existing.getUid());
            } catch (Exception e) {
                System.err.println("Failed to update Firebase password for UID " + existing.getUid() + ": " + e.getMessage());
                throw new RuntimeException("Failed to update Firebase password: " + e.getMessage(), e);
            }
        }
        // Preserve existing roles and other fields not updated by the frontend
        User updatedUser = userRepository.save(existing);
        // Invalidate cache if roles changed (though roles aren't updated here, keep for consistency)
        if (dto.getRoles() != null && !dto.getRoles().equals(existing.getRoles())) {
            redisTemplate.delete(ROLE_CACHE_PREFIX + existing.getUid());
        }
        return toDTO(updatedUser);
    }
    
}