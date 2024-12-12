package com.tripactions.taCallBot.features;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestOperations;

@RequiredArgsConstructor
public class baselineServiceClient {
	private final String baseUri;
	private final RestOperations restOperations;

}
