package com.finverse.profile.application;

import com.finverse.profile.domain.dto.UserProfileDTO;
import com.finverse.profile.domain.model.UserProfile;
import com.finverse.profile.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public List<UserProfile> getUserProfiles(){
        return userRepository.findAll();
    }

    @PostMapping("/create-user")
    public void createNewUser(@RequestBody final UserProfileDTO userProfileDTO){
//        user.setRegisteredSince(LocalDate.now());
//        userRepository.save(userProfileDTO);
        UserProfile user = new UserProfile(
                userProfileDTO.getUsername(),
//                passwordEncoder.encode(userProfileDTO.getPassword()),
                userProfileDTO.getFirst_name(),
                userProfileDTO.getLast_name(),
                userProfileDTO.getAge(),
                userProfileDTO.getOccupation()
        );
//        System.out.println("Encoded password stored: " + user.getPassword());

        this.userRepository.save(user);
    }

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
