package com.jpinfo.mudengine.common.security;

import java.util.Collections;

import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenService {
	
	private static final long TOKEN_TTL = 3600000;  // 1 hour
	
	private static final String PLAYER_ID_CLAIM = "playerId";
	
	public static final String HEADER_TOKEN = "Auth";
	
	// 128bits hex key
	private static final String SECRET = "a7ac498c7bba59e0eb7c647d2f0197f8";
	
	
	public static final String INTERNAL_ACCOUNT = "Internal";
	public static final Long INTERNAL_PLAYER_ID = Long.MAX_VALUE;

	
	public static String buildToken(String userName, Long playerId) {
		
		String token = null;
		
		JwtBuilder builder = Jwts.builder();
		
		builder.setSubject(userName);
		builder.claim(TokenService.PLAYER_ID_CLAIM, playerId);
		builder.setExpiration(new Date(System.currentTimeMillis() + TokenService.TOKEN_TTL));
		builder.signWith(SignatureAlgorithm.HS512, TokenService.SECRET);
		
		
		token = builder.compact();
		
		return token;
	}
	
	public static Authentication getAuthenticationFromToken(String token) {
		
		Authentication result = null;
		
		if (token!=null) {
			
			String username = Jwts.parser()
				.setSigningKey(TokenService.SECRET)
				.parseClaimsJws(token).getBody().getSubject();
			
			result = new UsernamePasswordAuthenticationToken(username, null, Collections.<GrantedAuthority>emptyList());
		}
		
		return result;
	}
	
	public static String getUsernameFromToken(String token) {
		
		String result = null;
		
		if (token!=null) {
			
			result = Jwts.parser().setSigningKey(TokenService.SECRET)
				.parseClaimsJws(token).getBody().getSubject();
		}
		
		return result;
	}
	
	public static Long getPlayerIdFromToken(String token) {
		
		Long result = null;
		
		if (token!=null) {
			
			Jws<Claims> parsedToken = Jwts.parser().setSigningKey(TokenService.SECRET).parseClaimsJws(token);
			
			if (parsedToken.getBody().containsKey(TokenService.PLAYER_ID_CLAIM)) {
				result = new Long(parsedToken.getBody().get(TokenService.PLAYER_ID_CLAIM).toString());
			}
		}
		
		return result;
	}
	
	public static String buildInternalToken() {
		return buildToken(TokenService.INTERNAL_ACCOUNT, TokenService.INTERNAL_PLAYER_ID);
	}
}