package org.springkit.kits;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// com.baomidou.mybatisplus.autoconfigure.SafetyEncryptProcessor can't exclude, it's only decrypt(aes) "mpw:xxx" in Environment by command property(mpw.key)
// com.baomidou.mybatisplus.autoconfigure.MybatisDependsOnDatabaseInitializationDetector can't exclude, it's only set SqlSessionTemplate InitializationBean first when database initialization
@SpringBootApplication(excludeName = { "com.baomidou.mybatisplus.autoconfigure.IdentifierGeneratorAutoConfiguration", //
		"com.baomidou.mybatisplus.autoconfigure.IdentifierGeneratorAutoConfiguration", //
		"com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration", //
		"com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration" })
@ComponentScan(value = { "org.springkit.kits.test" })
@MapperScan("org.springkit.kits.test.mapper")
//@ImportAutoConfiguration(MybatisConfiguration.class)
public class SpringKitWithMybatisStart {

	public static void main(String[] args) {
		SpringApplication.run(SpringKitWithMybatisStart.class, args);
	}

}
