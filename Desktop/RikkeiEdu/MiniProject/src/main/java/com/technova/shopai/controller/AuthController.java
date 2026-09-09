package com.technova.shopai.controller;

import com.technova.shopai.dto.JwtAuthResponse;
import com.technova.shopai.dto.LoginDto;
import com.technova.shopai.dto.RegisterDto;
import com.technova.shopai.dto.UserResponse;
import com.technova.shopai.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Đăng ký tài khoản khách hàng mới
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterDto registerDto) {
        UserResponse registeredUser = authService.register(registerDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // Đăng nhập hệ thống và lấy JWT token
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        
        JwtAuthResponse jwtAuthResponse = JwtAuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
                
        return ResponseEntity.ok(jwtAuthResponse);
    }
}
