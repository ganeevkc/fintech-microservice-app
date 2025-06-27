package com.finverse.profile.dto;

import com.finverse.profile.model.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileDTO {
    private String username;
//    private String password;
    private String first_name;
    private String last_name;
    private int age;
    private String occupation;
    private LocalDate registeredSince;
    private Role role;

}
