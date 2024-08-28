package com.ecommerce.constants;

public enum UserTypes {
	
	USER("USER"),
	ADMIN("ADMIN"),
	MODERATOR("MODERATOR"),
	;
	
	private final String value;

	UserTypes(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
