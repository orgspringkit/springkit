package org.springkit.kits.test.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.datasource.init.ScriptException;

@Configuration
public class KitsTestConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	@Resource
	private DataSource dataSource;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			initDatabase(event.getApplicationContext());
		} catch (ScriptException | SQLException e) {
			e.printStackTrace();
		}
	}

	private void initDatabase(ApplicationContext ctx) throws ScriptException, SQLException {
		org.springframework.core.io.Resource initSql = ctx.getResource("classpath:h2/mybatisplus-init.sql");

		try (Connection conn = dataSource.getConnection()) {
			// ScriptUtils.executeSqlScript(conn, initSql);
			// conn.commit();
		}
	}

	@Bean
	// @ConditionalOnMissingBean(type = {
	// "com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean" })
	@ConditionalOnMissingClass("com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean")
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		return sessionFactory.getObject();
	}

}
