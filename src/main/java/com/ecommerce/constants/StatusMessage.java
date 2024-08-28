package com.ecommerce.constants;

public enum StatusMessage {

	SUCCESS("SUCCESS"),
	FAIL("FAIL"),
	TIMEOUT("TIMEOUT"),
	PENDING("PENDING"),
	SOME_WRONG("Something went wrong."),
	;
	
	private final String value;

	StatusMessage(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
