package com.jpinfo.mudengine.world;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MudWorldApplicationTests {
	
	@Autowired
	private MockMvc testContext;

	@Test
	public void contextLoads() {
	}

}
