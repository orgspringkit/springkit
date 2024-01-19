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
