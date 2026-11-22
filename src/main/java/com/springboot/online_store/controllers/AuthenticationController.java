package com.springboot.online_store.controllers;

import com.springboot.online_store.dtos.user.AuthResponse;
import com.springboot.online_store.dtos.user.UserCreateDto;
import com.springboot.online_store.dtos.user.UserLogin;
import com.springboot.online_store.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody UserLogin userLogin){
        log.info("login user");
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.login(userLogin)
        );
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserCreateDto createDto){
        log.info("register user");
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.registerUser(createDto));
    }
}

