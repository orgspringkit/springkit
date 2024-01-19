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

import java.sql.Driver;

import org.springframework.beans.factory.FactoryBean;

/**
 * use in spring4
 */
public class JdbcStrictConnWrapper implements FactoryBean<String> {

	private String dbUrl;
	private String scheme;
	private String driverClass;

	public void setJdbcURL(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public void setSchemes(String scheme) {
		this.scheme = scheme;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getObject() throws Exception {

		int sch = StrictConnSchemeEnum.merge(StrictConnSchemeEnum.separate(scheme));

		if (sch == 0) {
			sch = StrictConnSchemeEnum.merge(StrictConnSchemeEnum.TIMEZONE);
		}

		Class<? extends Driver> driverClz = null;

		try {
			driverClz = (Class<? extends Driver>) Class.forName(driverClass);
		} catch (ClassNotFoundException | ExceptionInInitializerError e) {

		}

		if (driverClz != null) {
			StrictConnBuilder builder = JdbcStrictConnConfiguration.findBuilder(dbUrl, driverClz);
			if (builder != null) {
				return JdbcStrictConnConfiguration.getDatabaseStrictConn(builder, sch).make(dbUrl);
			} else {
				return dbUrl;
			}
		} else {
			return dbUrl;
		}
	}

	@Override
	public Class<String> getObjectType() {
		return String.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
