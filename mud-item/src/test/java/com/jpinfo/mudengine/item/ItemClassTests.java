package com.jpinfo.mudengine.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.itemclass.ItemClass;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.item.fixture.ItemTemplates;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.repository.ItemClassRepository;
import com.jpinfo.mudengine.item.service.ItemClassServiceImpl;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemClassTests {
	
	@Autowired
	private ItemClassServiceImpl service;
	
	@MockBean
	private ItemClassRepository classRepository;
	
	@MockBean
	private TokenService tokenService;
	
	@PostConstruct
	private void setup() {
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.item.fixture");
	}

	@Test
	public void testGetItemClass() {
		
		MudItemClass dbMockResponse = Fixture.from(MudItemClass.class).gimme(ItemTemplates.VALID);
		
		given(classRepository.findById(dbMockResponse.getCode()))
			.willReturn(Optional.of(dbMockResponse));
		
		ItemClass serviceResponse = service.getItemClass(dbMockResponse.getCode());
		
		assertThat(serviceResponse.getCode()).isEqualTo(dbMockResponse.getCode());
		assertThat(serviceResponse.getDescription()).isEqualTo(dbMockResponse.getDescription());
		assertThat(serviceResponse.getName()).isEqualTo(dbMockResponse.getName());
		assertThat(serviceResponse.getSize()).isEqualTo(dbMockResponse.getSize());
		assertThat(serviceResponse.getWeight()).isEqualTo(dbMockResponse.getWeight());
		
	}
}
