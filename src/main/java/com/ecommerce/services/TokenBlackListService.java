package com.ecommerce.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.util.CommonServiceHelper;
import com.ecommerce.util.SPName;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TokenBlackListService {
	
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private CommonServiceHelper helper;
    
    

    // Scheduled to run every hour (3600000 milliseconds)
//    @Scheduled(fixedRate = 3600000) 
//    @Scheduled(fixedRate = 120000) 
    public void cleanUpExpiredTokens() {
        try {
        	log.info("[Starting scheduled job] - Cleanup Expired Tokens.");
            helper.executeStoredProcedure(SPName.SP_DELETE_EXPIRED_TOKENS.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public void blacklistToken(String token, Date expirationTime) {
        
        if (expirationTime != null) {
        	
        	Map<String, Object> params = new HashMap<>();
        	
        	// Set input parameters
        	params.put("jwt_token", token);
        	params.put("exp_time", new Timestamp(expirationTime.getTime()));
        	
        	helper.executeStoredProcedure(SPName.SP_BLACKLIST_TOKEN.toString(), params);
            
        } else {
            // Handle cases where the token is invalid
        	
        }
    }

    public boolean isTokenBlacklisted(String token) {
    	
    	Map<String, Object> params = new HashMap<>();
    	
    	//Set IN parameters
    	params.put("jwt_token", token);
    	
    	//Set OUT parameters
        params.put("is_blacklisted", Map.of("type", Boolean.class));
        
        Map<String, Object> results = helper.executeStoredProcedure(SPName.SP_IS_TOKEN_BLACKLISTED.toString(), params);

        Boolean isBlacklisted = (Boolean) results.get("is_blacklisted");

        return isBlacklisted;
    }
}
