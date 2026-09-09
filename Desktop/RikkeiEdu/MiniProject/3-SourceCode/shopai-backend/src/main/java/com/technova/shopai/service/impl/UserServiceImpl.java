package com.technova.shopai.service.impl;

import com.technova.shopai.dto.AssignRoleDto;
import com.technova.shopai.dto.UserResponse;
import com.technova.shopai.entity.Role;
import com.technova.shopai.entity.User;
import com.technova.shopai.exception.BadRequestException;
import com.technova.shopai.exception.ResourceNotFoundException;
import com.technova.shopai.repository.OrderRepository;
import com.technova.shopai.repository.RoleRepository;
import com.technova.shopai.repository.UserRepository;
import com.technova.shopai.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public UserResponse assignRole(AssignRoleDto assignRoleDto) {
        User user = userRepository.findByUsername(assignRoleDto.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + assignRoleDto.getUsername()));

        String requestedRole = assignRoleDto.getRoleName().trim().toUpperCase();
        if (!requestedRole.startsWith("ROLE_")) {
            requestedRole = "ROLE_" + requestedRole;
        }

        Role role = roleRepository.findByName(requestedRole)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + assignRoleDto.getRoleName()));

        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);

        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRevenue() {
        BigDecimal revenue = orderRepository.calculateTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        // userRepository.findAll() đã được cấu hình @EntityGraph tránh N+1 Query
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserResponse).collect(Collectors.toList());
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
