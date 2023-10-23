package com.example.demo.repos;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.example.demo.config.CouchbaseEnvironmentProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.vault.support.JsonMapFlattener;

import java.util.Map;

public class CouchbaseEnvironmentRepository implements EnvironmentRepository, Ordered {

    private final Collection collection;
    private int order;

    @Autowired
    private ObjectMapper objectMapper;

    public CouchbaseEnvironmentRepository(Collection collection, CouchbaseEnvironmentProperties couchbaseEnvironmentProperties) {
        this.collection = collection;
        this.order = couchbaseEnvironmentProperties.getOrder();
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        Environment environment = new Environment(application, profile, label, null, null);
        ExistsResult existsResult = collection.exists(application);
        if (existsResult.exists()) {
            GetResult getResult = collection.get(application);
            environment.add(new PropertySource("couchbase" + application, getResultMap(getResult)));
        }
        return environment;
    }

    public Map<String, Object> getResultMap(GetResult getResult) {

        return JsonMapFlattener.flatten(objectMapper.convertValue(getResult.contentAs(JsonNode.class), new TypeReference<>() {
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
