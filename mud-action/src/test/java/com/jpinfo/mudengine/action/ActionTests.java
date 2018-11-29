package com.jpinfo.mudengine.action;


import org.junit.Test;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.MessageServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.dto.ActionInfo;
import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.converter.ActionInfoConverter;
import com.jpinfo.mudengine.action.service.ActionHandler;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.action.Action.EnumActionState;
import com.jpinfo.mudengine.common.action.Action.EnumTargetType;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.placeclass.PlaceClass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8",
			"rule.endpoint=https://2arfb3lhp3.execute-api.us-east-2.amazonaws.com/test"})
public class ActionTests {
	
	private static final Integer START_PLACE_CODE = 1;
	private static final Integer END_PLACE_CODE = 2;
	
	@Autowired
	private ActionHandler handler;
	
	@Autowired
	private ActionInfoConverter actionInfoConverter;
	
	@MockBean
	private BeingServiceClient beingClient;
	
	@MockBean
	private ItemServiceClient itemClient;
	
	@MockBean
	private PlaceServiceClient placeClient;
	
	@MockBean
	private MessageServiceClient messageClient;
	
	@Autowired
	private TestRestTemplate restTemplate;

	
	@Test
	public void contextLoads() {
	}
	
	private Being getBeingOne() {
		Being beingOne = new Being();
		
		beingOne.setType(Being.enumBeingType.REGULAR_SENTIENT);
		beingOne.setCode(1L);
		beingOne.setClassCode("HUMAN");
		beingOne.setName("Tori");
		beingOne.setPlayerId(1L);
		beingOne.setCurPlaceCode(ActionTests.START_PLACE_CODE);
		beingOne.setCurWorld("aforgotten");
		beingOne.setQuantity(1);
		
		BeingClass beingClassOne = new BeingClass();
		beingClassOne.setName("Human beings");
		
		beingOne.setBeingClass(beingClassOne);		
		
		return beingOne;
	}
	
	private Being getBeingTwo() {
		Being beingTwo = new Being();
		
		beingTwo.setType(Being.enumBeingType.REGULAR_SENTIENT);
		beingTwo.setCode(2L);
		beingTwo.setClassCode("HUMAN");
		beingTwo.setName("Uke");
		beingTwo.setPlayerId(1L);
		beingTwo.setCurPlaceCode(ActionTests.START_PLACE_CODE);
		beingTwo.setCurWorld("aforgotten");
		beingTwo.setQuantity(1);
		
		BeingClass beingClassTwo = new BeingClass();
		beingClassTwo.setName("Human beings");
		
		beingTwo.setBeingClass(beingClassTwo);
		
		
		return beingTwo;
	}

	private Place getPlaceOne() {
		Place placeOne = new Place();
		placeOne.setCode(ActionTests.START_PLACE_CODE);
		placeOne.setClassCode("PLAIN");
		
		// Adding the north exit to place 2
		PlaceExit northExit = new PlaceExit();
		northExit.setTargetPlaceCode(ActionTests.END_PLACE_CODE);
		northExit.setOpened(true);
		placeOne.getExits().put("NORTH", northExit);
		
		PlaceClass placeClassOne = new PlaceClass();
		placeClassOne.setName("Plain");
		placeClassOne.setDescription("Plain land");
		
		placeOne.setPlaceClass(placeClassOne);

		return placeOne;
	}

	private void setupMocks() {
		
		given(beingClient.getBeing(eq(1L))).willReturn(getBeingOne());
		given(beingClient.getBeing(eq(2L))).willReturn(getBeingTwo());
		given(placeClient.getPlace(eq(1))).willReturn(getPlaceOne());
		
	}
	
	@Test
	public void testWalkDir() {
		
		setupMocks();
		
		MudAction walkAction = new MudAction();
		walkAction.setActorCode(1L);
		walkAction.setActionClassCode("WALK");   // WALKDIR
		walkAction.setTargetCode("NORTH");
		walkAction.setRunType(Action.EnumRunningType.SIMPLE.toString());
		walkAction.setTargetTypeEnum(EnumTargetType.DIRECTION);

		ActionInfo testData = actionInfoConverter.build(walkAction);

		// ************ UPDATE ACTION *****************
		// =============================================
		testData = handler.runOneAction(1L, testData);
		
		// Assert testData values
		assertThat(testData.getEndTurn()).isNotNull();
		assertThat(testData.getCurState()).isEqualTo(EnumActionState.STARTED);
		
		// Finishing the action
		testData = handler.runOneAction(testData.getEndTurn(), testData);
		
		assertThat(testData.getCurState()).isEqualTo(EnumActionState.COMPLETED);
		assertThat(testData.getActor().getBeing().getCurPlaceCode()).isEqualTo(ActionTests.END_PLACE_CODE);
	}
	
	@Test
	public void testCommands() {
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("locale", "en-US");

		ResponseEntity<Command[]> response = restTemplate.exchange(
				"/action/class/commands/{locale}", 
				HttpMethod.GET, null, Command[].class, urlVariables);
		
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(response.getBody().length).isGreaterThan(0);
	}

}
