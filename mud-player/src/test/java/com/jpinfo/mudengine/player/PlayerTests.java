package com.jpinfo.mudengine.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.player.client.BeingServiceClient;
import com.jpinfo.mudengine.player.model.MudPlayer;
import com.jpinfo.mudengine.player.model.MudPlayerBeing;
import com.jpinfo.mudengine.player.model.pk.MudPlayerBeingPK;
import com.jpinfo.mudengine.player.repository.PlayerRepository;
import com.jpinfo.mudengine.player.service.MailService;
import com.jpinfo.mudengine.player.service.PlayerServiceImpl;
import com.jpinfo.mudengine.player.service.SessionServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8"})
public class PlayerTests {
	
	private static final Date REFERENCE_DATE = new Date();
	
	private static final Long TEST_PLAYER_ID = 1L;
	private static final Long TEST_PENDING_PLAYER_ID = 2L;

	private static final String TEST_USERNAME = "testuser";
	private static final String TEST_PASSWORD = "pass-testuser";
	private static final String TEST_PASSWORD_CHANGED = "pass-changed";
	private static final String TEST_LOCALE = "pt-BR";
	private static final String TEST_EMAIL = "josiel.silva.oliveira@gmail.com";

	private static final String TEST_USERNAME_2 = "josiel2";
	private static final String TEST_LOCALE_2 = "en-US";
	private static final String TEST_EMAIL_2 = "changed@test.com";

	private static final Long TEST_BEING_CODE = 1L;
	private static final String TEST_BEING_CLASS = "beingClass";
	private static final String TEST_BEING_NAME = "beingName";

	private static final String TEST_WORLD_NAME = "worldName";
	private static final Integer TEST_PLACE_CODE = 2;

	
	@MockBean
	private PlayerRepository repository;
	
	@MockBean
	private BeingServiceClient beingClient;

	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private PlayerServiceImpl service;
	
	@MockBean
	private SessionServiceImpl sessionService;
	
	@MockBean
	private MailService mailService;

