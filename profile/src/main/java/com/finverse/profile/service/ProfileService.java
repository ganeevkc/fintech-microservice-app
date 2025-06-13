package com.finverse.profile.service;

import com.finverse.profile.dto.ProfileResponse;
import com.finverse.profile.dto.ProfileUpdateRequest;
import com.finverse.profile.exception.ProfileAlreadyExistsException;
import com.finverse.profile.exception.ProfileNotFoundException;
import com.finverse.profile.model.Role;
import com.finverse.profile.model.UserProfile;
import com.finverse.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final AuthServiceClient authServiceClient;

    public UserProfile createBasicProfile(UUID userId, String username, Role role) {
        try {
            validateUuid(userId);
            UserProfile profile = new UserProfile();
            profile.setUserId(userId);
            profile.setUsername(username);
            profile.setRole(role);
            profile.setRegisteredSince(LocalDate.now());
            profileRepository.save(profile);
            log.info("Created profile for user: {}", username);
            return profile;
        }catch (DataAccessException e) {
            log.error("Database error creating profile", e);
            throw new AmqpRejectAndDontRequeueException("Database error", e);
        }
    }
    private void validateUuid(UUID uuid) {
        if (uuid == null || uuid.toString().length() != 36) {
            throw new IllegalArgumentException("Invalid UUID format");
        }
    }

    @Transactional
    public void updateProfile(UUID userId, ProfileUpdateRequest request) {
        if (!profileRepository.existsByUserId(userId)) {
            throw new ProfileNotFoundException("Profile not found for user ID: " + userId);
        }
        UserProfile profile = (UserProfile) profileRepository.findByUserId(userId);

        if (request.getFirstName() != null) {
            profile.setFirstname(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastname(request.getLastName());
        }
        if (request.getAge() != null) {
            profile.setAge(request.getAge());
        }
        if (request.getOccupation() != null) {
            profile.setOccupation(request.getOccupation());
        }
        profileRepository.save(profile);
    }
//    public ProfileResponse getProfile(UUID userId) {
//
//        return profileRepository.findByUserId(userId)
//                .map(this::mapToProfileResponse)
//                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with this user id."));
//    }
//    private ProfileResponse mapToProfileResponse(UserProfile profile) {
//        return new ProfileResponse(
//                profile.getUserId(),
//                profile.getUsername(),
//                profile.getFirstname(),
//                profile.getLastname(),
//                profile.getAge(),
//                profile.getOccupation(),
//                profile.getRegisteredSince()
//        );
//    }
    public ProfileResponse getProfile(UUID userId) {
        UserProfile profile = profileRepository.findByUserId(userId);
        return new ProfileResponse(
                profile.getUserId(),
                profile.getUsername(),
                profile.getFirstname(),
                profile.getLastname(),
                profile.getAge(),
                profile.getOccupation(),
                profile.getRegisteredSince(),
                profile.getRole()
        );
    }
}
