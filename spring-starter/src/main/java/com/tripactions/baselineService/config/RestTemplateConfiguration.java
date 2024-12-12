package com.tripactions.notification.configuration;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;

@Configuration
public class RestTemplateConfiguration {

	private static final int CONNECT_TIMEOUT_MS = 20000;

	@Bean
	@Primary
	public RestOperations restOperations(RestTemplateBuilder restTemplateBuilder) {
		HttpComponentsClientHttpRequestFactory httpRequestFactory = getHttpComponentsClientHttpRequestFactory(getHttpClient());
		return restTemplateBuilder
				.requestFactory(() -> new BufferingClientHttpRequestFactory(httpRequestFactory))
				.build();
	}

	private HttpComponentsClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		httpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT_MS);
		httpRequestFactory.setReadTimeout(CONNECT_TIMEOUT_MS);
		httpRequestFactory.setConnectionRequestTimeout(CONNECT_TIMEOUT_MS);
		return httpRequestFactory;
	}

	private CloseableHttpClient getHttpClient() {
		return HttpClientBuilder
				.create()
				.setMaxConnTotal(1000)
				.setMaxConnPerRoute(500)
				.build();
	}
}
