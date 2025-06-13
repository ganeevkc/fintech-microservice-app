package com.finverse.profile.dto;

import com.finverse.profile.model.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProfileResponse {
    UUID userId;
    String username;
    String firstName;
    String lastName;
    Integer age;
    String occupation;
    LocalDate registeredSince;
    Role role;

    public ProfileResponse(UUID userId, String username, String firstname, String lastname, int age, String occupation, LocalDate registeredSince, Role role) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstname;
        this.lastName = lastname;
        this.age = age;
        this.occupation = occupation;
        this.registeredSince = registeredSince;
        this.role = role;
    }
}
