package com.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.dtos.ApiResponse;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class AppGlobalExceptionHandler {
	Gson gson = new Gson();

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
		ApiResponse<Object> response = new ApiResponse<>("FAIL", ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		ApiResponse<Object> response = new ApiResponse<>("FAIL", ex.getMessage());
		log.info("Response: {}", gson.toJson(response));
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiResponse<Object>> handleInvalidCredentialsException(InvalidCredentialsException  ex) {
		ApiResponse<Object> response = new ApiResponse<>("FAIL", ex.getMessage());
		log.info("Response: {}", gson.toJson(response));
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}
}
