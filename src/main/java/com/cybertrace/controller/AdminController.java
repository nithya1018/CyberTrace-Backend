package com.cybertrace.controller;

import com.cybertrace.dto.RegisterRequest;
import com.cybertrace.model.User;
import com.cybertrace.service.AuthService;
import com.cybertrace.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AuthService authService;

    // GET ALL USERS
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET ALL INVESTIGATORS
    @GetMapping("/investigators")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getInvestigators() {
        return ResponseEntity.ok(userService.getInvestigators());
    }

    // CREATE NEW INVESTIGATOR
    @PostMapping("/investigators")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createInvestigator(
            @Valid @RequestBody RegisterRequest request) {

        // Always create an investigator, not an admin.
        request.setRole("INVESTIGATOR");

        User user = authService.register(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Investigator created successfully");
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getName().name());

        return ResponseEntity.ok(response);
    }

    // ENABLE / DISABLE USER
    @PatchMapping("/users/{id}/toggle-enabled")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> toggleEnabled(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleEnabled(id));
    }
}