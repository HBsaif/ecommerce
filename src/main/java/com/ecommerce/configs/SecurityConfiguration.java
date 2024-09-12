package com.ecommerce.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.constants.UserTypes;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	@Value("${version}")
	private String version;
	private final AuthenticationProvider authenticationProvider;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
			AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						requests -> requests.requestMatchers(version+"/auth/**").permitAll()
						.requestMatchers(HttpMethod.GET, version+"/api/products/**").permitAll()
						.requestMatchers(HttpMethod.GET, version+"/api/categories/**").permitAll()
						.requestMatchers(HttpMethod.POST, version+"/api/products/**").hasAnyRole(UserTypes.ADMIN.toString(), UserTypes.MODERATOR.toString())
						.requestMatchers(HttpMethod.PUT, version+"/api/products/**").hasAnyRole(UserTypes.ADMIN.toString(), UserTypes.MODERATOR.toString())
						.requestMatchers(HttpMethod.DELETE, version+"/api/products/**").hasAnyRole(UserTypes.ADMIN.toString(), UserTypes.MODERATOR.toString())
						.requestMatchers(HttpMethod.POST, version+"/api/categories/**").hasAnyRole(UserTypes.ADMIN.toString(), UserTypes.MODERATOR.toString())
						.requestMatchers(HttpMethod.PUT, version+"/api/categories/**").hasAnyRole(UserTypes.ADMIN.toString(), UserTypes.MODERATOR.toString())
						.requestMatchers(HttpMethod.DELETE, version+"/api/categories/**").hasAnyRole(UserTypes.ADMIN.toString(), UserTypes.MODERATOR.toString())
						.requestMatchers(version+"/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
//				.addFilterBefore(new RequestLoggingFilter(), JwtAuthenticationFilter.class)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				;

		return http.build();
	}

}
