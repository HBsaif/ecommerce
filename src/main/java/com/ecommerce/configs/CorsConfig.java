package com.ecommerce.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Apply CORS to all endpoints
            .allowedOrigins("*")  // Allow requests from this origin
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allow specific methods
            .allowedHeaders("*")  // Allow all headers
            .allowCredentials(true);  // Allow credentials (cookies, authorization headers, etc.)
    }
}