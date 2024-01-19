package org.springkit.kits;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// can't autoconfig by org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.0+
// org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration
// org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
@SpringBootApplication(excludeName = { "org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration", //
		"org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration" })
@ComponentScan(value = { "org.springkit.kits.test" })
@MapperScan("org.springkit.kits.test.mapper")
public class SpringKitWithMybatisPlusStart {

	public static void main(String[] args) {
		SpringApplication.run(SpringKitWithMybatisPlusStart.class, args);
	}

}
