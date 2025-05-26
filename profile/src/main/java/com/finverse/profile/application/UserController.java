package com.finverse.profile.application;

import com.finverse.profile.domain.model.User;
import com.finverse.profile.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getUserProfiles(){
        return userRepository.findAll();
    }

    @PostMapping("/create-user")
    public void createNewUser(@RequestBody final User user){
//        user.setRegisteredSince(LocalDate.now());
        userRepository.save(user);
    }

    @PutMapping("/update-user")
    public void updateUser(@RequestBody final User user){
        userRepository.save(user);
    }
//
//    @DeleteMapping("/user/{username}")
//    public void deleteUser(@PathVariable final String username){
//        userRepository.deleteById(username);
//    }

}
