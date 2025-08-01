package com.turki.gamefyback.controller;

import com.turki.gamefyback.dto.CreateAdminDTO;
import com.turki.gamefyback.dto.SignupRequestDTO;
import com.turki.gamefyback.dto.UserDTO;
import com.turki.gamefyback.model.Role;
import com.turki.gamefyback.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.turki.gamefyback.model.User;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody @Valid SignupRequestDTO dto) {
        UserDTO saved = userService.toDTO(userService.createUser(dto));
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        return userService.getUserById(id)
                .map(userService::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestParam(required = false) String email,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        List<UserDTO> users = userService.getAllUsers(email, pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UserDTO dto) {
        UserDTO updated = userService.toDTO(userService.updateUser(id, dto));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDTO> updateUserRoles(@PathVariable String id, @RequestBody Set<Role> roles) {
        UserDTO updatedUser = userService.updateUserRoles(id, roles);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount(@RequestParam(name = "role", required = false) Role role) {
        long count;
        if (role != null) {
            count = userService.countUsersByRole(role);
        } else {
            count = userService.countAllUsers();
        }
        return ResponseEntity.ok(count);
    }

    ////////////////oussema methods ////////////////////////
  @PostMapping("/admin/signup")
  @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createAdmin(@RequestBody @Valid CreateAdminDTO dto) {
        User savedUser = userService.createAdmin(dto);
        return ResponseEntity.ok(userService.toDTO(savedUser));
    }

    //get user conncted infos
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        String firebaseToken = authorizationHeader.replace("Bearer ", "");
        UserDTO userDTO = userService.getCurrentUser(firebaseToken);
        return ResponseEntity.ok(userDTO);
    }
    //update user connected infos
    @PutMapping("/me/{id}")
    public ResponseEntity<UserDTO> updateUserConnecter(@PathVariable String id, @RequestBody UserDTO dto) {
        UserDTO updated = userService.updateUserConnecter(id, dto);
        return ResponseEntity.ok(updated);
    }
    
}