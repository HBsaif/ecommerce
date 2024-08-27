package com.ecommerce.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dtos.ConfirmRegistrationDto;
import com.ecommerce.dtos.RegisterUserDto;
import com.ecommerce.entities.OtpRequest;
import com.ecommerce.entities.User;
import com.ecommerce.exceptions.UserAlreadyExistsException;
import com.ecommerce.repositories.OtpRepository;
import com.ecommerce.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OtpService {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private OtpRepository otpRepository;
	
	@Autowired
	private UserRepository userRepository;

	public String sendOtp(String email) {
		
		log.info("Request for intiate register with email : {}", email);

		// Check if the email already exists
		Optional<User> existingUser = userRepository.findByEmail(email);

		if (existingUser.isPresent()) {
			log.error("Signup attempt with already existing email: {}", email);
			throw new UserAlreadyExistsException("User with email " + email + " already exists.");
		}

		// Generate a 6-digit OTP by ensuring it falls within the range 100000-999999
		String otp = String.format("%06d", new Random().nextInt(900000) + 100000);

		// Create requestId in the format "OTP-timestamp"
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
		String requestId = "OTP-" + timestamp;

		// Get OTP expiration time from configuration
		int otpExpiryMinutes = Integer.parseInt(configurationService.getValue("otp.expiration.time"));

		OtpRequest otpRequest = new OtpRequest();
		otpRequest.setRequestId(requestId);
		otpRequest.setEmail(email);
		otpRequest.setOtp(otp);
		otpRequest.setCreatedAt(new Date(System.currentTimeMillis()));
		otpRequest.setExpiredAt(new Date(System.currentTimeMillis() + otpExpiryMinutes * 60 * 1000));

		otpRepository.save(otpRequest);
		
		log.info("OTP generated for email : {}", email);

		// IMPLEMENT SEND OTP TO MAIL/MOBILE HERE

		return requestId;
	}

	public OtpRequest verifyOtp(String requestId, String otp) {

		log.info("Request for confirm register for requestId: {}", requestId);
		
		OtpRequest otpRequest = otpRepository.findByRequestId(requestId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid requestId"));

		if (!otpRequest.getOtp().equals(otp)) {
			throw new IllegalArgumentException("Invalid OTP");
		}

		if (isOtpExpired(otpRequest.getExpiredAt())) {
			throw new IllegalArgumentException("OTP has expired");
		}

		return otpRequest;

	}

	private boolean isOtpExpired(Date otpExpiryDate) {
		Date currentDate = new Date();
		return currentDate.after(otpExpiryDate);
	}

}
