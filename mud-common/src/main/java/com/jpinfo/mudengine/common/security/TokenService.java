package com.jpinfo.mudengine.common.security;

import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenService {
	
	private static final long TOKEN_TTL = 3600000;  // 1 hour
	
	private static final String PLAYER_ID_CLAIM = "playerId";
	
	public static final String HEADER_TOKEN = "Auth";
	
	// 128bits hex key
	private static final String SECRET = "a7ac498c7bba59e0eb7c647d2f0197f8";

	
	public static String buildToken(String userName, Long playerId) {
		
		String token = null;
		
		token = Jwts.builder()
			.setSubject(userName)
			.claim(TokenService.PLAYER_ID_CLAIM, playerId)
			.setExpiration(new Date(System.currentTimeMillis() + TokenService.TOKEN_TTL))
			.signWith(SignatureAlgorithm.HS512, TokenService.SECRET)
			.compact();
		
		return token;
	}
	
	public static Authentication getAuthenticationFromToken(String token) {
		
		Authentication result = null;
		
		if (token!=null) {
			
			String username = Jwts.parser()
				.setSigningKey(TokenService.SECRET)
				.parseClaimsJws(token).getBody().getSubject();
			
			result = new UsernamePasswordAuthenticationToken(username, null);
		}
		
		return result;
	}
	
	public static Long getPlayerId(String token) {
		
		Long result = null;
		
		if (token!=null) {
			
			result = (Long) Jwts.parser().setSigningKey(TokenService.SECRET)
				.parseClaimsJws(token).getBody().get(TokenService.PLAYER_ID_CLAIM);
		}
		
		return result;
	}
}
