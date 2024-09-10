package com.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dtos.ApiResponse;
import com.ecommerce.dtos.RegisterUserDto;
import com.ecommerce.entities.User;
import com.ecommerce.exceptions.UserAlreadyExistsException;
import com.ecommerce.repositories.UserRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

	Gson gson = new Gson();

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> allUsers() {
		List<User> users = new ArrayList<>();

		userRepository.findAll().forEach(users::add);

		return users;
	}

	public ApiResponse<User> createModerator(RegisterUserDto input) throws Exception {
		log.info("Signup request for user : {}", input.toString());

		// Check if the email already exists
		Optional<User> existingUser = userRepository.findByEmail(input.getEmail());

		if (existingUser.isPresent()) {
			log.error("Registration attempt with already existing email: {}", input.getEmail());
			throw new UserAlreadyExistsException("User with email " + input.getEmail() + " already exists.");
		}

		User user = null;
		user = userRepository.save(new User(input.getFullName(), input.getEmail(),
				passwordEncoder.encode(input.getPassword()), "MODERATOR", true));
		
		if (user == null) {
			log.error("Failed to register user with email: {}", input.getEmail());
			throw new Exception("Failed to register user.");
		}
		
		ApiResponse<User> response = new ApiResponse<>("SUCCESS", "User registered successfully");
		log.info("Response: {}", gson.toJson(response));

		return response;
	}

	public boolean isFirstLogin(String email) {
		 User user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        return user.isFirstLogin();
	}

	public void updateFirstLoginStatus(String email, boolean status) {
		User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstLogin(status);
        userRepository.save(user);
	}
}