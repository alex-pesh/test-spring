package com.example.test.unit;


import com.example.Application;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@AutoConfigurationPackage(basePackageClasses = Application.class)
@ComponentScan(basePackages = {"com.example.mapper"})
public class UnitTestContextConfig {

	public final static String CONTAINER_IMG_POSTGRES = "postgres:17";

	@Bean
	ServletWebServerFactory servletWebServerFactory(){
		return new TomcatServletWebServerFactory();
	}
}
