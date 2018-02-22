package com.jpinfo.mudengine.common.security;

import java.util.Collections;


import java.util.Date;
import java.util.Optional;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenService {
	
	private static final long TOKEN_TTL = 3600000;  // 1 hour
	
	// Information stored in the token
	private static final String PLAYER_DATA = "playerData";
	private static final String SESSION_DATA = "sessionData";

	public static final String HEADER_TOKEN = "Auth";
	
	// 128bits hex key
	private static final String SECRET = "a7ac498c7bba59e0eb7c647d2f0197f8";
	
	
	public static final String INTERNAL_ACCOUNT = "Internal";
	public static final Long INTERNAL_PLAYER_ID = Long.MAX_VALUE;
	public static final Long INTERNAL_BEING_CODE = Long.MAX_VALUE;
	public static final String INTERNAL_LOCALE= "en_US";

	public static String buildToken(String userName, Player playerData, Session session) {

		String token = null;
		
		JwtBuilder builder = Jwts.builder();
		
		builder.setSubject(userName);
		builder.claim(TokenService.PLAYER_DATA, playerData);
		builder.claim(TokenService.SESSION_DATA, session);
		
		builder.setExpiration(new Date(System.currentTimeMillis() + TokenService.TOKEN_TTL));
		builder.signWith(SignatureAlgorithm.HS512, TokenService.SECRET);
		
		
		token = builder.compact();
		
		return Base64.encodeBase64String(token.getBytes());
		
	}

	public static String updateToken(String token, Optional<Player> playerData, Optional<Session> sessionData) {
	
		Jws<Claims> parsedToken = TokenService.parseToken(token);
		
		return TokenService.buildToken(parsedToken.getBody().getSubject(),
				(playerData.isPresent() ? playerData.get() : (Player) parsedToken.getBody().get(PLAYER_DATA)),
				(sessionData.isPresent() ? sessionData.get(): (Session) parsedToken.getBody().get(SESSION_DATA))
				);
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
			
			if (parsedToken.getBody().containsKey(TokenService.PLAYER_DATA)) {
				
				Player playerData = (Player)parsedToken.getBody().get(TokenService.PLAYER_DATA);
				
				result = playerData.getPlayerId();
			}
		}
		
		return result;
	}

	public static Long getBeingCodeFromToken(String token) {
		
		Long result = null;
		
		if (token!=null) {
			
			Jws<Claims> parsedToken = TokenService.parseToken(token);
			
			if (parsedToken.getBody().containsKey(TokenService.SESSION_DATA)) {
				
				Session sessionData = (Session)parsedToken.getBody().get(TokenService.SESSION_DATA);
				
				result = sessionData.getBeingCode();
			}
		}
		
		return result;
	}

	public static String getLocaleFromToken(String token) {
		
		String result = null;
		
		if (token!=null) {
			
			Jws<Claims> parsedToken = TokenService.parseToken(token);
			
			if (parsedToken.getBody().containsKey(TokenService.PLAYER_DATA)) {
				
				Player playerData = (Player)parsedToken.getBody().get(TokenService.PLAYER_DATA);
				
				result = playerData.getLocale();
			}
		}
		
		return result;
	}
	
	public static Session getSessionDataFromToken(String token) {
		
		Session result = null;
		
		if (token!=null) {
			
			Jws<Claims> parsedToken = TokenService.parseToken(token);
			
			if (parsedToken.getBody().containsKey(TokenService.SESSION_DATA)) {
				
				Session sessionData = (Session)parsedToken.getBody().get(TokenService.SESSION_DATA);
				
				result = sessionData;
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
		
		Player playerData = new Player();
		playerData.setPlayerId(TokenService.INTERNAL_PLAYER_ID);
		playerData.setLocale(TokenService.INTERNAL_LOCALE);
		
		Session sessionData = new Session();
		sessionData.setBeingCode(TokenService.INTERNAL_BEING_CODE);
		
		return buildToken(TokenService.INTERNAL_ACCOUNT, playerData, sessionData);
	}
	
	public static void main(String[] args) {
		
		String internalToken = TokenService.buildInternalToken();
		
		System.out.println("internal = " + internalToken);
	}
}
