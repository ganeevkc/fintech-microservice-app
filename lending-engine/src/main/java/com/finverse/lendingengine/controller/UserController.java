package com.finverse.lendingengine.controller;

import com.finverse.lendingengine.dto.UserLendingInfoDTO;
import com.finverse.lendingengine.exception.UserNotFoundException;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current-balance")
    public ResponseEntity<Double> getCurrentBalance(
            @RequestHeader("X-User-ID") UUID userId) {
        double balance = userService.getCurrBalance(userId);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @GetMapping("/lending-info")
    public ResponseEntity<UserLendingInfoDTO> getLendingInfo(
            @RequestHeader("X-User-ID") UUID userId) {
        UserLendingInfoDTO info = userService.lendingInfo(userId);
        return ResponseEntity.ok(info);
    }

    @GetMapping
    public User findUser(UUID userId) {
        return userService.findUser(userId);
    }

}
