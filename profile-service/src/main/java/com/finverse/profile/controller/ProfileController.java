package com.finverse.profile.controller;

import com.finverse.profile.dto.ProfileDTO;
import com.finverse.profile.dto.ProfileResponse;
import com.finverse.profile.dto.ProfileUpdateRequest;
import com.finverse.profile.model.UserProfile;
import com.finverse.profile.repository.ProfileRepository;
import com.finverse.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileRepository userRepository, PasswordEncoder passwordEncoder, ProfileService profileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public String getProfile(
            @RequestHeader("X-User-ID") UUID userId) {

        ProfileResponse profile = profileService.getProfile(userId);
//        userRepository.findByUserId(userId);
        // Use userId to fetch profile details from DB
        return "Profile data for ${userId}: " + profile;
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<Void> updateProfile(
            @PathVariable UUID userId,
            @RequestBody ProfileUpdateRequest request
    ) {
        profileService.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public List<UserProfile> getUserProfiles(){
        return userRepository.findAll();
    }

//    @Transactional
//    @PostMapping("/create-user")
//    public void createNewUser(@RequestBody final ProfileDTO userProfileDTO) {
//        profileService.createProfile(userProfileDTO);
//    }

    @PutMapping("/update-user")
    public void updateUser(@RequestBody final UserProfile user){
        userRepository.save(user);
    }
//
//    @DeleteMapping("/user/{username}")
//    public void deleteUser(@PathVariable final String username){
//        userRepository.deleteById(username);
//    }

}
