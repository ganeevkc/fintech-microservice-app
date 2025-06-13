package com.finverse.auth.dto;

import com.finverse.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String username;
    private String password;
    private String first_name;
    private String last_name;
    private int age;
    private String occupation;
    private Role role;

}
