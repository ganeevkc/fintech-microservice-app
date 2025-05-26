package com.finverse.profile.domain.repository;

import com.finverse.profile.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
}
