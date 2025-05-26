package com.finverse.securityapp.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity //jpa entity
@Data
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
public class User {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char") // Stores UUID as readable string in database
    private UUID id;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Getter
    @Column(name = "role")
    private String role;
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getPassword() {return password;}
//    public void setPassword(String plainPassword) {
//        this.password = encoder.encode(plainPassword);
//    }
    public boolean matchesPassword(String plainPassword) {
        return encoder.matches(plainPassword, this.password);
    }

}