	@PostConstruct
	public void setup() throws IOException {
		
		SecurityContextHolder.getContext().setAuthentication(
				tokenService.getAuthenticationFromToken(
						tokenService.buildInternalToken(PlayerTests.TEST_PLAYER_ID)
						)
				);
		
		
		
		given(sessionService.setActiveBeing(TEST_BEING_CODE))
			.willReturn(new Session());
		
		
		Being createdBeing = PlayerTestData.loadBeing(PlayerTests.TEST_BEING_CODE);
		
		given(beingClient.createPlayerBeing( 
				eq(PlayerTests.TEST_PLAYER_ID),
				eq(PlayerTests.TEST_BEING_CLASS),
				eq(PlayerTests.TEST_WORLD_NAME),
				eq(PlayerTests.TEST_PLACE_CODE),
				eq(PlayerTests.TEST_BEING_NAME))).willReturn(createdBeing);
		
		given(beingClient.getBeing(TEST_BEING_CODE))
			.willReturn(createdBeing);
		
		given(repository.save(ArgumentMatchers.any(MudPlayer.class)))
			.willAnswer(i -> {

				MudPlayer playerBeingSaved = i.getArgument(0, MudPlayer.class);
				
				// Is this player being created?
				if (playerBeingSaved.getPlayerId()==null) {
					
					// Assign a code
					playerBeingSaved.setPlayerId(TEST_PENDING_PLAYER_ID);
					
					// In order to be able to VERIFY this call later, 
					// we populate the auto-generated date field with a well-known value
					playerBeingSaved.setCreateDate(REFERENCE_DATE);
				}
				
				
				
				return playerBeingSaved;
			});
		
		given(repository.findById(ArgumentMatchers.anyLong()))
			.willAnswer(i -> {
				
				return Optional.of(
						PlayerTestData.loadMudPlayer(i.getArgument(0, Long.class))
						);
			});
		
		given(repository.findByUsername(ArgumentMatchers.anyString()))
			.willAnswer(i -> {
				
				return Optional.of(
						PlayerTestData.loadMudPlayer(TEST_PLAYER_ID)
						);
				
			});
		
		given(repository.findByUsernameAndPassword(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
		.willAnswer(i -> {
			
			return Optional.of(
					PlayerTestData.loadMudPlayer(TEST_PLAYER_ID)
					);
			
		});
	}
	
	@Test
	public void testCreatePlayer() {
		
		Player createdPlayer =
				service.registerPlayer(PlayerTests.TEST_USERNAME, 
						PlayerTests.TEST_EMAIL, 
						PlayerTests.TEST_LOCALE);
		
		
		//verify(mailService, times(1)).send(anyObject());
		
		MudPlayer expectedEntity = new MudPlayer();
		expectedEntity.setPlayerId(TEST_PENDING_PLAYER_ID);
		expectedEntity.setUsername(PlayerTests.TEST_USERNAME);
		expectedEntity.setPassword(PlayerTests.TEST_PASSWORD);
		expectedEntity.setEmail(PlayerTests.TEST_EMAIL);
		expectedEntity.setLocale(PlayerTests.TEST_LOCALE);
		expectedEntity.setStatus(Player.STATUS_PENDING);
		expectedEntity.setCreateDate(PlayerTests.REFERENCE_DATE);
		
		verify(repository).save(expectedEntity);
		
		assertThat(createdPlayer.getUsername()).isEqualTo(PlayerTests.TEST_USERNAME);
		assertThat(createdPlayer.getEmail()).isEqualTo(PlayerTests.TEST_EMAIL);
		assertThat(createdPlayer.getLocale()).isEqualTo(PlayerTests.TEST_LOCALE);
		
	}
	
	@Test
	public void testCreatePlayerWithMailEnabled() {
		
		given(mailService.isEnabled()).willReturn(true);
		
		Player playerData = 
			service.registerPlayer(TEST_USERNAME, 
					TEST_EMAIL, 
					TEST_LOCALE);
		
		verify(mailService).sendActivationEmail(
				ArgumentMatchers.eq(playerData), 
				ArgumentMatchers.anyString());
	}
	
	
	@Test(expected = IllegalParameterException.class)
	public void testCreatePlayerDuplicateName() {
		
		given(repository.save(ArgumentMatchers.any(MudPlayer.class)))
			.willThrow(DataIntegrityViolationException.class);
		
		service.registerPlayer(PlayerTests.TEST_USERNAME, 
				PlayerTests.TEST_EMAIL, 
				PlayerTests.TEST_LOCALE);
	}
	
	@Test
	public void testLogin() {
		
		Player player = service.login(TEST_USERNAME, TEST_PASSWORD);
		
		assertThat(player.getPlayerId()).isEqualTo(TEST_PLAYER_ID);
		
	}
	
	@Test
	public void testGetPlayerDetails() {
		
		Player player = service.getPlayerDetails();
		
		assertThat(player.getPlayerId()).isNotNull();
		assertThat(player.getLocale()).isEqualTo(PlayerTests.TEST_LOCALE);
		assertThat(player.getUsername()).isEqualTo(PlayerTests.TEST_USERNAME);
		assertThat(player.getEmail()).isEqualTo(PlayerTests.TEST_EMAIL);
	}
	
	@Test
	public void testUpdatePlayerDetails() {
		
		Player updatePlayer = service.getPlayerDetails();
		
		updatePlayer.setUsername(PlayerTests.TEST_USERNAME_2);
		updatePlayer.setLocale(PlayerTests.TEST_LOCALE_2);
		updatePlayer.setEmail(PlayerTests.TEST_EMAIL_2);
		
		Player changedPlayer = service.updatePlayerDetails(updatePlayer);
		
		assertThat(changedPlayer.getUsername()).isEqualTo(PlayerTests.TEST_USERNAME_2);
		assertThat(changedPlayer.getLocale()).isEqualTo(PlayerTests.TEST_LOCALE_2);
		assertThat(changedPlayer.getEmail()).isEqualTo(PlayerTests.TEST_EMAIL_2);
		assertThat(changedPlayer.getStatus()).isEqualTo(Player.STATUS_PENDING);
	}
	
	@Test
	public void testChangePassword() throws IOException {
		
		Player updatePlayer = service.getPlayerDetails();
		
		service.setPlayerPassword(updatePlayer.getUsername(), 
				PlayerTests.TEST_PASSWORD, 
				PlayerTests.TEST_PASSWORD_CHANGED);
		
		MudPlayer expectedEntity = PlayerTestData.loadMudPlayer(TEST_PLAYER_ID);
		expectedEntity.setPassword(TEST_PASSWORD_CHANGED);
		expectedEntity.setStatus(Player.STATUS_ACTIVE);
		
		verify(repository).save(expectedEntity);
		
	}
	
	@Test
	public void testCreateBeing() throws Exception {
		
		Player changedPlayer = service.createBeing(
				PlayerTests.TEST_BEING_CLASS,
				PlayerTests.TEST_BEING_NAME,
				PlayerTests.TEST_WORLD_NAME,
				PlayerTests.TEST_PLACE_CODE);
		
		// Check if being service was called to create the being
		verify(beingClient).createPlayerBeing(anyLong(),
				eq(PlayerTests.TEST_BEING_CLASS),
				eq(PlayerTests.TEST_WORLD_NAME),
				eq(PlayerTests.TEST_PLACE_CODE),
				eq(PlayerTests.TEST_BEING_NAME));
		
		// Check if player was updated in database
		MudPlayer expectedEntity = PlayerTestData.loadMudPlayer(TEST_PLAYER_ID);
		MudPlayerBeing newPlayerBeing = new MudPlayerBeing();
		newPlayerBeing.setId(new MudPlayerBeingPK());
		newPlayerBeing.getId().setBeingCode(PlayerTests.TEST_BEING_CODE);
		newPlayerBeing.getId().setPlayerId(TEST_PLAYER_ID);
		newPlayerBeing.setBeingClass(PlayerTests.TEST_BEING_CLASS);
		newPlayerBeing.setBeingName(PlayerTests.TEST_BEING_NAME);
		
		expectedEntity.getBeingList().add(newPlayerBeing);
		
		verify(repository).save(expectedEntity);

		// Check if beingcode was returned in player
		assertThat(
				changedPlayer.getBeingList().stream()
				.anyMatch(playerBeing -> playerBeing.getBeingCode().equals(PlayerTests.TEST_BEING_CODE))
				).isTrue();
		
	}
	
	@Test
	public void testSetActiveBeing() throws IOException {
		
		service.setActiveBeing(TEST_BEING_CODE);
		
		verify(sessionService).setActiveBeing(TEST_BEING_CODE);
		
	}
	
	@Test
	public void testDestroyActiveBeing() throws IOException {

		Player changedPlayer = service.destroyBeing(TEST_BEING_CODE);
		
		// Check if external service was called
		verify(beingClient).destroyBeing(TEST_BEING_CODE);
		
		// Check if the being is no longer in beingList
		assertThat(changedPlayer.getBeingList().stream()
			.noneMatch(d -> d.getBeingCode().equals(PlayerTests.TEST_BEING_CODE))
		).isTrue();
		
		MudPlayer expectedEntity = PlayerTestData.loadMudPlayer(TEST_PLAYER_ID);
		expectedEntity.getBeingList().removeIf(e -> e.getId().getBeingCode().equals(PlayerTests.TEST_BEING_CODE));
		
		verify(repository).save(expectedEntity);
		
	}
}
