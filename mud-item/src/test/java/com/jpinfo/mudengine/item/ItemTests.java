package com.jpinfo.mudengine.item;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import javax.annotation.PostConstruct;

import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.itemclass.ItemClass;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.item.fixture.ItemTemplates;
import com.jpinfo.mudengine.item.fixture.MudItemProcessor;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemAttr;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.model.MudItemClassAttr;
import com.jpinfo.mudengine.item.model.converter.ItemConverter;
import com.jpinfo.mudengine.item.model.converter.MudItemAttrConverter;
import com.jpinfo.mudengine.item.repository.ItemClassRepository;
import com.jpinfo.mudengine.item.repository.ItemRepository;
import com.jpinfo.mudengine.item.utils.ItemHelper;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class ItemTests {
	
	private static final Integer MAX_DURATION_VALUE = 100;
	private static final Integer DURATION_VALUE = 500;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private TokenService tokenService;
	
	@MockBean
	private ItemRepository repository;
	
	@MockBean
	private ItemClassRepository classRepository;
	
	private HttpEntity<Object> emptyHttpEntity;
	

	@PostConstruct
	private void setup() {
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, tokenService.buildInternalToken());
		
		emptyHttpEntity = new HttpEntity<Object>(authHeaders);
		
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.item.fixture");
	}
	
	@Test
	public void testCreateWithPlace() {
		
		MudItem mockRequest = Fixture.from(MudItem.class).gimme(ItemTemplates.REQUEST_WITH_PLACE);
		MudItem mockResponse = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_WITH_PLACE);
		
		given(repository.save(mockRequest)).willReturn(mockResponse);
		
		given(classRepository.findById(mockRequest.getItemClass().getCode()))
			.willReturn(Optional.of(mockRequest.getItemClass()));
		
		Map<String, Object> urlVariables = new HashMap<>();
		
		urlVariables.put("itemClassCode", mockRequest.getItemClass().getCode());
		urlVariables.put("worldName", mockRequest.getCurWorld());
		urlVariables.put("placeCode", mockRequest.getCurPlaceCode());
		
		ResponseEntity<Item> createResponse = restTemplate.exchange(
				"/item/?itemClassCode={itemClassCode}&worldName={worldName}&placeCode={placeCode}", 
				HttpMethod.PUT, emptyHttpEntity, Item.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();
		
		Item createdItem = createResponse.getBody();
		
		assertThat(createdItem.getCurOwner()).isNull();
		assertThat(createdItem.getCurPlaceCode()).isEqualTo(mockResponse.getCurPlaceCode());
		assertThat(createdItem.getCurWorld()).isEqualTo(mockResponse.getCurWorld());
		assertThat(createdItem.getName()).isEqualTo(mockResponse.getItemClass().getName());
		assertThat(createdItem.getQuantity()).isEqualTo(1);
		assertThat(createdItem.getCode()).isNotNull();
		
		assertItemClass(mockResponse.getItemClass(), createdItem.getItemClass());
	}

	@Test
	public void testCreateWithOwner() {

		MudItem mockRequest = Fixture.from(MudItem.class).gimme(ItemTemplates.REQUEST_WITH_OWNER);
		MudItem mockResponse = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_WITH_OWNER);
		
		given(repository.save(mockRequest)).willReturn(mockResponse);
		
		given(classRepository.findById(mockRequest.getItemClass().getCode()))
			.willReturn(Optional.of(mockRequest.getItemClass()));
		
		Map<String, Object> urlVariables = new HashMap<>();
		
		urlVariables.put("itemClassCode", mockRequest.getItemClass().getCode());
		urlVariables.put("owner", mockRequest.getCurOwner());
		
		ResponseEntity<Item> createResponse = restTemplate.exchange(
				"/item/?itemClassCode={itemClassCode}&owner={owner}", 
				HttpMethod.PUT, emptyHttpEntity, Item.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();
		
		Item createdItem = createResponse.getBody();
		
		assertThat(createdItem.getCurOwner()).isEqualTo(mockResponse.getCurOwner());
		assertThat(createdItem.getCurPlaceCode()).isNull();
		assertThat(createdItem.getCurWorld()).isNull();
		assertThat(createdItem.getName()).isEqualTo(mockResponse.getItemClass().getName());
		assertThat(createdItem.getQuantity()).isEqualTo(1);
		assertThat(createdItem.getCode()).isNotNull();
		
		assertItemClass(mockResponse.getItemClass(), createdItem.getItemClass());
		
	}
	
	@Test
	public void testCreateWithBoth() {

		MudItem mockRequest = Fixture.from(MudItem.class).gimme(ItemTemplates.REQUEST_WITH_BOTH);
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemClassCode", mockRequest.getItemClass().getCode());
		urlVariables.put("worldName", mockRequest.getCurWorld());
		urlVariables.put("placeCode", mockRequest.getCurPlaceCode());		
		urlVariables.put("owner", mockRequest.getCurOwner());
		
		ResponseEntity<Item> createResponse = restTemplate.exchange(
				"/item/?itemClassCode={itemClassCode}&worldName={worldName}&placeCode={placeCode}&owner={owner}", 
				HttpMethod.PUT, emptyHttpEntity, Item.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
	}

	@Test
	public void testCreateWithNone() {

		MudItem mockRequest = Fixture.from(MudItem.class).gimme(ItemTemplates.REQUEST);
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemClassCode", mockRequest.getItemClass().getCode());
		
		ResponseEntity<Item> createResponse = restTemplate.exchange(
				"/item/?itemClassCode={itemClassCode}", 
				HttpMethod.PUT, emptyHttpEntity, Item.class, urlVariables);
		
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
	}

	@Test
	public void testRead() {
		MudItem mockResponse = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_FULL);
		
		given(repository.findById(mockResponse.getCode())).willReturn(Optional.of(mockResponse));
		
		Map<String, Object> urlVariables = new HashMap<>();
		
		urlVariables.put("itemId", mockResponse.getCode());
		
		ResponseEntity<Item> serviceResponse = restTemplate.exchange(
				"/item/{itemId}", 
				HttpMethod.GET, emptyHttpEntity, Item.class, urlVariables);
		
		assertThat(serviceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(serviceResponse.getBody()).isNotNull();
		
		Item serviceItem = serviceResponse.getBody();
		
		assertThat(serviceItem.getCurOwner()).isNull();
		assertThat(serviceItem.getCurPlaceCode()).isEqualTo(mockResponse.getCurPlaceCode());
		assertThat(serviceItem.getCurWorld()).isEqualTo(mockResponse.getCurWorld());
		assertThat(serviceItem.getName()).isEqualTo(mockResponse.getItemClass().getName());
		assertThat(serviceItem.getQuantity()).isEqualTo(1);
		assertThat(serviceItem.getCode()).isNotNull();
		
		assertAttrMap(mockResponse, serviceItem);
		
	}

	@Test
	public void testUpdateClass() {

		MudItem originalMudItem = Fixture.from(MudItem.class)
				.uses(new MudItemProcessor())
				.gimme(ItemTemplates.RESPONSE_FULL);

		MudItemClass newClass = Fixture.from(MudItemClass.class).gimme(ItemTemplates.VALID);
		
		MudItem savedItem = (MudItem)SerializationUtils.clone(originalMudItem);
		savedItem.setCode(originalMudItem.getCode());
		savedItem.setName(originalMudItem.getName());
		savedItem.setQuantity(originalMudItem.getQuantity());
		savedItem.setCurOwner(originalMudItem.getCurOwner());
		savedItem.setCurPlaceCode(originalMudItem.getCurPlaceCode());
		savedItem.setCurWorld(originalMudItem.getCurWorld());
		
		// Update the savedItem to contain the new Class
		savedItem.setItemClass(newClass);

		// Adjust attributes as well
		savedItem.getAttrs().clear();
		newClass.getAttrs().stream()
			.forEach(d -> {
				savedItem.getAttrs().add(MudItemAttrConverter.build(savedItem.getCode(), d));
			});
		
		
		given(repository.findById(originalMudItem.getCode())).willReturn(Optional.of(originalMudItem));
		given(classRepository.findById(originalMudItem.getItemClass().getCode())).willReturn(Optional.of(originalMudItem.getItemClass()));
		given(classRepository.findById(newClass.getCode())).willReturn(Optional.of(newClass));

		given(repository.save(savedItem)).willReturn(savedItem);
		

		Item mockBody = ItemConverter.convert(originalMudItem);
		mockBody.setClassCode(newClass.getCode());
		
		HttpEntity<Item> mockHttpEntity = new HttpEntity<>(mockBody, emptyHttpEntity.getHeaders());
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemId", originalMudItem.getCode());
		
		ResponseEntity<Item> serviceResponse = restTemplate.exchange(
				"/item/{itemId}", 
				HttpMethod.POST, mockHttpEntity, Item.class, urlVariables);
		
		assertThat(serviceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(serviceResponse.getBody()).isNotNull();
		
		Item serviceItem = serviceResponse.getBody();
		
		assertThat(serviceItem.getName()).isEqualTo(newClass.getName());
		assertThat(serviceItem.getQuantity()).isEqualTo(1);
		assertThat(serviceItem.getCode()).isNotNull();
		
		// Check consistency between MudItemClass and ItemClass
		assertItemClass(newClass, serviceItem.getItemClass());
		
		// Check consistency between ItemClass and Item
		assertAttrMap(serviceItem, serviceItem.getItemClass());
	}
	
	@Test
	public void testUpdateDuration() {

		// That's the original mudItem record
		MudItem originalMudItem = Fixture.from(MudItem.class)
				.uses(new MudItemProcessor())
				.gimme(ItemTemplates.RESPONSE_FULL);
		
		// We'll inject two duration attributes: DURATION and MAX_DURATION.
		// Both will be the same, because we expect that these values to be correct
		// by the time the record is persisted in database
		originalMudItem.getAttrs().add(
				MudItemAttrConverter.build(
						originalMudItem.getCode(), ItemHelper.ITEM_DURATION_ATTR, MAX_DURATION_VALUE)
				);

		originalMudItem.getAttrs().add(
				MudItemAttrConverter.build(
						originalMudItem.getCode(), ItemHelper.ITEM_MAX_DURATION_ATTR, MAX_DURATION_VALUE)
				);
		
		// Instruct the mocked Item repository to return our original Item
		given(repository.findById(originalMudItem.getCode())).willReturn(Optional.of(originalMudItem));

		// Return the same record as result of updating the database
		given(repository.save(originalMudItem)).willReturn(originalMudItem);
		
		
		// Translates our original item in a service request
		Item mockBody = ItemConverter.convert(originalMudItem);
		
		// In this request, we'll set the duration above the maximum allowed.
		mockBody.getAttrs().put(ItemHelper.ITEM_DURATION_ATTR, DURATION_VALUE);
		
		HttpEntity<Item> mockHttpEntity = new HttpEntity<>(mockBody, emptyHttpEntity.getHeaders());
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemId", originalMudItem.getCode());
		
		ResponseEntity<Item> serviceResponse = restTemplate.exchange(
				"/item/{itemId}", 
				HttpMethod.POST, mockHttpEntity, Item.class, urlVariables);
		
		assertThat(serviceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(serviceResponse.getBody()).isNotNull();
		
		Item serviceItem = serviceResponse.getBody();
		
		// Check that the duration attribute was adjusted to the maximum duration
		// (The same fact that the return is not null implies that the result was ajusted when
		// saved to database
		assertThat(serviceItem.getAttrs().get(ItemHelper.ITEM_DURATION_ATTR)).isEqualTo(100);

	}
	
	@Test
	public void testUpdateDestroyed() {

		// That's the original mudItem record
		MudItem originalMudItem = Fixture.from(MudItem.class)
				.uses(new MudItemProcessor())
				.gimme(ItemTemplates.RESPONSE_FULL);
		
		// Setting the MAX_DURATION attribute
		// That will be used to trigger item destruction
		originalMudItem.getAttrs().add(
				MudItemAttrConverter.build(
						originalMudItem.getCode(), ItemHelper.ITEM_MAX_DURATION_ATTR, MAX_DURATION_VALUE)
				);
		
		// Instruct the mocked Item repository to return our original Item
		given(repository.findById(originalMudItem.getCode())).willReturn(Optional.of(originalMudItem));
		
		// Translates our original item in a service request
		Item mockBody = ItemConverter.convert(originalMudItem);
		
		// In this request, we'll set the duration to zero
		mockBody.getAttrs().put(ItemHelper.ITEM_DURATION_ATTR, 0);
		
		HttpEntity<Item> mockHttpEntity = new HttpEntity<>(mockBody, emptyHttpEntity.getHeaders());
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemId", originalMudItem.getCode());
		
		ResponseEntity<Item> serviceResponse = restTemplate.exchange(
				"/item/{itemId}", 
				HttpMethod.POST, mockHttpEntity, Item.class, urlVariables);
		
		assertThat(serviceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// Check that the delete method was called in database
		verify(repository, times(1)).delete(originalMudItem);
		
	}
	
	@Test
	public void testUpdateDemised() {

		MudItem originalMudItem = Fixture.from(MudItem.class)
				.uses(new MudItemProcessor())
				.gimme(ItemTemplates.RESPONSE_FULL);
		
		
		// We'll inject two duration attributes: DURATION and MAX_DURATION.
		// Both will be the same, because we expect that these values to be correct
		// by the time the record is persisted in database
		originalMudItem.getAttrs().add(
				MudItemAttrConverter.build(
						originalMudItem.getCode(), ItemHelper.ITEM_DURATION_ATTR, MAX_DURATION_VALUE)
				);

		originalMudItem.getAttrs().add(
				MudItemAttrConverter.build(
						originalMudItem.getCode(), ItemHelper.ITEM_MAX_DURATION_ATTR, MAX_DURATION_VALUE)
				);


		MudItemClass newClass = Fixture.from(MudItemClass.class).gimme(ItemTemplates.VALID);
		
		// Set the demised class of the original item to the second class
		originalMudItem.getItemClass().setDemiseClassCode(newClass.getCode());
		
		// The item saved in database will need to contain already the new class
		MudItem savedItem = (MudItem)SerializationUtils.clone(originalMudItem);
		savedItem.setCode(originalMudItem.getCode());
		savedItem.setName(originalMudItem.getName());
		savedItem.setQuantity(originalMudItem.getQuantity());
		savedItem.setCurOwner(originalMudItem.getCurOwner());
		savedItem.setCurPlaceCode(originalMudItem.getCurPlaceCode());
		savedItem.setCurWorld(originalMudItem.getCurWorld());
		
		// Update the savedItem to contain the new Class
		savedItem.setItemClass(newClass);

		// Adjust attributes as well
		savedItem.getAttrs().clear();
		newClass.getAttrs().stream()
			.forEach(d -> {
				savedItem.getAttrs().add(MudItemAttrConverter.build(savedItem.getCode(), d));
			});
		
		
		given(repository.findById(originalMudItem.getCode())).willReturn(Optional.of(originalMudItem));
		given(classRepository.findById(originalMudItem.getItemClass().getCode())).willReturn(Optional.of(originalMudItem.getItemClass()));
		given(classRepository.findById(newClass.getCode())).willReturn(Optional.of(newClass));

		given(repository.save(savedItem)).willReturn(savedItem);
		

		// Converting the original item to service request
		Item mockBody = ItemConverter.convert(originalMudItem);
		
		// In this request, we'll set the duration to zero
		mockBody.getAttrs().put(ItemHelper.ITEM_DURATION_ATTR, 0);
		
		HttpEntity<Item> mockHttpEntity = new HttpEntity<>(mockBody, emptyHttpEntity.getHeaders());
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemId", originalMudItem.getCode());
		
		ResponseEntity<Item> serviceResponse = restTemplate.exchange(
				"/item/{itemId}", 
				HttpMethod.POST, mockHttpEntity, Item.class, urlVariables);
		
		assertThat(serviceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(serviceResponse.getBody()).isNotNull();
		
		Item serviceItem = serviceResponse.getBody();
		
		assertThat(serviceItem.getName()).isEqualTo(newClass.getName());
		assertThat(serviceItem.getQuantity()).isEqualTo(1);
		assertThat(serviceItem.getCode()).isNotNull();
		
		// Check consistency between MudItemClass and ItemClass
		assertItemClass(newClass, serviceItem.getItemClass());
		
		// Check consistency between ItemClass and Item
		
		// Checking that all attributes in database response are present in result
		// We'll not compare map sizes here because we injected two 'strange' attributes, so
		// the sizes will differ
		for(String curAttr: serviceItem.getItemClass().getAttrs().keySet()) {
			assertThat(serviceItem.getAttrs()).containsKey(curAttr);
			assertThat(serviceItem.getAttrs().get(curAttr)).isEqualTo(serviceItem.getItemClass().getAttrs().get(curAttr));
		}
		
		// Checking if the duration was reset to the maximum duration allowed
		assertThat(serviceItem.getAttrs().get(ItemHelper.ITEM_DURATION_ATTR)).isEqualTo(MAX_DURATION_VALUE);
	}
	
	@Test
	public void testDeleteDemised() {

		MudItem originalMudItem = Fixture.from(MudItem.class)
				.uses(new MudItemProcessor())
				.gimme(ItemTemplates.RESPONSE_FULL);
		
		MudItemClass newClass = Fixture.from(MudItemClass.class).gimme(ItemTemplates.VALID);
		
		// Set the demised class of the original item to the second class
		originalMudItem.getItemClass().setDemiseClassCode(newClass.getCode());
		
		// The item saved in database will need to contain already the new class
		MudItem savedItem = (MudItem)SerializationUtils.clone(originalMudItem);
		savedItem.setCode(originalMudItem.getCode());
		savedItem.setName(originalMudItem.getName());
		savedItem.setQuantity(originalMudItem.getQuantity());
		savedItem.setCurOwner(originalMudItem.getCurOwner());
		savedItem.setCurPlaceCode(originalMudItem.getCurPlaceCode());
		savedItem.setCurWorld(originalMudItem.getCurWorld());
		
		// Update the savedItem to contain the new Class
		savedItem.setItemClass(newClass);

		// Adjust attributes as well
		savedItem.getAttrs().clear();
		newClass.getAttrs().stream()
			.forEach(d -> {
				savedItem.getAttrs().add(MudItemAttrConverter.build(savedItem.getCode(), d));
			});
		
		
		given(repository.findById(originalMudItem.getCode())).willReturn(Optional.of(originalMudItem));
		given(classRepository.findById(originalMudItem.getItemClass().getCode())).willReturn(Optional.of(originalMudItem.getItemClass()));
		given(classRepository.findById(newClass.getCode())).willReturn(Optional.of(newClass));

		given(repository.save(savedItem)).willReturn(savedItem);
		

		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemId", originalMudItem.getCode());
		
		ResponseEntity<Item> serviceResponse = restTemplate.exchange(
				"/item/{itemId}", 
				HttpMethod.DELETE, emptyHttpEntity, Item.class, urlVariables);
		
		Item serviceItem = serviceResponse.getBody();
		
		assertThat(serviceItem.getName()).isEqualTo(newClass.getName());
		assertThat(serviceItem.getQuantity()).isEqualTo(1);
		assertThat(serviceItem.getCode()).isNotNull();
		
		// Check consistency between MudItemClass and ItemClass
		assertItemClass(newClass, serviceItem.getItemClass());
		
		// Check consistency between ItemClass and Item
		assertAttrMap(serviceItem, serviceItem.getItemClass());
	}
	
	@Test
	public void testDeleteDestroyed() {
		
		// That's the original mudItem record
		MudItem originalMudItem = Fixture.from(MudItem.class)
				.uses(new MudItemProcessor())
				.gimme(ItemTemplates.RESPONSE_FULL);
		
		// Instruct the mocked Item repository to return our original Item
		given(repository.findById(originalMudItem.getCode())).willReturn(Optional.of(originalMudItem));
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemId", originalMudItem.getCode());
		
		ResponseEntity<Item> serviceResponse = restTemplate.exchange(
				"/item/{itemId}", 
				HttpMethod.DELETE, emptyHttpEntity, Item.class, urlVariables);
		
		assertThat(serviceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// Check that the delete method was called in database
		verify(repository, times(1)).delete(originalMudItem);

		
	}
	
	

	private void assertAttrMap(MudItem mudItem, Item item) {

		// Ensure that the attributes map has the same size as returned by database
		assertThat(item.getAttrs().keySet().size()).isEqualTo(mudItem.getAttrs().size());
		
		// Checking that all attributes in database response are present in result
		for(MudItemAttr curAttr: mudItem.getAttrs()) {
			assertThat(item.getAttrs()).containsKey(curAttr.getCode());
			assertThat(item.getAttrs().get(curAttr.getCode())).isEqualTo(curAttr.getValue());
		}		
	}

	private void assertAttrMap(Item item, ItemClass itemClass) {

		// Ensure that the attributes map has the same size as returned by database
		assertThat(item.getAttrs().keySet().size()).isEqualTo(itemClass.getAttrs().keySet().size());
		
		// Checking that all attributes in database response are present in result
		for(String curAttr: itemClass.getAttrs().keySet()) {
			assertThat(item.getAttrs()).containsKey(curAttr);
			assertThat(item.getAttrs().get(curAttr)).isEqualTo(itemClass.getAttrs().get(curAttr));
		}		
	}
	
	
	private void assertItemClass(MudItemClass mudItemClass, ItemClass itemClass) {

		assertThat(itemClass).isNotNull();
		assertThat(itemClass.getCode()).isEqualTo(mudItemClass.getCode());
		assertThat(itemClass.getName()).isEqualTo(mudItemClass.getName());
		assertThat(itemClass.getSize()).isEqualTo(mudItemClass.getSize());
		assertThat(itemClass.getWeight()).isEqualTo(mudItemClass.getWeight());
		assertThat(itemClass.getDescription()).isEqualTo(mudItemClass.getDescription());

		// Ensure that the attributes map has the same size as returned by database
		assertThat(itemClass.getAttrs().keySet().size()).isEqualTo(mudItemClass.getAttrs().size());
		
		// Checking that all attributes in database response are present in result
		for(MudItemClassAttr curAttr: mudItemClass.getAttrs()) {
			assertThat(itemClass.getAttrs()).containsKey(curAttr.getCode());
			assertThat(itemClass.getAttrs().get(curAttr.getCode())).isEqualTo(curAttr.getValue());
		}		
	}
	
	
}
