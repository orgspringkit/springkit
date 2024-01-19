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
package org.springkit.kits.test.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@Configuration
public class KitsTestConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	@Resource
	private DataSource dataSource;

	@Resource
	private Environment env;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {

			String jdbcDriver = env.getProperty("spring.datasource.type");

			if (jdbcDriver != null && jdbcDriver.toLowerCase().indexOf("mock") == -1) {
				initDatabase(event.getApplicationContext());
			}
		} catch (ScriptException | SQLException e) {
			e.printStackTrace();
		}
	}

	private void initDatabase(ApplicationContext ctx) throws ScriptException, SQLException {
		org.springframework.core.io.Resource initSql = ctx.getResource("classpath:h2/h2-init.sql");

		try (Connection conn = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(conn, initSql);

			conn.commit();
		}
	}

}
