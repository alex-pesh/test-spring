package com.example.test.integration;


import com.example.Application;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@TestConfiguration
@AutoConfigurationPackage(basePackageClasses = Application.class)
@ComponentScan(basePackages = {"com.example"},
		excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.example.test.unit.*"))
public class IntTestContextConfig {

/*	@Bean
	ServletWebServerFactory servletWebServerFactory(){
		return new TomcatServletWebServerFactory();
	}*/

}
