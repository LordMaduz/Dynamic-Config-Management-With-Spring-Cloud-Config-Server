package com.example.demo.client;

import org.bson.Document;

import com.example.demo.config.MongoEnvironmentProperties;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MongoClient {

    public MongoCollection<Document> build(MongoEnvironmentProperties environmentProperties) {
        try (com.mongodb.client.MongoClient client = MongoClients.create(environmentProperties.getConnectionString())) {
            return client.getDatabase(environmentProperties.getDatabase())
                .getCollection(environmentProperties.getCollectionName());
        }
    }

}
