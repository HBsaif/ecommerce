package com.ecommerce.exceptions;

public class WrongPasswordException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongPasswordException(String message) {
		super(message);
	}

	public WrongPasswordException(String message, Throwable cause) {
		super(message, cause);
	}
}
