package com.example.config.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.HashMap;
import java.util.Map;

public class CustomScope implements Scope {

	private final Map<String, Object> beans = new HashMap<>();


	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		System.out.println("Accessing bean " + name);
		return beans.computeIfAbsent(name, k -> objectFactory.getObject());
	}

	@Override
	public Object remove(String name) {
		return beans.remove(name);
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {

	}

	@Override
	public Object resolveContextualObject(String key) {
		return beans;
	}

	@Override
	public String getConversationId() {
		return "";
	}
}