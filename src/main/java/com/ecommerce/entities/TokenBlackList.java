package com.ecommerce.entities;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="token_blacklist")
@Getter
@Setter
public class TokenBlackList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int id;
    private String token;  // Assuming the token is unique and can be the primary key
    private Date expirationTime;

}
