package com.technova.shopai.controller;

import com.technova.shopai.dto.AssignRoleDto;
import com.technova.shopai.dto.UserResponse;
import com.technova.shopai.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Xem toàn bộ danh sách người dùng kèm vai trò
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Xem thống kê tổng doanh thu (Đơn hàng ở trạng thái PAID)
    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getRevenue() {
        BigDecimal revenue = userService.getRevenue();
        return ResponseEntity.ok(revenue);
    }

    // Cấp quyền (gán Role) cho tài khoản nhân viên
    @PostMapping("/assign-role")
    public ResponseEntity<UserResponse> assignRole(@Valid @RequestBody AssignRoleDto assignRoleDto) {
        UserResponse userResponse = userService.assignRole(assignRoleDto);
        return ResponseEntity.ok(userResponse);
    }
}
