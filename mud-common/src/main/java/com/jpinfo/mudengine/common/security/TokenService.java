package com.jpinfo.mudengine.common.security;

import java.util.Collections;


import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenService {
	
	private static final long TOKEN_TTL = 3600000;  // 1 hour
	
	// Information stored in the token
	private static final String PLAYER_DATA = "playerData";
	private static final String SESSION_DATA = "sessionData";

	
	public static final String INTERNAL_ACCOUNT = "Internal";
	public static final Long INTERNAL_PLAYER_ID = Long.MAX_VALUE;
	public static final Long INTERNAL_BEING_CODE = Long.MAX_VALUE;
	public static final String INTERNAL_LOCALE= "en_US";

	// 128bits hex key	
	@Value("${token.secret}")
	private String tokenSecret;

	public String buildToken(String userName, Player playerData, Session session) {

		String token = null;
		
		JwtBuilder builder = Jwts.builder();
		
		builder.setSubject(userName);
		
		if (playerData!=null)
			builder.claim(TokenService.PLAYER_DATA, playerData);
		
		if (session!=null)
			builder.claim(TokenService.SESSION_DATA, session);
		
		builder.setExpiration(new Date(System.currentTimeMillis() + TokenService.TOKEN_TTL));
		builder.signWith(SignatureAlgorithm.HS256, tokenSecret);
		
		token = builder.compact();
		
		return Base64.encodeBase64String(token.getBytes());
		
	}

	public String updateToken(String token, Optional<Player> playerData, Optional<Session> sessionData) {
	
		Jws<Claims> parsedToken = parseToken(token);
		
		Session oldSession = getSessionDataFromToken(token);
		Player oldPlayer = getPlayerDataFromToken(token);
		
		return buildToken(parsedToken.getBody().getSubject(),
				(playerData.isPresent() ? playerData.get() : oldPlayer),
				(sessionData.isPresent() ? sessionData.get(): oldSession)
				);
	}
	
	public Authentication getAuthenticationFromToken(String token) {
		
		Authentication result = null;
		
		if ((token!=null) && (!token.isEmpty())) {
			
			String username = getUsernameFromToken(token);
			
			result = new UsernamePasswordAuthenticationToken(username, null, Collections.<GrantedAuthority>emptyList());
		}
		
		return result;
	}
	
	public String getUsernameFromToken(String token) {
		
		String result = null;
		
		if ((token!=null) && (!token.isEmpty())) {
			
			Jws<Claims> parsedToken = parseToken(token);
			
			result = parsedToken.getBody().getSubject();
		}
		
		return result;
	}
	
	public Long getPlayerIdFromToken(String token) {
		
		Long result = null;
		
		if ((token!=null) && (!token.isEmpty())) {
			
			Player player = getPlayerDataFromToken(token);
			
			if (player!=null)
				result = player.getPlayerId();
		}
		
		return result;
	}

	public Long getBeingCodeFromToken(String token) {
		
		Long result = null;
		
		if ((token!=null) && (!token.isEmpty())) {
			
			Session sessionData = getSessionDataFromToken(token);
			
			if (sessionData!=null)
				result = sessionData.getBeingCode();
		}
		
		return result;
	}

	public String getLocaleFromToken(String token) {
		
		String result = null;
		
		if ((token!=null) && (!token.isEmpty())) {
			
			Player player = getPlayerDataFromToken(token);
			
			if (player!=null)
				result = player.getLocale();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Session getSessionDataFromToken(String token) {
		
		Session result = null;
		
		if ((token!=null) && (!token.isEmpty())) {
			
			Jws<Claims> parsedToken = parseToken(token);
			
			if (parsedToken.getBody().containsKey(TokenService.SESSION_DATA)) {
				
				result = new Session((Map<String, Object>)parsedToken.getBody().get(TokenService.SESSION_DATA));
			}
		}
		
		return result;
	}	
	
	@SuppressWarnings("unchecked")
	public Player getPlayerDataFromToken(String token) {
		
		Player result = null;
		
		if ((token!=null) && (!token.isEmpty())) {
			
			Jws<Claims> parsedToken = parseToken(token);
			
			if (parsedToken.getBody().containsKey(TokenService.PLAYER_DATA)) {
				
				result = new Player((Map<String, Object>)parsedToken.getBody().get(TokenService.PLAYER_DATA));
			}
		}
		
		return result;
	}
	
	public String buildInternalToken() {
		
		Player playerData = new Player();
		playerData.setPlayerId(TokenService.INTERNAL_PLAYER_ID);
		playerData.setLocale(TokenService.INTERNAL_LOCALE);
		
		Session sessionData = new Session();
		sessionData.setSessionId(Long.MAX_VALUE);
		sessionData.setPlayerId(TokenService.INTERNAL_PLAYER_ID);
		sessionData.setBeingCode(TokenService.INTERNAL_BEING_CODE);
		
		return buildToken(TokenService.INTERNAL_ACCOUNT, playerData, sessionData);
	}
	
	private Jws<Claims> parseToken(String token) {
		
		String decodedToken = new String(Base64.decodeBase64(token));
		
		return Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(decodedToken);
		
	}
}
