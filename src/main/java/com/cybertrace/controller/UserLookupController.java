package com.cybertrace.controller;

import com.cybertrace.model.RoleName;
import com.cybertrace.model.User;
import com.cybertrace.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserLookupController {

    private final UserRepository userRepo;

    public UserLookupController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // password is @JsonIgnore in User, so it is never sent
    @GetMapping("/investigators")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> investigators() {
        return userRepo.findByRole_Name(RoleName.INVESTIGATOR).stream()
                .filter(User::isEnabled)
                .toList();
    }
}