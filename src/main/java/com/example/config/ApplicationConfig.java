package com.example.config;

import com.example.config.scope.CustomScope;
import jakarta.servlet.Filter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration(proxyBeanMethods = true)
public class ApplicationConfig implements BeanFactoryPostProcessor {


	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.registerScope("custom", new CustomScope());
	}

/*
	@Bean
	public static CustomScopeConfigurer customScopeConfigurer() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		configurer.addScope("custom", new CustomScope());
		return configurer;
	}
*/


	@Bean
	public Filter etagEtagHeaderFilter() {
		ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
		filter.setWriteWeakETag(true);
		return filter;
	}

	static class BeanType_1 {
		String property_1;
		String property_2;
	}

	static class BeanType_2 {
		Integer property_1;
		Integer property_2;
		BeanType_1 property_3;
	}

	static class BeanType_3 {
		BeanType_1 property;
	}

	@Bean
	BeanType_1 b_1() {
		return new BeanType_1();
	}

	@Bean
	BeanType_2 b_2(BeanType_1 properties) {
		BeanType_2 b = new BeanType_2();
		b.property_3 = b_1();
		return b;
	}

	@Bean
	BeanType_3 b_3() {
		BeanType_3 b = new BeanType_3();
		b.property = b_1();
		return b;
	}

}
