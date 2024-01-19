package org.springkit.jdbc.strictconn;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springkit.kits.test.SpringKitTestStart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { SpringKitTestStart.class })
@ActiveProfiles("mockjdbc")
public class JdbcStrictConnTest {

	@Before
	public void setUp() {

	}

	@Resource
	private ApplicationContext ctx;

	@Test
	public void testJdbcConnURL() {
		DataSourceProperties dsProps = ctx.getBean(DataSourceProperties.class);

		log.info(dsProps.getUrl());

		Assert.assertEquals("jdbc:mysql://127.0.0.1:3306/test?forceConnectionTimeZoneToSession=true&connectionTimeZone=GMT%2B09%3A00", dsProps.getUrl());
	}

}
