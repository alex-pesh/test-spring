package com.example.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CustomAspectConfig {

	@Pointcut("@annotation(com.example.aspect.CustomAspect)")
	public void pointcut() {}


	@Before("pointcut()")
	public void printInfo(JoinPoint joinPoint) {
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();

		log.info("Method '{}' invoked with arguments: {} ", methodName, java.util.Arrays.toString(args));
	}

}
