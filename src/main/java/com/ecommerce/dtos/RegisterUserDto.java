package com.ecommerce.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class RegisterUserDto {

	private String email;
	private String password;
	private String fullName;
	
	@Override
	public String toString() {
		return "RegisterUserDto [email=" + email + ", fullName=" + fullName + "]";
	}
	
	
}
