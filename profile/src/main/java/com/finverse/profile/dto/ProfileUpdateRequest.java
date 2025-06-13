package com.finverse.profile.dto;

import com.finverse.profile.model.Role;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ProfileUpdateRequest {
    @NotBlank
    private String username;

    @Size(max = 50, message = "First name must be less than 50 characters")
    String firstName;

    @Size(max = 50, message = "Last name must be less than 50 characters")
    String lastName;

    @Min(value = 13, message = "Age must be at least 13")
    @Max(value = 120, message = "Age must be less than 120")
    Integer age;

    @Size(max = 100, message = "Occupation must be less than 100 characters")
    String occupation;

    @Enumerated(EnumType.STRING)
    Role role;
}
