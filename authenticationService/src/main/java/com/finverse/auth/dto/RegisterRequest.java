package com.finverse.auth.dto;

import com.finverse.auth.model.Role;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
