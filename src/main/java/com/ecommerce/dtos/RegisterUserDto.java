package com.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
public class RegisterUserDto {

	private String email;
	private String password;
	private String fullName;
	@Override
	public String toString() {
		return "RegisterUserDto [email=" + email + ", fullName=" + fullName + "]";
	}
	
	
}
