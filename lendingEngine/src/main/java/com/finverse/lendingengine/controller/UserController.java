package com.finverse.lendingengine.controller;

import com.finverse.lendingengine.dto.UserLendingInfoDTO;
import com.finverse.lendingengine.repository.UserRepository;
import com.finverse.lendingengine.service.TokenValidationService;
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
//    private final UserRepository userRepository;
//    private final TokenValidationService tokenValidationService;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, TokenValidationService tokenValidationService) {
        this.userService = userService;
//        this.userRepository = userRepository;
//        this.tokenValidationService = tokenValidationService;
    }

//    @GetMapping("/lending-status")
//    public String getLendingStatus(
//            @RequestHeader("X-User-ID") UUID userId) {
//
//        // Use userId to fetch lending info
//        return "Lending status for user: " + userId;
//    }

    @GetMapping("/current-balance") //done
    public ResponseEntity<Double> getCurrentBalance(
            @RequestHeader("X-User-ID") UUID userId) {
        double balance = userService.getCurrBalance(userId);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @GetMapping("/lending-info") //done
    public ResponseEntity<UserLendingInfoDTO> getLendingInfo(
            @RequestHeader("X-User-ID") UUID userId) {
        UserLendingInfoDTO info = userService.lendingInfo(userId);
        return ResponseEntity.ok(info);

    }



//    @GetMapping(value = "/users", produces= {MediaType.APPLICATION_JSON_VALUE})
//    //swagger annotations-
//    @ApiOperation(value="Return all Users with details", notes="This is a public API", response=List.class)
//    @ApiResponse(code = HttpServletResponse.SC_OK, message = "Success")
//    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request){
//        tokenValidationService.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
//        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
//    }
//    @GetMapping("/users")
//    public Page<UserResponse> getAllUsers()


}
