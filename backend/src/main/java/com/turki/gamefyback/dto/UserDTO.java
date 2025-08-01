package com.turki.gamefyback.dto;

import com.turki.gamefyback.model.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private String id;
    private String uid;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private boolean emailVerified;
    private boolean hasLocalPassword;
    private String gender;
    private String eloTrack;
    private Set<Role> roles;
    private String bio;
    private Set<String> specialties;
    private boolean isAvailableOnline;
    private boolean isAvailableInPerson;
    private boolean twoFactorEnabled;
    private String password; 
    private String newPassword; // Added for password updates

}
