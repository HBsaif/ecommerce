package com.ecommerce.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dtos.ApiResponse;
import com.ecommerce.dtos.LoginUserDto;
import com.ecommerce.dtos.RegisterUserDto;
import com.ecommerce.entities.User;
import com.ecommerce.model.LoginResponse;
import com.ecommerce.services.AuthenticationService;
import com.ecommerce.services.JwtService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/auth")
@RestController
@Slf4j
public class AuthenticationController {
	
	Gson gson = new Gson();

	private final JwtService jwtService;

	private final AuthenticationService authenticationService;

	public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
		this.jwtService = jwtService;
		this.authenticationService = authenticationService;
	}

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<User>> register(@RequestBody RegisterUserDto registerUserDto) throws Exception {
		ApiResponse<User> response = authenticationService.signup(registerUserDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> authenticate(@RequestBody LoginUserDto loginUserDto) throws Exception {
		
		User authenticatedUser = authenticationService.authenticate(loginUserDto);
		String jwtToken = jwtService.generateToken(authenticatedUser);
		
		LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
		ApiResponse<LoginResponse> response = new ApiResponse<>("SUCCESS", "Login successful.", loginResponse);
		
		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}