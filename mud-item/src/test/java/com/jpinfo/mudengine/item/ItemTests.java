package com.jpinfo.mudengine.item;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.item.Item;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class ItemTests {

	@Autowired
	private TestRestTemplate restTemplate;
	
	private static final String testItemClass = "TEST";
	private static final String testItemClassAttr1 = "TSTA1";
	private static final String testItemClassAttr2 = "TSTA2";
	
	private static final String test2ItemClass = "TEST2";
	private static final String test2ItemClassAttr1 = "TSTA3";
	private static final String test2ItemClassAttr2 = "TSTA4";
	
	private static final String testCurrentWorld = "aforgotten";
	private static final Integer testCurrentPlace = 1;
	
	private static final String test2CurrentWorld = "fake";
	private static final Integer test2CurrentPlace = 2;

	private static final Integer testQtty = 1;
	private static final Integer test2Qtty = 2;
	
	private static final String testNewAttr = "TSTA5";
	
	private static final Long testCurOwner = 1L;
	
	@Test
	public void testCrudItem() {
		
		// ********** CREATE ***************
		// =================================
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("itemClassCode", ItemTests.testItemClass);
		urlVariables.put("worldName", ItemTests.testCurrentWorld);
		urlVariables.put("placeCode", ItemTests.testCurrentPlace);
		urlVariables.put("quantity", ItemTests.testQtty);
		
		ResponseEntity<Item> createResponse = restTemplate.exchange(
				"/item/?itemClassCode={itemClassCode}&worldName={worldName}&placeCode={placeCode}&quantity={quantity}", 
				HttpMethod.PUT, null, Item.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();
		
		Item createItem = createResponse.getBody();
		
		assertThat(createItem.getItemClass()).isEqualTo(ItemTests.testItemClass);
		assertThat(createItem.getCurPlaceCode()).isEqualTo(ItemTests.testCurrentPlace);
		assertThat(createItem.getCurWorld()).isEqualTo(ItemTests.testCurrentWorld);
		assertThat(createItem.getQuantity()).isEqualTo(ItemTests.testQtty);
		assertThat(createItem.getCurOwner()).isNull();

		assertThat(createItem.getAttrs().get(ItemTests.testItemClassAttr1)).isNotNull();
		assertThat(createItem.getAttrs().get(ItemTests.testItemClassAttr2)).isNotNull();
		
		
		// ************ READ ***************
		// =================================
		urlVariables.put("itemId", createItem.getItemCode());
		
		
		ResponseEntity<Item> readResponse = restTemplate.exchange("/item/{itemId}", HttpMethod.GET, null, Item.class, urlVariables);
		
		assertThat(readResponse.getStatusCode().is2xxSuccessful());
		assertThat(readResponse.getBody()).isNotNull();

		Item readItem = readResponse.getBody();
		
		assertThat(readItem.getItemClass()).isEqualTo(ItemTests.testItemClass);
		assertThat(readItem.getCurPlaceCode()).isEqualTo(ItemTests.testCurrentPlace);
		assertThat(readItem.getCurWorld()).isEqualTo(ItemTests.testCurrentWorld);
		
		assertThat(readItem.getAttrs().get(ItemTests.testItemClassAttr1)).isNotNull();
		assertThat(readItem.getAttrs().get(ItemTests.testItemClassAttr2)).isNotNull();
		

		// *********** UPDATE **************
		// =================================
		urlVariables.put("itemId", createItem.getItemCode());

		readItem.setCurWorld(ItemTests.test2CurrentWorld);
		readItem.setCurPlaceCode(ItemTests.test2CurrentPlace);
		readItem.setItemClass(ItemTests.test2ItemClass);
		readItem.setQuantity(ItemTests.test2Qtty);
		
		readItem.getAttrs().put(ItemTests.testNewAttr, new Integer(55));
		
		HttpEntity<Item> updateRequest = new HttpEntity<Item>(readItem);
		
		ResponseEntity<Item> updateResponse = restTemplate.exchange("/item/{itemId}", HttpMethod.POST, updateRequest, Item.class, urlVariables);

		assertThat(updateResponse.getStatusCode().is2xxSuccessful());
		assertThat(updateResponse.getBody()).isNotNull();

		Item updateItem = updateResponse.getBody();
		
		assertThat(updateItem.getItemClass()).isEqualTo(ItemTests.test2ItemClass);
		assertThat(updateItem.getCurPlaceCode()).isEqualTo(ItemTests.test2CurrentPlace);
		assertThat(updateItem.getCurWorld()).isEqualTo(ItemTests.test2CurrentWorld);
		assertThat(updateItem.getQuantity()).isEqualTo(ItemTests.test2Qtty);
		
		// Old attributes removed
		assertThat(updateItem.getAttrs().get(ItemTests.testItemClassAttr1)).isNull();
		assertThat(updateItem.getAttrs().get(ItemTests.testItemClassAttr2)).isNull();
		
		// New attributes added
		assertThat(updateItem.getAttrs().get(ItemTests.test2ItemClassAttr1)).isNotNull();
		assertThat(updateItem.getAttrs().get(ItemTests.test2ItemClassAttr2)).isNotNull();
		
		assertThat(updateItem.getAttrs().get(ItemTests.testNewAttr)).isNotNull();
		
		// *********** DELETE **************
		// =================================

		ResponseEntity<Item> deleteResponse =  restTemplate.exchange("/item/{itemId}", HttpMethod.DELETE, null, Item.class, urlVariables);
		

		assertThat(deleteResponse.getStatusCode().is2xxSuccessful());
		
		
		// Read after delete
		ResponseEntity<Item> readAfterDeleteResponse = restTemplate.exchange("/item/{itemId}", HttpMethod.GET, null, Item.class, urlVariables);
		
		assertThat(readAfterDeleteResponse.getStatusCode().is4xxClientError());
	}

	@Test
	public void testCrudWithPlace() {

		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// ********** CREATING FIRST ITEM ***************
		// ==============================================
		
		urlVariables.put("itemClassCode", ItemTests.testItemClass);
		urlVariables.put("worldName", ItemTests.testCurrentWorld);
		urlVariables.put("placeCode", ItemTests.testCurrentPlace);
		
		ResponseEntity<Item> createResponse = restTemplate.exchange(
				"/item/?itemClassCode={itemClassCode}&worldName={worldName}&placeCode={placeCode}", 
				HttpMethod.PUT, null, Item.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();
		
		// ********** CREATING SECOND ITEM **************
		// ==============================================
		
		urlVariables.put("itemClassCode", ItemTests.test2ItemClass);
		urlVariables.put("worldName", ItemTests.testCurrentWorld);
		urlVariables.put("placeCode", ItemTests.testCurrentPlace);

		createResponse = restTemplate.exchange(
				"/item/?itemClassCode={itemClassCode}&worldName={worldName}&placeCode={placeCode}", 
				HttpMethod.PUT, null, Item.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();
		
		
		// ******** READ ITEMS FROM PLACE ***********
		// ==============================================
		urlVariables.clear();
		urlVariables.put("worldName", ItemTests.testCurrentWorld);
		urlVariables.put("placeCode", ItemTests.testCurrentPlace);
		
		ResponseEntity<Item[]> findResponse = restTemplate.exchange("/item/place/{worldName}/{placeCode}", HttpMethod.GET, null, Item[].class, urlVariables);
		
		assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(findResponse.getBody().length).isEqualTo(2);
		
		
		// ******** DESTROYING ALL FROM PLACE ***********
		// ==============================================
		ResponseEntity<String> destroyResponse = restTemplate.exchange("/item/place/{worldName}/{placeCode}", HttpMethod.DELETE, null, String.class, urlVariables);
		
		assertThat(destroyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		
		// Looking after both items again
		findResponse = restTemplate.exchange("/item/place/{worldName}/{placeCode}", HttpMethod.GET, null, Item[].class, urlVariables);
		
		assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(findResponse.getBody().length).isEqualTo(0);
		
	}
	
	@Test
	public void testCrudWithOwner() {

		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		// ********** CREATING FIRST ITEM ***************
		// ==============================================
		
		urlVariables.put("itemClassCode", ItemTests.testItemClass);
		urlVariables.put("owner", ItemTests.testCurOwner);
		
		ResponseEntity<Item> createResponse = restTemplate.exchange(
				"/item/?itemClassCode={itemClassCode}&owner={owner}", 
				HttpMethod.PUT, null, Item.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();
		
		Item firstItem = createResponse.getBody();
		
		// ********** CREATING SECOND ITEM **************
		// ==============================================
		
		urlVariables.put("itemClassCode", ItemTests.test2ItemClass);

		createResponse = restTemplate.exchange(
				"/item/?itemClassCode={itemClassCode}&owner={owner}", 
				HttpMethod.PUT, null, Item.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();
		
		Item secondItem = createResponse.getBody();
		
		// ************ READ FROM OWNER ***************
		// ============================================
		
		urlVariables.clear();
		urlVariables.put("owner", ItemTests.testCurOwner);
		
		ResponseEntity<Item[]> readPlaceResponse = restTemplate.exchange("/item/being/{owner}", HttpMethod.GET, null, Item[].class, urlVariables);
		
		assertThat(readPlaceResponse.getStatusCode().is2xxSuccessful());
		assertThat(readPlaceResponse.getBody()).isNotNull();
		assertThat(readPlaceResponse.getBody().length).isEqualTo(2);
		
		
		// ******** DROPPING ALL FROM OWNER ***********
		// ==============================================
		
		urlVariables.put("owner", ItemTests.testCurOwner);
		urlVariables.put("worldName", ItemTests.test2CurrentWorld);
		urlVariables.put("placeCode", ItemTests.test2CurrentPlace);
		
		ResponseEntity<String> dropResponse = restTemplate.exchange("/item/being/{owner}?worldName={worldName}&placeCode={placeCode}", HttpMethod.DELETE, null, String.class, urlVariables);
		
		assertThat(dropResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		
		
		// ********* READ ALL OWNER AGAIN *************
		// ============================================
		ResponseEntity<Item[]> findResponse = restTemplate.exchange("/item/being/{owner}", HttpMethod.GET, null, Item[].class, urlVariables);
		
		assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(findResponse.getBody().length).isEqualTo(0);

		
		// ************ READ FIRST ITEM ***************
		// ============================================
		
		// Looking after the items created; they must be in place, no longer in being
		urlVariables.clear();
		urlVariables.put("itemId", firstItem.getItemCode());
		ResponseEntity<Item> firstItemResponse = restTemplate.exchange("/item/{itemId}", HttpMethod.GET, null, Item.class, urlVariables);
		
		assertThat(firstItemResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(firstItemResponse.getBody()).isNotNull();
		
		Item updatedFirstItem = firstItemResponse.getBody();
		
		assertThat(updatedFirstItem.getCurWorld()).isEqualTo(ItemTests.test2CurrentWorld);
		assertThat(updatedFirstItem.getCurPlaceCode()).isEqualTo(ItemTests.test2CurrentPlace);
		assertThat(updatedFirstItem.getCurOwner()).isNull();
		
		// Deleting it
		urlVariables.clear();
		urlVariables.put("itemId", firstItem.getItemCode());
		ResponseEntity<Item> cleanupFirstItem = restTemplate.exchange("/item/{itemId}", HttpMethod.DELETE, null, Item.class, urlVariables);
		
		assertThat(cleanupFirstItem.getStatusCode()).isEqualTo(HttpStatus.OK)		;

		// ************ READ SECOND ITEM **************
		// ============================================
		
		// Looking after the second item
		
		urlVariables.clear();
		urlVariables.put("itemId", secondItem.getItemCode());
		ResponseEntity<Item> secondItemResponse = restTemplate.exchange("/item/{itemId}", HttpMethod.GET, null, Item.class, urlVariables);
		
		assertThat(secondItemResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(secondItemResponse.getBody()).isNotNull();
		
		Item updatedSecondItem = secondItemResponse.getBody();
		
		assertThat(updatedSecondItem.getCurWorld()).isEqualTo(ItemTests.test2CurrentWorld);
		assertThat(updatedSecondItem.getCurPlaceCode()).isEqualTo(ItemTests.test2CurrentPlace);
		assertThat(updatedSecondItem.getCurOwner()).isNull();
		

		// Deleting this second item as well
		urlVariables.clear();
		urlVariables.put("itemId", secondItem.getItemCode());
		ResponseEntity<Item> cleanupSecondItem = restTemplate.exchange("/item/{itemId}", HttpMethod.DELETE, null, Item.class, urlVariables);
		
		assertThat(cleanupSecondItem.getStatusCode()).isEqualTo(HttpStatus.OK)		;
		
	}
}
