package com.ecommerce.configs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.ecommerce.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final HandlerExceptionResolver handlerExceptionResolver;

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
			HandlerExceptionResolver handlerExceptionResolver) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		
		// Wrap the request
        CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);

		// Log initial request details
		log.info("Request URL: {}", wrappedRequest.getRequestURL());
		log.info("Request Method: {}", wrappedRequest.getMethod());
		log.info("Request Headers: {}", getHeaders(wrappedRequest));
		log.info("Request Body: {}", getBody(wrappedRequest));


		final String authHeader = wrappedRequest.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(wrappedRequest, response);
			return;
		}

		try {
			final String jwt = authHeader.substring(7);
			final String userEmail = jwtService.extractUsername(jwt);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (userEmail != null && authentication == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

				if (jwtService.isTokenValid(jwt, userDetails)) {
					// Extract roles from JWT and ensure they're prefixed with "ROLE_"
					List<String> roles = jwtService.extractRoles(jwt); // Implement this method in jwtService
					List<GrantedAuthority> authorities = roles.stream()
							.map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());

//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, authorities);

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}

			filterChain.doFilter(wrappedRequest, response);
		} catch (Exception exception) {
			handlerExceptionResolver.resolveException(wrappedRequest, response, null, exception);
		}
	}

	private String getHeaders(HttpServletRequest request) {

		return Collections.list(request.getHeaderNames()).stream()
				.map(header -> header + ": " + request.getHeader(header)).collect(Collectors.joining(", "));
	}

	private String getBody(CachedBodyHttpServletRequestWrapper wrappedRequest) throws IOException {
		return wrappedRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

//		InputStream inputStream = cachedRequest.getInputStream();
//		byte[] bodyBytes = inputStream.readAllBytes();
//		return new String(bodyBytes, StandardCharsets.UTF_8);
//		return "dummy body";
	}
}