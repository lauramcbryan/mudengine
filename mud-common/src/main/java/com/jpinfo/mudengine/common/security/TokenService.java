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

	public String buildToken(String userName, Optional<Player> playerData, Optional<Session> session) {

		String token = null;
		
		JwtBuilder builder = Jwts.builder();
		
		builder.setSubject(userName);
		
		if (playerData.isPresent())
			builder.claim(TokenService.PLAYER_DATA, playerData.get());
		
		if (session.isPresent())
			builder.claim(TokenService.SESSION_DATA, session.get());
		
		builder.setExpiration(new Date(System.currentTimeMillis() + TokenService.TOKEN_TTL));
		builder.signWith(SignatureAlgorithm.HS256, tokenSecret);
		
		token = builder.compact();
		
		return Base64.encodeBase64String(token.getBytes());
		
	}

	public String updateToken(String token, Optional<Player> playerData, Optional<Session> sessionData) {
	
		Authentication auth = getAuthenticationFromToken(token);
		return buildToken(auth.getPrincipal().toString(), playerData, sessionData);
	}
	
	@SuppressWarnings("unchecked")
	public Authentication getAuthenticationFromToken(String token) {
		
		UsernamePasswordAuthenticationToken result = null;
		
		if ((token!=null) && (!token.isEmpty())) {
			
			// Parse the token
			Jws<Claims> parsedToken = parseToken(token);
			
			// Retrieving the username
			String username = parsedToken.getBody().getSubject();
			
			// Retrieving session information if available
			Session sessionInfo = null;
			
			if (parsedToken.getBody().containsKey(TokenService.SESSION_DATA)) {
				
				sessionInfo = new Session((Map<String, Object>)parsedToken.getBody().get(TokenService.SESSION_DATA));
			}
			
			// Retrieving player information if available
			Player playerData = null;
			
			if (parsedToken.getBody().containsKey(TokenService.PLAYER_DATA)) {
				
				playerData= new Player((Map<String, Object>)parsedToken.getBody().get(TokenService.PLAYER_DATA));
			}

			// Creating the authentication object
			result = new UsernamePasswordAuthenticationToken(username, token, Collections.<GrantedAuthority>emptyList());

			// Setting the userDetails
			result.setDetails(new MudUserDetails(
					Optional.ofNullable(sessionInfo), 
					Optional.ofNullable(playerData)
					)
				);

		}
		
		return result;
	}
	
	public String buildInternalToken(Long playerId) {
		
		Player playerData = new Player();
		playerData.setPlayerId(playerId);
		playerData.setLocale(TokenService.INTERNAL_LOCALE);
		
		Session sessionData = new Session();
		sessionData.setSessionId(Long.MAX_VALUE);
		sessionData.setPlayerId(playerId);
		sessionData.setBeingCode(TokenService.INTERNAL_BEING_CODE);
		
		return buildToken(TokenService.INTERNAL_ACCOUNT, 
				Optional.of(playerData), 
				Optional.of(sessionData)
				);
	}
	
	public String buildInternalToken() {
		return buildInternalToken(TokenService.INTERNAL_PLAYER_ID);
	}
	
	private Jws<Claims> parseToken(String token) {
		
		String decodedToken = new String(Base64.decodeBase64(token));
		
		return Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(decodedToken);
		
	}
}
