package com.ecommerce.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dtos.ApiResponse;
import com.ecommerce.entities.User;
import com.ecommerce.services.UserService;

import java.util.List;
import java.util.Optional;

@RequestMapping("${version}/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/id")
    public ResponseEntity<ApiResponse<?>> getUserIdByEmail(@RequestParam String email) {
        Optional<User> optUser = userService.findByEmail(email);
        ApiResponse<?> response = null;
        if (optUser.isPresent()) {
            User user = optUser.get(); // Safe access
            response = new ApiResponse<>("SUCCESS", "User found.", user.getId());
        } else {
        	response = new ApiResponse<>("SUCCESS", "User not found.");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
}