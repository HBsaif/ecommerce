package com.ecommerce.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.entities.OtpRequest;

@Repository
public interface OtpRepository extends JpaRepository<OtpRequest, Long> {
    Optional<OtpRequest> findByRequestIdAndOtp(String requestId, String otp);
	Optional<OtpRequest> findByRequestId(String requestId);
}

