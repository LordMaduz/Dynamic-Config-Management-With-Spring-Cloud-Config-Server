package com.example.demo.repos;

import org.bson.Document;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.environment.EnvironmentRepositoryFactory;

import com.example.demo.client.MongoClient;
import com.example.demo.config.MongoEnvironmentProperties;
import com.mongodb.client.MongoCollection;

public class MongoEnvironmentRepositoryFactory implements EnvironmentRepositoryFactory<MongoEnvironmentRepository, MongoEnvironmentProperties> {

    private ConfigServerProperties configServerProperties;

    public MongoEnvironmentRepositoryFactory(ConfigServerProperties configServerProperties){
        this.configServerProperties = configServerProperties;
    }

    @Override
    public MongoEnvironmentRepository build(MongoEnvironmentProperties environmentProperties){
        final MongoCollection<Document> mongoCollection = new MongoClient().build(environmentProperties);
        MongoEnvironmentRepository repository = new MongoEnvironmentRepository(mongoCollection, environmentProperties);

        repository.setOrder(environmentProperties.getOrder());
        return  repository;
    }
}
