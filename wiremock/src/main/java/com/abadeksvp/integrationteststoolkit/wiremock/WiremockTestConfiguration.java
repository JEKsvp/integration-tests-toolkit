package com.abadeksvp.integrationteststoolkit.wiremock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WiremockTestConfiguration {

    @Bean
    WireMockConfigurationCustomizer optionsCustomizer() {
        return options -> options.extensions(new AsyncAwaiterPostServeAction(), new NoKeepAliveTransformer());
    }
}
