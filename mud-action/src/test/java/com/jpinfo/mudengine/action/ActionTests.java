package com.jpinfo.mudengine.action;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.service.ActionScheduler;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

import java.util.function.Predicate;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class ActionTests {
	
	@Autowired
	private ActionScheduler scheduler;
	
	@MockBean
	@SpyBean
	private BeingServiceClient beingClient;
	
	@MockBean
	@SpyBean	
	private ItemServiceClient itemClient;
	
	@MockBean
	@SpyBean	
	private PlaceServiceClient placeClient;

	@Test
	public void contextLoads() {
	}
	
	public void testWalk() {
		
		// Create the being
		Being beingOne = new Being();
		
		beingOne.setBeingType(1);
		beingOne.setBeingCode(1L);
		beingOne.setBeingClass("HUMAN");
		beingOne.setName("Tori");
		beingOne.setPlayerId(1L);
		beingOne.setCurPlaceCode(1);
		beingOne.setCurWorld("aforgotten");
		beingOne.setQuantity(1);
		
		given(beingClient.getBeing(anyString(), 1L)).willReturn(beingOne);

		
		// Create the firstPlace
		Place placeOne = new Place();
		placeOne.setPlaceCode(1);
		placeOne.setPlaceClassCode("PLAIN");
		
		// Adding the north exit to place 2
		PlaceExit northExit = new PlaceExit();
		northExit.setTargetPlaceCode(2);
		placeOne.getExits().put("NORTH", northExit);
		
		given(placeClient.getPlace(1)).willReturn(placeOne);
		
		// ************ CREATE COMMAND *****************
		// =============================================
		
		// ************ READ COMMAND *****************
		// =============================================

		
		// ************ CHECK PREREQS  *****************
		// =============================================

		
		// verify being update
		verify(beingClient).updateBeing(anyString(), beingOne.getBeingCode(), argThatMatches(
				(Being b) -> b.getCurPlaceCode().equals(2)) );
		
	}
	
	
	private static <T> T argThatMatches(Predicate<T> predicate) {
		LambdaMatcher<T> matcher = new LambdaMatcher<>(predicate);
	    return argThat(matcher);
	}
}
