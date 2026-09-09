package com.technova.shopai.service;

import com.technova.shopai.dto.AssignRoleDto;
import com.technova.shopai.dto.UserResponse;
import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserResponse assignRole(AssignRoleDto assignRoleDto);
    BigDecimal getRevenue();
    List<UserResponse> getAllUsers();
}
