package com.ecommerce.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.javapoet.ClassName;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dtos.ApiResponse;
import com.ecommerce.dtos.LoginUserDto;
import com.ecommerce.dtos.RegisterUserDto;
import com.ecommerce.entities.User;
import com.ecommerce.exceptions.InvalidCredentialsException;
import com.ecommerce.exceptions.UserAlreadyExistsException;
import com.ecommerce.repositories.UserRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationService {
	
	Gson gson = new Gson();

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager,
			PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public ApiResponse<User> signup(RegisterUserDto input) throws Exception {
		log.info("Signup request for user : {}", input.toString());
		
		// Check if the email already exists
	    Optional<User> existingUser = userRepository.findByEmail(input.getEmail());
	    
	    if (existingUser.isPresent()) {
	        log.error("Signup attempt with already existing email: {}", input.getEmail());
	        throw new UserAlreadyExistsException("User with email " + input.getEmail() + " already exists.");
	    }
	    
		User user = null;
		user = userRepository.save(new User(input.getFullName(), input.getEmail(), passwordEncoder.encode(input.getPassword()), "ADMIN", false));
		if(user == null) {
			log.error("Failed to signup user with email: {}", input.getEmail());
	        throw new Exception("Failed to signup user.");
		}
		ApiResponse<User> response = new ApiResponse<>("SUCCESS", "User registered successfully");
		log.info("Response: {}", gson.toJson(response));
		
		return response;
	}

	public User authenticate(LoginUserDto input) throws Exception {
		LoginUserDto dummy = new LoginUserDto(input.getEmail(), "****");
		log.info("Login Request : {}", gson.toJson(dummy));
		
		User user = null;
		try {
			user = userRepository.findByEmail(input.getEmail()).orElseThrow(()-> new InvalidCredentialsException("Invalid email or password."));
			authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));

		} catch (Exception e){
			log.error("Error!!! : {}", e.getMessage());
	        throw new InvalidCredentialsException("Invalid email or password.");
		}
		
		return user;
		
	}
}