package com.hospital.controller;

import com.hospital.dto.AuthDTO;
import com.hospital.dto.LoginRequestDTO;
import com.hospital.dto.RegisterRequestDTO;
import com.hospital.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<AuthDTO.AuthResponse> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        AuthDTO.AuthResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // Login an existing user
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO request) {

        try {
            AuthDTO.AuthResponse response = authService.login(request);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
}