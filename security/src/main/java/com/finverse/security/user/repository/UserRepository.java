package com.finverse.security.user.repository;

import com.finverse.security.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserDetails_Username(String username);
    Optional<User> findByUserDetails_JwtToken(String username);
}
