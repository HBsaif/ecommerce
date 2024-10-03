package com.ecommerce.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.constants.StatusMessage;
import com.ecommerce.constants.UserTypes;
import com.ecommerce.dtos.ApiResponse;
import com.ecommerce.dtos.ChangePasswordDto;
import com.ecommerce.dtos.LoginUserDto;
import com.ecommerce.dtos.RegisterUserDto;
import com.ecommerce.entities.OtpRequest;
import com.ecommerce.entities.User;
import com.ecommerce.model.LoginResponse;
import com.ecommerce.product.services.ShoppingCartService;
import com.ecommerce.repositories.UserRepository;
import com.ecommerce.services.AuthenticationService;
import com.ecommerce.services.JwtService;
import com.ecommerce.services.OtpService;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("${version}/auth")
@RestController
@Slf4j
public class AuthenticationController {

	@Autowired
	private Gson gson;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ShoppingCartService shoppingCartService;

	private final JwtService jwtService;

	private final AuthenticationService authenticationService;

	@Autowired
	private OtpService otpService;

	public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
		this.jwtService = jwtService;
		this.authenticationService = authenticationService;
	}

	// FOR ADMIN CREATION, ONLY USED IN POSTMAN, NOT IN FRONTEND
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<User>> signup(@RequestBody RegisterUserDto registerUserDto) throws Exception {
		ApiResponse<User> response = authenticationService.signup(registerUserDto, UserTypes.ADMIN.toString());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Used to initiate register general users
	@PostMapping("/initiate-register")
	public ResponseEntity<ApiResponse<Map<String, String>>> initiateRegister(@RequestBody RegisterUserDto input)
			throws Exception {
		
		String requestId = otpService.sendOtp(input.getEmail());
		Map<String, String> responseData = new HashMap<>();
		responseData.put("requestId", requestId);
		
		ApiResponse<Map<String, String>> response = new ApiResponse<>(StatusMessage.SUCCESS.toString(), "Successfully sent OTP to email.", responseData);
		log.info("Response : ", response);
		
		return ResponseEntity.ok(response);
	}

	// Used to confirm register general users
	@PostMapping("/confirm-register")
	public ResponseEntity<ApiResponse<User>> confirmRegister(@RequestBody RegisterUserDto registerUserDto)
			throws Exception {
		
		OtpRequest otpRequest = otpService.verifyOtp(registerUserDto.getRequestId(), registerUserDto.getOtp());
		log.info("OTP verified for email : {}", otpRequest.getEmail());
		
		registerUserDto.setEmail(otpRequest.getEmail());
		ApiResponse<User> response = authenticationService.signup(registerUserDto, UserTypes.USER.toString());
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> authenticate(@RequestBody LoginUserDto loginUserDto)
			throws Exception {

		User authenticatedUser = authenticationService.authenticate(loginUserDto);
		String accessToken = jwtService.generateAccessToken(authenticatedUser);
		String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
		boolean isFirstLogin = authenticatedUser.isFirstLogin();
		int cartItems = shoppingCartService.getCartItemsNo(loginUserDto.getEmail());
		LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken, jwtService.getExpirationTime(), isFirstLogin, cartItems);
		ApiResponse<LoginResponse> response = new ApiResponse<>(StatusMessage.SUCCESS.toString(), "Login successful.", loginResponse);

		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse<?>> changePassword(@RequestBody ChangePasswordDto changePasswordDto,
			Authentication authentication) throws Exception {
		log.info("Change password start.");

		if (authentication == null) {
			log.error("Authentication required to change password.");
			throw new Exception("Authentication required to change password.");
		} else if (!authentication.isAuthenticated()) {
			log.error("User is not authenticated.");
			throw new Exception("User is not authenticated.");
		}
		ApiResponse<?> response = null;
		response = authenticationService.changePassword(authentication.getName(), changePasswordDto);

		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody Map<String, String> request) {
	    String refreshToken = request.get("refreshToken");
	    
	    if (refreshToken == null || refreshToken.isEmpty()) {
	        return ResponseEntity.badRequest().body(new ApiResponse<>("FAIL", "Refresh token is required", null));
	    }

	    try {
	        // Validate refresh token
	        if (!jwtService.isTokenValid(refreshToken)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("FAIL", "Invalid refresh token", null));
	        }
	        String username = jwtService.extractUsername(refreshToken);
	        User user = userRepository.findByEmail(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        String newAccessToken = jwtService.generateAccessToken(user);
	        String newRefreshToken = jwtService.generateRefreshToken(user);
	        
	        
	        LoginResponse responseData = new LoginResponse(newAccessToken, newRefreshToken, jwtService.getExpirationTime(), false, 0);

	        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Token refreshed successfully", responseData));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("FAIL", "Error refreshing token", null));
	    }
	}


	
	@PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logoutUser(HttpServletRequest request) {
        // Get the token from the Authorization header
        String token = request.getHeader("Authorization").replace("Bearer ", "");  
        String refreshToken = request.getHeader("Refresh-Token"); // Assuming you send refresh token in a header
        // Invalidate the token by adding it to a blacklist or setting its expiration
        jwtService.invalidateToken(token);
        if (refreshToken != null) {
            jwtService.invalidateToken(refreshToken);
        }
        ApiResponse<?> response = new ApiResponse<>("SUCCESS", "Logged out successfully");
        log.info("Response : {}", gson.toJson(response));
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}