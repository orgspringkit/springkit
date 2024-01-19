package org.springkit.kits.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = { "org.springkit.kits.test" })
@MapperScan("org.springkit.kits.test.mapper")
public class SpringKitTestStart {

	public static void main(String[] args) {
		SpringApplication.run(SpringKitTestStart.class, args);
	}

}
