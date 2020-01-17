package com.jpinfo.mudengine.common.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenService {
	
	private static final long TOKEN_TTL = 3600000;  // 1 hour
	
	private ObjectMapper jsonMapper = new ObjectMapper();
	
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

	public String buildToken(String userName, Player playerData, Session session) throws IOException {

		return Jwts.builder()
				.setSubject(userName)
				.claim(PLAYER_DATA, jsonMapper.writeValueAsString(playerData))
				.claim(SESSION_DATA, jsonMapper.writeValueAsString(session))
				.setExpiration(new Date(System.currentTimeMillis() + TokenService.TOKEN_TTL))
				.signWith(SignatureAlgorithm.HS256, tokenSecret)
				.compact();		
	}

	public String updateToken(String token, Player playerData, Session sessionData) throws IOException {
	
		Authentication auth = getAuthenticationFromToken(token);
		return buildToken(auth.getPrincipal().toString(), playerData, sessionData);
	}
	
	public Authentication getAuthenticationFromToken(String token) throws IOException {
		
		UsernamePasswordAuthenticationToken result = null;
		
		if ((token!=null) && (!token.isEmpty())) {
			
			// Parse the token
			Jws<Claims> parsedToken = parseToken(token);
			
			// Creating the authentication object
			result = new UsernamePasswordAuthenticationToken(
					parsedToken.getBody().getSubject(), // username
					token, Collections.<GrantedAuthority>emptyList());

			// Setting the userDetails
			result.setDetails(new MudUserDetails(
					retrieveSession(parsedToken),
					retrievePlayer(parsedToken)
					)
				);

		}
		
		return result;
	}
	
	public String buildInternalToken(Long playerId) throws IOException {
		
		Player playerData = new Player();
		playerData.setPlayerId(playerId);
		playerData.setUsername(TokenService.INTERNAL_ACCOUNT);
		playerData.setLocale(TokenService.INTERNAL_LOCALE);
		
		Session sessionData = new Session();
		sessionData.setSessionId(Long.MAX_VALUE);
		sessionData.setPlayerId(playerId);
		sessionData.setBeingCode(TokenService.INTERNAL_BEING_CODE);
		
		return buildToken(TokenService.INTERNAL_ACCOUNT, 
				playerData, 
				sessionData
				);
	}
	
	public String buildInternalToken() throws IOException  {
		return buildInternalToken(TokenService.INTERNAL_PLAYER_ID);
	}
	
	private Session retrieveSession(Jws<Claims> parsedToken) throws IOException {
		
		if (parsedToken.getBody().containsKey(TokenService.SESSION_DATA)) {
			
			return jsonMapper.readValue(
					parsedToken.getBody().get(TokenService.SESSION_DATA, String.class), 
					Session.class);
			
		} else {
			return null;
		}
	}
	
	private Player retrievePlayer(Jws<Claims> parsedToken) throws IOException {
		
		if (parsedToken.getBody().containsKey(TokenService.PLAYER_DATA)) {
			
			return jsonMapper.readValue(
					parsedToken.getBody().get(TokenService.PLAYER_DATA, String.class), 
					Player.class);
			
		} else {
			return null;
		}
		
	}
	
	
	private Jws<Claims> parseToken(String token) {
		return Jwts.parser()
				.setSigningKey(tokenSecret)
				.parseClaimsJws(token);
		
	}
}
