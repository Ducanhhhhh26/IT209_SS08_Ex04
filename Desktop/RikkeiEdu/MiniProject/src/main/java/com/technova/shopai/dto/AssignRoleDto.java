package com.technova.shopai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleDto {
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Role name cannot be blank")
    private String roleName; // STAFF, MANAGER, CUSTOMER
}
