package org.springkit.kits;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * if less spring-boot-autoconfigure-2.7.x and org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.0+
 * the org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration can't auto configure, so must defined it. 
 */
@Configuration
public class Mybatis30Configuration {

	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		return sessionFactory.getObject();
	}

}
