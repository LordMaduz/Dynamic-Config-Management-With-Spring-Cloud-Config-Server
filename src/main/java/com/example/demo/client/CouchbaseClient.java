package com.example.demo.client;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.example.demo.config.CouchbaseEnvironmentProperties;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CouchbaseClient {

    public Collection build(CouchbaseEnvironmentProperties couchbaseEnvironmentProperties) {
        return Cluster.connect(
                        couchbaseEnvironmentProperties.getConnectionString(),
                        couchbaseEnvironmentProperties.getUserName(),
                        couchbaseEnvironmentProperties.getPassword())
                .bucket(couchbaseEnvironmentProperties.getBucketName())
                .scope(couchbaseEnvironmentProperties.getScopeName())
                .collection(couchbaseEnvironmentProperties.getCollectionName());
    }
}
