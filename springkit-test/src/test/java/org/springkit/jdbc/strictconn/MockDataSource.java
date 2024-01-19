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

import java.sql.Connection;
import java.sql.SQLException;

import org.mockito.Mockito;

import com.mysql.cj.jdbc.DatabaseMetaData;
import com.mysql.cj.jdbc.MysqlDataSource;

public class MockDataSource extends MysqlDataSource {

	private static final long serialVersionUID = 4378655249720200067L;

	public Connection mockConn;

	public static String dbURL;

	public MockDataSource() throws SQLException {

		DatabaseMetaData dsMeta = Mockito.mock(DatabaseMetaData.class);
		Mockito.when(dsMeta.getDatabaseProductName()).thenReturn("MySQL");
		Mockito.when(dsMeta.getDatabaseMajorVersion()).thenReturn(8);
		Mockito.when(dsMeta.getDatabaseMinorVersion()).thenReturn(0);
		Mockito.when(dsMeta.getDriverName()).thenReturn("MySQL Connector/J");

		Mockito.when(dsMeta.getDriverMajorVersion()).thenReturn(8);
		Mockito.when(dsMeta.getDriverMinorVersion()).thenReturn(0);

		mockConn = Mockito.mock(Connection.class);
		Mockito.when(mockConn.getMetaData()).thenReturn(dsMeta);

	}

	public void setURL(String url) {
		super.setURL(url);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return mockConn;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return mockConn;
	}

}
