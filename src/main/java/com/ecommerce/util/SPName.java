package com.ecommerce.util;

public enum SPName {
	SP_BLACKLIST_TOKEN("blacklist_token"),
	SP_IS_TOKEN_BLACKLISTED("is_token_blacklisted"),
	;
	
	
	
	
	private final String value;

	SPName(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
