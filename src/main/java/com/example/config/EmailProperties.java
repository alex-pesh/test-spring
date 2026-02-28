package com.example.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.*;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "mail")
public class EmailProperties {

	private final String hostname;
	private final int port;
	private final String from;
	private final List<String> defaultRecipients;
	private final Map<String, String> additionalHeaders;
	private final Credentials credentials;
	private final Credentials credentials2;

}
