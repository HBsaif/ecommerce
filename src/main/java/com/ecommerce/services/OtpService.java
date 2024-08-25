package com.ecommerce.services;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dtos.ConfirmRegistrationDto;
import com.ecommerce.dtos.RegisterUserDto;
import com.ecommerce.entities.OtpRequest;
import com.ecommerce.repositories.OtpRepository;

@Service
public class OtpService {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private OtpRepository otpRepository;

	public String generateOtp(RegisterUserDto input) {
		// Generate a 6-digit OTP by ensuring it falls within the range 100000-999999
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);

        // Create requestId in the format "OTP-timestamp"
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
        String requestId = "OTP-" + timestamp;


		// Get OTP expiration time from configuration
		int otpExpiryMinutes = Integer.parseInt(configurationService.getValue("otp.expiration.time"));

		OtpRequest otpRequest = new OtpRequest();
		otpRequest.setRequestId(requestId);
		otpRequest.setEmail(input.getEmail());
		otpRequest.setFullName(input.getFullName());
		otpRequest.setPassword(input.getPassword());
		otpRequest.setOtp(otp);
		otpRequest.setCreatedAt(new Date(System.currentTimeMillis()));
		otpRequest.setExpiredAt(new Date(System.currentTimeMillis() + otpExpiryMinutes * 60 * 1000));

		otpRepository.save(otpRequest);

		// IMPLEMENT SEND OTP TO MAIL/MOBILE HERE

		return requestId;
	}

	public OtpRequest verifyOtp(ConfirmRegistrationDto confirmRegistrationDto) {
		
		OtpRequest otpRequest = otpRepository.findByRequestId(confirmRegistrationDto.getRequestId())
	            .orElseThrow(() -> new IllegalArgumentException("Invalid requestId"));
		
		if (!otpRequest.getOtp().equals(confirmRegistrationDto.getOtp())) {
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
