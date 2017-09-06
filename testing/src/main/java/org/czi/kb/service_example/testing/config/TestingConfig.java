package org.czi.kb.service_example.testing.config;

import org.czi.rato.testing.service.ServiceTestRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Dave Ellery on 29/01/17.
 * ServiceTestRunner lives in a different package in RATO,
 * the service should never component scan org.czi.rato packages
 * this is a simple work around to expose it in the services spring context.
 */
@Configuration
@Profile("SERVICE_TEST")
public class TestingConfig {

    @Bean
    public ServiceTestRunner getServiceTestRunner() {
        return new ServiceTestRunner();
    }

}
