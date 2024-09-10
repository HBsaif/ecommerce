package com.ecommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.entities.TokenBlackList;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlackList, Integer> {
	
	@Modifying
    @Query("DELETE FROM TokenBlackList t WHERE t.expirationTime < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
