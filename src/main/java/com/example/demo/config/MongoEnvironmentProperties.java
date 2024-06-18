package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.server.support.EnvironmentRepositoryProperties;
import org.springframework.core.Ordered;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("spring.cloud.config.server.mongo")
@Setter
@Getter
public class MongoEnvironmentProperties implements EnvironmentRepositoryProperties {

    private String connectionString;
    private String database;
    private String collectionName;
    private int order = Ordered.LOWEST_PRECEDENCE;


    @Override
    public void setOrder(int order) {
        this.order = order;
    }
}
