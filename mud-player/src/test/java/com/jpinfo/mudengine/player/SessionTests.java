package com.jpinfo.mudengine.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.player.client.BeingServiceClient;
import com.jpinfo.mudengine.player.model.MudSession;
import com.jpinfo.mudengine.player.model.converter.PlayerConverter;
import com.jpinfo.mudengine.player.repository.SessionRepository;
import com.jpinfo.mudengine.player.service.SessionServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8"})
public class SessionTests {

	private static final String TEST_CLIENT_TYPE = "text/plain";
	private static final String TEST_IP_ADDRESS = "127.0.0.1";
	
	private static final Long OTHER_BEING_CODE = 2L;
	
	@MockBean
	private BeingServiceClient beingClient;
	
	@MockBean
	private SessionRepository sessionRepository;
	
	@Autowired
	private SessionServiceImpl service;
	
	@Autowired
	private TokenService tokenService;
	
	@PostConstruct
	public void setup() throws IOException {
		
		SecurityContextHolder.getContext().setAuthentication(
				tokenService.getAuthenticationFromToken(
						tokenService.buildInternalToken(PlayerTestData.TEST_PLAYER_ID)
						)
				);
		
		given(sessionRepository.save(ArgumentMatchers.any()))
				.willAnswer(i -> {
					
					MudSession sessionBeingSaved = i.getArgument(0, MudSession.class);
					
					if (sessionBeingSaved.getSessionId()==null) {
						sessionBeingSaved.setSessionId(Long.MAX_VALUE);
						sessionBeingSaved.setSessionStart(PlayerTestData.REFERENCE_DATE);
					}
					
					
					return sessionBeingSaved;
				});
		
		given(sessionRepository.findActiveSession(ArgumentMatchers.anyString()))
			.willAnswer(i -> {

				return Optional.of(
						PlayerTestData.loadMudSession(
								i.getArgument(0, String.class))
						);
			});
		
		given(beingClient.getBeing(ArgumentMatchers.anyLong()))
			.willAnswer(i -> {
				
				return PlayerTestData.loadBeing(i.getArgument(0, Long.class));
			});
	}
	
	@Test
	public void testGetSession() {
		
		Session sessionData = service.getActiveSession();
		
		assertThat(sessionData.getPlayerId()).isEqualTo(PlayerTestData.TEST_PLAYER_ID);
		assertThat(sessionData.getClientType()).isEqualTo(SessionTests.TEST_CLIENT_TYPE);
		assertThat(sessionData.getIpAddress()).isEqualTo(SessionTests.TEST_IP_ADDRESS);
		
	}
	
	@Test
	public void testCreateSession() throws IOException {
		
		Player player = PlayerConverter.convert(
				PlayerTestData.loadMudPlayer(PlayerTestData.TEST_PLAYER_ID)
				);

		Session sessionData =
				service.createSession(
						player,
						SessionTests.TEST_CLIENT_TYPE, 
						SessionTests.TEST_IP_ADDRESS);
		
		MudSession expectedEntity = new MudSession();
		expectedEntity.setSessionId(Long.MAX_VALUE);
		expectedEntity.setPlayerId(PlayerTestData.TEST_PLAYER_ID);
		expectedEntity.setIpAddress(SessionTests.TEST_IP_ADDRESS);
		expectedEntity.setClientType(SessionTests.TEST_CLIENT_TYPE);
		expectedEntity.setSessionStart(PlayerTestData.REFERENCE_DATE);
		
		verify(sessionRepository).save(expectedEntity);
		
		assertThat(sessionData.getSessionId()).isEqualTo(Long.MAX_VALUE);
		assertThat(sessionData.getSessionStart()).isNotNull();
		assertThat(sessionData.getPlayerId()).isEqualTo(PlayerTestData.TEST_PLAYER_ID);
		assertThat(sessionData.getClientType()).isEqualTo(SessionTests.TEST_CLIENT_TYPE);
		assertThat(sessionData.getIpAddress()).isEqualTo(SessionTests.TEST_IP_ADDRESS);
		
	}
	
	@Test(expected=IllegalParameterException.class)
	public void testCreateSessionPendingAccount() throws Exception {
		
		Player player = PlayerConverter.convert(
				PlayerTestData.loadMudPlayer(PlayerTestData.TEST_PENDING_PLAYER_ID)
				);		
		
		service.createSession(
				player,
				SessionTests.TEST_CLIENT_TYPE, 
				SessionTests.TEST_IP_ADDRESS);
	}

	@Test
	public void testSetActiveBeing() throws IOException {
		
		Session responseSession = service.setActiveBeing(OTHER_BEING_CODE);
		
		MudSession expectedEntity = PlayerTestData.loadMudSession(TokenService.INTERNAL_ACCOUNT);
		expectedEntity.setBeingCode(OTHER_BEING_CODE);
		
		verify(sessionRepository).save(expectedEntity);
		
		MudUserDetails uDetails = (MudUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		// Check if the beingCode is set in header token
		assertThat(uDetails.getSessionData().getBeingCode()).isEqualTo(OTHER_BEING_CODE);
		
		// Check if the beingCode is set in session object
		assertThat(responseSession.getBeingCode()).isEqualTo(OTHER_BEING_CODE);

	}		

	@Test
	public void testDestroyActiveBeing() throws IOException {
		
		Session responseSession =
				service.destroyBeing(PlayerTestData.TEST_BEING_CODE);
		
		MudSession expectedEntity = PlayerTestData.loadMudSession(TokenService.INTERNAL_ACCOUNT);
		expectedEntity.setBeingCode(null);
		
		verify(sessionRepository).save(expectedEntity);
		
		MudUserDetails uDetails = (MudUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		// Check if the beingCode is NOT set in header token
		assertThat(uDetails.getSessionData().getBeingCode()).isNull();;
		
		// Check if the beingCode is NOT set in session object
		assertThat(responseSession.getBeingCode()).isNull();
		
	}
	
}
