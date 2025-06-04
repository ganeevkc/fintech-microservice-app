package com.finverse.lendingengine.controller;

import com.finverse.lendingengine.service.TokenValidationService;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final TokenValidationService tokenValidationService;

    @Autowired
    public UserController(UserRepository userRepository, TokenValidationService tokenValidationService) {
        this.userRepository = userRepository;
        this.tokenValidationService = tokenValidationService;
    }

    @GetMapping(value = "/users", produces= {MediaType.APPLICATION_JSON_VALUE})
    //swagger annotations-
    @ApiOperation(value="Return all Users with details", notes="This is a public API", response=List.class)
    @ApiResponse(code = HttpServletResponse.SC_OK, message = "Success")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request){
        tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

}
