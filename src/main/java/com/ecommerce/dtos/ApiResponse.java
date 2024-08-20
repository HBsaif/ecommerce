package com.ecommerce.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
	private String status;
	private String message;
	private T data;

	public ApiResponse(String status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

}
