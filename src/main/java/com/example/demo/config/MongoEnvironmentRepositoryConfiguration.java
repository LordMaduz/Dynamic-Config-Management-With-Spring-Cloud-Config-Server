package com.example.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.example.demo.client.MongoClient;
import com.example.demo.repos.MongoEnvironmentRepository;
import com.example.demo.repos.MongoEnvironmentRepositoryFactory;

@Configuration(proxyBeanMethods = false)
@Import({ MongoRepositoryConfiguration.class})
public class MongoEnvironmentRepositoryConfiguration extends EnvironmentRepositoryConfiguration {

    public MongoEnvironmentRepositoryConfiguration() {
        super();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(MongoClient.class)
    static class MongoFactoryConfig {

        @Bean
        public MongoEnvironmentRepositoryFactory mongoEnvironmentRepositoryFactory(ConfigServerProperties configServerProperties) {
            return new MongoEnvironmentRepositoryFactory(configServerProperties);
        }

    }

}

@Configuration(proxyBeanMethods = false)
@Profile("mongo")
@ConditionalOnClass(MongoClient.class)
class MongoRepositoryConfiguration {

    @Bean
    @ConditionalOnMissingBean(MongoEnvironmentRepository.class)
    public MongoEnvironmentRepository mongoEnvironmentRepository(MongoEnvironmentRepositoryFactory mongoEnvironmentRepositoryFactory, MongoEnvironmentProperties mongoEnvironmentProperties) {
        return mongoEnvironmentRepositoryFactory.build(mongoEnvironmentProperties);
    }
}
