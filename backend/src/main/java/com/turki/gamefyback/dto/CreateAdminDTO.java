package com.turki.gamefyback.dto;

import com.turki.gamefyback.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
public class CreateAdminDTO {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String userName;

    @Email
    @NotBlank
    private String email;

    @Length(min = 8, message = "Password must be at least 8 characters long") // Validation only if provided
    private String password; // Optional, backend generates if null

    private String phoneNumber;
    private Set<Role> roles;
    private boolean emailVerified;
}