package com.ecommerce.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.dtos.ApiResponse;
import com.ecommerce.dtos.RegisterUserDto;
import com.ecommerce.entities.User;
import com.ecommerce.services.UserService;

@RequestMapping("${version}/admin")
@RestController
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-moderator")
    public ResponseEntity<ApiResponse<User>> createModerator(@RequestBody RegisterUserDto registerUserDto) throws Exception {
    	ApiResponse<User> response = userService.createModerator(registerUserDto);
    	return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

