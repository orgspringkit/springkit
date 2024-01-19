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
