package com.turki.gamefyback.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException; // Import for more specific error handling
import com.turki.gamefyback.dto.UserDTO;
import com.turki.gamefyback.model.User;
import com.turki.gamefyback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.turki.gamefyback.service.AuthService; // ADD THIS IMPORT
import com.turki.gamefyback.service.AuthService.PasswordResetRequest; // ADD THIS IMPORT
import com.turki.gamefyback.service.AuthService.ResetPasswordRequest; // ADD THIS IMPORT
import com.turki.gamefyback.service.AuthService.VerifyCodeRequest;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // Initialize Logger

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @PostMapping(path="/login")
    public ResponseEntity<UserDTO> login(@RequestHeader("Authorization") String authorizationHeader) {
        // Log the received authorization header for debugging
        logger.info("Received Authorization header: {}", authorizationHeader != null ? authorizationHeader : "null");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or malformed for login request.");
            return ResponseEntity.status(401).body(null); // Return null body explicitly
        }

        String idToken = authorizationHeader.replace("Bearer ", "");
        FirebaseToken decodedToken;

        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            logger.error("Firebase ID Token verification failed: {}", e.getMessage());
            // Log the stack trace for more detailed debugging
            logger.debug("Firebase ID Token verification failure details:", e);
            return ResponseEntity.status(401).body(null); // Return null body explicitly
        }

        String uid = decodedToken.getUid();
        User user;
        try {
            user = userService.getUserByUid(uid)
                    .orElseThrow(() -> new RuntimeException("User not found in database for UID: " + uid));
        } catch (RuntimeException e) {
            logger.error("User not found in database: {}", e.getMessage());
            logger.debug("User lookup failure details:", e);
            return ResponseEntity.status(401).body(null); // Return null body explicitly
        }

        logger.info("User {} logged in successfully.", user.getEmail());
        return ResponseEntity.ok(userService.toDTO(user));
    }

    // NEW ENDPOINT: Request password reset
    @PostMapping("/forgot-password-request")
    public ResponseEntity<Void> forgotPasswordRequest(@RequestBody PasswordResetRequest request) {
        authService.initiatePasswordReset(request.email());
        return ResponseEntity.ok().build(); // Always return 200 OK to prevent email enumeration
    }

    // NEW ENDPOINT: Verify password reset code
    @PostMapping("/verify-reset-code")
    public ResponseEntity<Boolean> verifyResetCode(@RequestBody VerifyCodeRequest request) {
        boolean isValid = authService.verifyPasswordResetCode(request.email(), request.code());
        return ResponseEntity.ok(isValid);
    }

    // NEW ENDPOINT: Reset password using code
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}
