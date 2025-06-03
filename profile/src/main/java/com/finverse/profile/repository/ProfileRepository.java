package com.finverse.profile.repository;

import com.finverse.profile.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<UserProfile,String> {

    boolean existsByUserId(UUID userId);

    Optional<Object> findByUserId(UUID userId);
}
