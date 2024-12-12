package com.tripactions.taCallBot.config;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.util.NamedThreadFactory;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;

@Configuration
@Slf4j
@ConditionalOnExpression("T(org.apache.commons.lang3.StringUtils).isNotBlank('${newrelic.api-key:}')")
public class NewRelicConfiguration {
    private final String newRelicApiKey;
    private final String applicationName;

    public NewRelicConfiguration(@Value("${newrelic.api-key}") String newRelicApiKey,
                                 @Value("${newrelic.applicationName:ta-call-bot}") String applicationName) {
        this.newRelicApiKey = newRelicApiKey;
        this.applicationName = applicationName;
    }

    public NewRelicRegistryConfig newRelicConfig() {
        log.info ("Configuring New Relic for application: {}", applicationName);
        return new NewRelicRegistryConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String apiKey() {
                return newRelicApiKey;
            }

            @Override
            public Duration step() {
                return Duration.ofSeconds(5);
            }

            @Override
            public String serviceName() {
                return applicationName;
            }
        };
    }

    @Bean(name="notificationServerMetricRegistry")
    public NewRelicRegistry newRelicMeterRegistry() throws UnknownHostException {
        NewRelicRegistryConfig config = newRelicConfig();

        log.info ("Setting up New Relic registry for application: {}", applicationName);
        NewRelicRegistry newRelicRegistry =
                NewRelicRegistry.builder(config)
                        .commonAttributes(
                                new Attributes()
                                        .put("host", InetAddress.getLocalHost().getHostName()))
                        .build();
        newRelicRegistry.config().meterFilter(MeterFilter.ignoreTags("plz_ignore_me"));
        newRelicRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("jvm.threads"));
        newRelicRegistry.start(new NamedThreadFactory("newrelic.micrometer.registry"));

        return newRelicRegistry;
    }
}
