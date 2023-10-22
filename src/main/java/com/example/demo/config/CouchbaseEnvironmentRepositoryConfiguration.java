package com.example.demo.config;

import com.example.demo.client.CouchbaseClient;
import com.example.demo.repos.CouchbaseEnvironmentRepository;
import com.example.demo.repos.CouchbaseEnvironmentRepositoryFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Import({CouchbaseRepositoryConfiguration.class})
public class CouchbaseEnvironmentRepositoryConfiguration extends EnvironmentRepositoryConfiguration {

    public CouchbaseEnvironmentRepositoryConfiguration() {
        super();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(CouchbaseClient.class)
    static class CouchbaseFactoryConfig {

        @Bean
        public CouchbaseEnvironmentRepositoryFactory couchbaseEnvironmentRepositoryFactory(ConfigServerProperties configServerProperties) {
            return new CouchbaseEnvironmentRepositoryFactory(configServerProperties);
        }


    }

}

@Configuration(proxyBeanMethods = false)
@Profile("couchbase")
@ConditionalOnClass(CouchbaseClient.class)
class CouchbaseRepositoryConfiguration {

    @Bean
    @ConditionalOnMissingBean(CouchbaseEnvironmentRepository.class)
    public CouchbaseEnvironmentRepository couchbaseEnvironmentRepository(CouchbaseEnvironmentRepositoryFactory couchbaseEnvironmentRepositoryFactory, CouchbaseEnvironmentProperties couchbaseEnvironmentProperties) {
        return couchbaseEnvironmentRepositoryFactory.build(couchbaseEnvironmentProperties);
    }
}
