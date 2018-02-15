package com.jpinfo.mudengine.common.security;

import java.util.Collections;


import java.util.Date;

import org.apache.tomcat.util.codec.binary.Base64;
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
	
	// Information stored in the token
	private static final String PLAYER_ID_CLAIM = "playerId";
	private static final String BEING_CODE_CLAIM = "beingCode";
	private static final String LOCALE_CLAIM = "locale";
	
	public static final String HEADER_TOKEN = "Auth";
	
	// 128bits hex key
	private static final String SECRET = "a7ac498c7bba59e0eb7c647d2f0197f8";
	
	
	public static final String INTERNAL_ACCOUNT = "Internal";
	public static final Long INTERNAL_PLAYER_ID = Long.MAX_VALUE;
	public static final Long INTERNAL_BEING_CODE = Long.MAX_VALUE;
	public static final String INTERNAL_LOCALE= "en_US";

	public static String buildToken(String userName, Long playerId, String locale) {
		return TokenService.buildToken(userName, playerId, locale, null);
	}

	
	public static String buildToken(String userName, Long playerId, String locale, Long beingCode) {
		
		String token = null;
		
		JwtBuilder builder = Jwts.builder();
		
		builder.setSubject(userName);
		builder.claim(TokenService.PLAYER_ID_CLAIM, playerId);
		builder.claim(TokenService.LOCALE_CLAIM, locale);
		
		if (beingCode!=null) {
			builder.claim(TokenService.BEING_CODE_CLAIM, beingCode);
		}
		
			
		
		builder.setExpiration(new Date(System.currentTimeMillis() + TokenService.TOKEN_TTL));
		builder.signWith(SignatureAlgorithm.HS512, TokenService.SECRET);
		
		
		token = builder.compact();
		
		return Base64.encodeBase64String(token.getBytes());
	}
	
	public static Authentication getAuthenticationFromToken(String token) {
		
		Authentication result = null;
		
		if (token!=null) {
			
			String username = TokenService.getUsernameFromToken(token);
			
			result = new UsernamePasswordAuthenticationToken(username, null, Collections.<GrantedAuthority>emptyList());
		}
		
		return result;
	}
	
	public static String getUsernameFromToken(String token) {
		
		String result = null;
		
		if (token!=null) {
			
			Jws<Claims> parsedToken = TokenService.parseToken(token);
			
			result = parsedToken.getBody().getSubject();
		}
		
		return result;
	}
	
	public static Long getPlayerIdFromToken(String token) {
		
		Long result = null;
		
		if (token!=null) {
			
			Jws<Claims> parsedToken = TokenService.parseToken(token);
			
			if (parsedToken.getBody().containsKey(TokenService.PLAYER_ID_CLAIM)) {
				result = new Long(parsedToken.getBody().get(TokenService.PLAYER_ID_CLAIM).toString());
			}
		}
		
		return result;
	}

	public static Long getBeingCodeFromToken(String token) {
		
		Long result = null;
		
		if (token!=null) {
			
			Jws<Claims> parsedToken = TokenService.parseToken(token);
			
			if (parsedToken.getBody().containsKey(TokenService.BEING_CODE_CLAIM)) {
				result = new Long(parsedToken.getBody().get(TokenService.BEING_CODE_CLAIM).toString());
			}
		}
		
		return result;
	}

	public static String getLocaleFromToken(String token) {
		
		String result = null;
		
		if (token!=null) {
			
			Jws<Claims> parsedToken = TokenService.parseToken(token);
			
			if (parsedToken.getBody().containsKey(TokenService.LOCALE_CLAIM)) {
				result = parsedToken.getBody().get(TokenService.LOCALE_CLAIM).toString();
			}
		}
		
		return result;
	}
	
	private static Jws<Claims> parseToken(String token) {
		
		String decodedToken = new String(Base64.decodeBase64(token));
		
		Jws<Claims> parsedToken = Jwts.parser().setSigningKey(TokenService.SECRET).parseClaimsJws(decodedToken);
		
		return parsedToken;
		
	}
	
	public static String buildInternalToken() {
		
		return buildToken(TokenService.INTERNAL_ACCOUNT, 
				TokenService.INTERNAL_PLAYER_ID, 
				TokenService.INTERNAL_LOCALE,
				TokenService.INTERNAL_BEING_CODE 
				);
	}
	
	public static void main(String[] args) {
		
		String internalToken = TokenService.buildInternalToken();
		
		String usToken = TokenService.buildToken("username",1L,"en_US", 1L);
		String brToken = TokenService.buildToken("username",1L,"pt_BR", 1L);
		
		System.out.println("internal = " + internalToken);
		System.out.println("en_US = " + usToken);
		System.out.println("pt_BR = " + brToken);
	}
}
