package org.springkit.jdbc.strictconn;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@ConditionalOnClass({ DataSource.class })
@AutoConfigureAfter({ DataSourceAutoConfiguration.class })
public class JdbcStrictConnConfiguration implements ApplicationListener<ContextRefreshedEvent>, BeanPostProcessor {

	private StrictConnBuilder strictConnBuilder;

	// print MYSQL database time zone in current session
	public void printDbConfig(DataSource ds) {
		StringWriter dbConfig = new StringWriter();

		try (PrintWriter dbConfigPrint = new PrintWriter(dbConfig); Connection conn = ds.getConnection()) {
			if (conn != null) {
				try (Statement st = conn.createStatement()) {
					if (st != null) {
						try (ResultSet rs = st.executeQuery(strictConnBuilder.printConfSql())) {
							dbConfigPrint.print("db config =\t");
							while (rs.next()) {
								StringBuffer line = new StringBuffer();
								line.append(rs.getString(1)).append("=").append(rs.getString(2));
								dbConfigPrint.print(line + ", ");
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println(dbConfig.toString());
	}

	private static int getConnStrictScheme() {

		String scheme = System.getProperty("jdbc.strictconn.schemes", "");
		int sch = StrictConnSchemeEnum.merge(StrictConnSchemeEnum.separate(scheme));

		if (sch == 0) {
			sch = StrictConnSchemeEnum.merge(StrictConnSchemeEnum.TIMEZONE);
		}

		return sch;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (this.strictConnBuilder != null) {
			DataSource ds = event.getApplicationContext().getBean(DataSource.class);
			this.printDbConfig(ds);
		}
	}

	public static JdbcStrictConn getDatabaseStrictConn(StrictConnBuilder connBuilder, int schemes) {
		return new JdbcStrictConn(connBuilder, schemes);
	}

	static StrictConnBuilder findBuilder(String url, Class<? extends Driver> driverClazz) {
		StrictConnBuilder findBuilder = null;

		StrictConnBuilderFactory factory = StrictConnBuilderFactory.factory(driverClazz);

		if (factory != null) {
			for (StrictConnBuilder builder : factory.builders()) {
				if (builder.support(url)) {
					findBuilder = builder;
					break;
				}
			}

			return findBuilder;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private void strictConnURL(org.springframework.boot.autoconfigure.jdbc.DataSourceProperties properties) {

		String url = properties.getUrl();
		Class<? extends Driver> driverClass = null;

		try {
			driverClass = (Class<? extends Driver>) Class.forName(properties.getDriverClassName());
		} catch (ClassNotFoundException | ExceptionInInitializerError e) {

		}
		if (driverClass != null) {
			StrictConnBuilder builder = findBuilder(url, driverClass);

			if (builder != null) {
				this.strictConnBuilder = builder;
				url = getDatabaseStrictConn(builder, getConnStrictScheme()).make(url);
				properties.setUrl(url);
			}
		}
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof org.springframework.boot.autoconfigure.jdbc.DataSourceProperties) {

			strictConnURL((org.springframework.boot.autoconfigure.jdbc.DataSourceProperties) bean);
		}
		return bean;
	}

}
