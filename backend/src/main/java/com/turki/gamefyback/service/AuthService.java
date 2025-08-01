package com.turki.gamefyback.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.turki.gamefyback.emailManager.EmailService;
import com.turki.gamefyback.exception.UserNotFoundException;
import com.turki.gamefyback.model.PasswordResetToken;
import com.turki.gamefyback.model.User;
import com.turki.gamefyback.repository.PasswordResetTokenRepository;
import com.turki.gamefyback.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  PasswordResetTokenRepository tokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    // @Autowired
    // private RedisTemplate<String, String> redisTemplate;
    // --- DTOs for password reset requests (can be inner classes or separate files) ---
    public record PasswordResetRequest(String email) {}
    public record VerifyCodeRequest(String email, String code) {}
    public record ResetPasswordRequest(String email, String code, String newPassword) {}


    public void initiatePasswordReset(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email " + userEmail + " not found."));

        // Invalidate any existing tokens for this user
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        // Generate a new token
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 6); // Simple 6-char code
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15); // Token valid for 15 minutes

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiryDate);
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        // Store the token in Redis with the email as key and 30 minutes expiration
        // redisTemplate.opsForValue().set(userEmail, token, 30, TimeUnit.MINUTES);

        // Send email using the new EmailService
        System.out.println("user for email reset : " + user.getEmail());
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), token);
    }
    public boolean verifyPasswordResetCode(String userEmail, String code) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email " + userEmail + " not found."));

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(code);

        if (tokenOpt.isEmpty()) {
            return false; // Token not found
        }

        PasswordResetToken token = tokenOpt.get();

        // Check if token belongs to the user, is not used, and is not expired
        return token.getUser().getId().equals(user.getId())
                && !token.isUsed()
                && !token.isExpired();
    }

    public void resetPassword(String userEmail, String code, String newPassword) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with email " + userEmail + " not found."));

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(code);

        if (tokenOpt.isEmpty() || tokenOpt.get().isUsed() || tokenOpt.get().isExpired() || !tokenOpt.get().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Invalid or expired verification code."); // Use a more specific exception if needed
        }

        PasswordResetToken token = tokenOpt.get();

        // Update user's password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate the token after use
        token.setUsed(true);
        tokenRepository.save(token);
    }
}