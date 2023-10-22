package com.example.demo.repos;

import com.couchbase.client.java.Collection;
import com.example.demo.client.CouchbaseClient;
import com.example.demo.config.CouchbaseEnvironmentProperties;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.environment.EnvironmentRepositoryFactory;

public class CouchbaseEnvironmentRepositoryFactory implements EnvironmentRepositoryFactory<CouchbaseEnvironmentRepository, CouchbaseEnvironmentProperties> {

    private ConfigServerProperties configServerProperties;

    public CouchbaseEnvironmentRepositoryFactory(ConfigServerProperties configServerProperties){
        this.configServerProperties = configServerProperties;
    }

    @Override
    public CouchbaseEnvironmentRepository build(CouchbaseEnvironmentProperties environmentProperties){
       final Collection collection = new CouchbaseClient().build(environmentProperties);
       CouchbaseEnvironmentRepository repository = new CouchbaseEnvironmentRepository(collection, environmentProperties);

       repository.setOrder(environmentProperties.getOrder());
       return  repository;
    }
}
