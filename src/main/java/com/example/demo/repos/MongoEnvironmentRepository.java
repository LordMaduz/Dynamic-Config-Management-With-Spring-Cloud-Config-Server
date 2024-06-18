package com.example.demo.repos;

import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.vault.support.JsonMapFlattener;

import com.example.demo.config.MongoEnvironmentProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class MongoEnvironmentRepository implements EnvironmentRepository, Ordered {

    private static final String APPLICATION_ID = "application_Id";

    @Autowired
    private ObjectMapper objectMapper;

    private int order;
    private MongoCollection<Document> mongoCollection;

    public MongoEnvironmentRepository(MongoCollection<Document> mongoCollection, MongoEnvironmentProperties mongoEnvironmentProperties) {
        this.mongoCollection = mongoCollection;
        this.order = mongoEnvironmentProperties.getOrder();
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        Environment environment = new Environment(application, profile, label, null, null);

        Document document = mongoCollection.find(Filters.eq(APPLICATION_ID, application))
            .first();
        if (document != null) {
            environment.add(new PropertySource("couchbase" + application, getResultMap(document)));
        }

        return environment;
    }

    public Map<String, Object> getResultMap(Document document) {
        return JsonMapFlattener.flatten(objectMapper.convertValue(document, new TypeReference<>() {
        }));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
