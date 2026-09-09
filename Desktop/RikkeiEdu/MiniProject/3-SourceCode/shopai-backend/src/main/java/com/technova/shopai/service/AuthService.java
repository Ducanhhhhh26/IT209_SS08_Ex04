package com.technova.shopai.service;

import com.technova.shopai.dto.LoginDto;
import com.technova.shopai.dto.RegisterDto;
import com.technova.shopai.dto.UserResponse;

public interface AuthService {
    String login(LoginDto loginDto);
    UserResponse register(RegisterDto registerDto);
}
