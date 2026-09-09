package com.technova.shopai.service.impl;

import com.technova.shopai.dto.LoginDto;
import com.technova.shopai.dto.RegisterDto;
import com.technova.shopai.dto.UserResponse;
import com.technova.shopai.entity.Role;
import com.technova.shopai.entity.User;
import com.technova.shopai.exception.BadRequestException;
import com.technova.shopai.repository.RoleRepository;
import com.technova.shopai.repository.UserRepository;
import com.technova.shopai.security.JwtTokenProvider;
import com.technova.shopai.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    @Transactional
    public UserResponse register(RegisterDto registerDto) {
        // Kiểm tra xem username đã tồn tại chưa
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        // Kiểm tra xem email đã tồn tại chưa
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BadRequestException("Email is already registered!");
        }

        // Tạo tài khoản mới
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setFullName(registerDto.getFullName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setStatus("ACTIVE");

        // Gán Role mặc định: ROLE_CUSTOMER
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new BadRequestException("Default Customer Role not found in system."));
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }
}
