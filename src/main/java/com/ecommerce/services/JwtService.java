package com.ecommerce.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ecommerce.entities.User;

@Service
public class JwtService {

	@Value("${security.jwt.secret-key}")
	private String secretKey;
//	@Value("${security.jwt.expiration-time}")
//	private long jwtExpiration;
	
	@Value("${security.jwt.access-token-expiration-time}")
    private long accessTokenExpiration;
    @Value("${security.jwt.refresh-token-expiration-time}")
    private long refreshTokenExpiration;

	@Autowired
	private TokenBlackListService tokenBlacklistService;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Date getExpirationTimeFromToken(String token, String secretKey) {
		try {
			Claims claims = extractAllClaims(token);
			return claims.getExpiration(); // This will give you the expiration time
		} catch (SignatureException e) {
			// Handle invalid token signature
			e.printStackTrace();
			return null;
		}
	}

//	public String generateToken(User userDetails) {
////		return generateToken(new HashMap<>(), userDetails);
//		// Include roles in the JWT claims
//		Map<String, Object> claims = new HashMap<>();
//		claims.put("roles", List.of(userDetails.getRole()));
//		return generateToken(claims, userDetails);
//	}

//	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
////		extraClaims.put("roles", userDetails.getAuthorities().stream()
////				.map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toList()));
//		return buildToken(extraClaims, userDetails, jwtExpiration);
//	}

	public long getExpirationTime() {
		return accessTokenExpiration;
	}

//	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
//		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
//				.setIssuedAt(new Date(System.currentTimeMillis()))
//				.setExpiration(new Date(System.currentTimeMillis() + expiration))
//				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
//	}
	
	
	public String generateAccessToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(userDetails.getRole()));
        return buildToken(claims, userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(userDetails.getRole()));
        return buildToken(claims, userDetails, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && !isTokenRevoked(token);
	}

	public boolean isTokenRevoked(String token) {
		return tokenBlacklistService.isTokenBlacklisted(token);
	}

	public void invalidateToken(String token) {
		Date expirationTime = getExpirationTimeFromToken(token, secretKey);
		tokenBlacklistService.blacklistToken(token, expirationTime);
	}

	public boolean isTokenValid(String token) {
		return !tokenBlacklistService.isTokenBlacklisted(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// Extract roles from the token
	@SuppressWarnings("unchecked")
	public List<String> extractRoles(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("roles", List.class);
	}
}