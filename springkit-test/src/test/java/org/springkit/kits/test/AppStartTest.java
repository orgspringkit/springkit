package org.springkit.kits.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springkit.kits.SpringKitWithMybatisStart;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = { SpringKitWithMybatisStart.class })
public class AppStartTest {
	
	@Test
	public void start() {
		while(true);
	}

}
