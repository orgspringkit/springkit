/*
 * Copyright (c) 2011-2024, springkit (orgspringkit@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.springkit.kits.SpringKitWithMybatisStart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { SpringKitWithMybatisStart.class })
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
