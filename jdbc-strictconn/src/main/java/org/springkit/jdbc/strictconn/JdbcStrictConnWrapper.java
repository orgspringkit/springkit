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
