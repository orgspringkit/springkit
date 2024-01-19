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
package org.springkit.mybatis.autofill;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springkit.kits.SpringKitWithMybatisStart;
import org.springkit.kits.test.mapper.AutoFillTestEntity;
import org.springkit.kits.test.mapper.MockAutoFillTestMapper;
import org.springkit.mybatis.autofill.CurrentContextHolder.Supplier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { SpringKitWithMybatisStart.class })
public class MybatisAutoFillTest {

	@Resource
	private ApplicationContext ctx;

	@Resource
	private DataSource dataSource;

	@Resource
	private MockAutoFillTestMapper mockMybatisMapper;

	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private String holderId;

	@Before
	public void setUp() throws ScriptException, SQLException {
		holderId = "h" + dtf.format(LocalDateTime.now());
		CurrentContextHolder.setSupplier(new Supplier() {
			@Override
			public String get() {
				return holderId;
			}
		});
	}

	@Test
	public void testInsert() throws SQLException {

		String col1 = LocalDateTime.now().format(dtf) + "_col1";

		AutoFillTestEntity entity = new AutoFillTestEntity();
		entity.setCol1(col1);
		entity.setCol2(LocalDateTime.now().format(dtf) + "_col2");

		mockMybatisMapper.insert(entity);

		AutoFillTestEntity newEntity = mockMybatisMapper.selectOne(entity.getId());

		System.out.println("insert id =" + entity.getId());

		Assert.assertEquals(holderId, newEntity.getCreator());

	}

	@After
	public void showTableContent() {

		List<AutoFillTestEntity> list = mockMybatisMapper.select();

		System.out.println("=====================================");
		System.out.println("========= h2 database print =========");
		System.out.println("=====================================");

		list.stream().forEach(e -> {
			System.out.println(String.format("%s, %s, %s, %s, %s", e.getId(), e.getCol1(), e.getCol2(), e.getCreator(), e.getUpdater()));
		});

	}

}
