package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.server.support.EnvironmentRepositoryProperties;
import org.springframework.core.Ordered;

@ConfigurationProperties("spring.cloud.config.server.couchbase")
@Setter
@Getter
public class CouchbaseEnvironmentProperties implements EnvironmentRepositoryProperties {

    private String connectionString;
    private String userName;
    private String password;
    private String bucketName;
    private String scopeName;
    private String collectionName;
    private int order = Ordered.LOWEST_PRECEDENCE;


    @Override
    public void setOrder(int order) {
        this.order = order;
    }
}
