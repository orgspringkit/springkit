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
import java.util.ArrayList;
import java.util.List;

abstract class StrictConnBuilderFactory {

	protected final Class<? extends Driver> driverClass;

	private StrictConnBuilderFactory(Class<? extends Driver> driverClass) {
		this.driverClass = driverClass;
	}

	public abstract StrictConnBuilder[] builders();

	public static StrictConnBuilderFactory factory(Class<? extends Driver> driverClass) {
		if (driverClass.getName().startsWith("com.mysql.")) {
			return new MysqlStrictConnBuilderFactory(driverClass);
		} else {
			return null;
		}
	}

	private static class MysqlStrictConnBuilderFactory extends StrictConnBuilderFactory {

		MysqlStrictConnBuilderFactory(Class<? extends Driver> driverClass) {
			super(driverClass);
		}

		@Override
		public StrictConnBuilder[] builders() {
			List<StrictConnBuilder> builders = new ArrayList<>();
			{
				StrictConnBuilder builder = mysqlDriverStrictConn(this.driverClass);
				if (builder != null) {
					builders.add(builder);
				}
			}

			return builders.toArray(new StrictConnBuilder[builders.size()]);
		}
	}

	private static StrictConnBuilder mysqlDriverStrictConn(Class<? extends Driver> driverClass) {
		Integer ver = null;

		try {
			
			// 8.x or 6.x
			if (driverClass.getName().equals("com.mysql.cj.jdbc.Driver")) {

				try {
					// 8.x
					Class<?> clzz = Thread.currentThread().getContextClassLoader().loadClass("com.mysql.cj.Constants");
					if (clzz != null) {
						ver = 8;

						return new Mysql8StrictConnBuilder();
					}
				} catch (ClassNotFoundException | IllegalArgumentException | SecurityException e) {
				}

				try {
					// 6.x
					Class<?> clzz = Thread.currentThread().getContextClassLoader().loadClass("com.mysql.cj.core.Constants");
					if (clzz != null) {
						ver = 6;

						return new Mysql6StrictConnBuilder();
					}
				} catch (ClassNotFoundException | IllegalArgumentException | SecurityException e) {
				}

			} else {
				try {
					// 5.x
					Class<?> clzz = Thread.currentThread().getContextClassLoader().loadClass("com.mysql.jdbc.Constants");
					if (clzz != null) {
						ver = 5;

						return new Mysql5StrictConnBuilder();
					}
				} catch (ClassNotFoundException | IllegalArgumentException | SecurityException e) {
				}
			}

			return null;
		} finally {
			if (ver != null) {
				System.out.println("find a driver: mysql-conntector-java.verion = " + ver);
			}
		}

	}

}
