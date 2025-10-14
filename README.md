# Spring Cloud Config Server

> Centralized configuration management server with multi-backend support (Git, Couchbase) and dynamic refresh capabilities using Spring Cloud Bus.

## Overview

A production-ready Spring Cloud Config Server that provides centralized configuration management for distributed microservices. Supports multiple backend storage options with composite configuration, enabling hybrid deployment scenarios where some configurations are stored in Git while others in Couchbase.

**Key Features:**
- Multi-backend configuration storage (Git + Couchbase)
- Dynamic configuration refresh without restart
- Spring Cloud Bus integration with Kafka
- Environment-specific configuration profiles
- Configuration change propagation to all clients
- Custom Couchbase environment repository implementation
- RESTful configuration API

---

## Architecture
<img width="2086" height="1520" alt="Mermaid Chart - Create complex, visual diagrams with text -2025-10-14-101443" src="https://github.com/user-attachments/assets/714f2b83-e398-4daa-a6df-433d21845683" />

---

## Dynamic Refresh Flow

<img width="2706" height="1674" alt="Mermaid Chart - Create complex, visual diagrams with text -2025-10-14-101223" src="https://github.com/user-attachments/assets/8ef9e4e1-8677-4882-a06c-fe8179d7a18c" />

## Tech Stack

| Category | Technologies |
|----------|-------------|
| **Core** | Java 17, Spring Boot 3.1.5 |
| **Config** | Spring Cloud Config Server 2022.0.4 |
| **Messaging** | Spring Cloud Bus, Apache Kafka |
| **Database** | Couchbase (Reactive) |
| **Reactive** | Spring WebFlux, Project Reactor |
| **Monitoring** | Spring Actuator |

---

## 📋 Getting Started

### Prerequisites
```bash
Java 17+
Maven 3.8+
Couchbase Server 7.0+
Apache Kafka 3.x
Git repository for configuration files


### Infrastructure Setup

#### 1. Start Couchbase
```bash
docker run -d --name couchbase \
  -p 8091-8096:8091-8096 \
  -p 11210:11210 \
  couchbase:latest

# Access UI at http://localhost:8091
# Create bucket: configuration-service
# Create scope: technical
# Create collection: application-properties
```
#### 2. Start Kafka
```bash
# Using Docker Compose or standalone
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

##### Create topic
```
bin/kafka-topics.sh --create \
  --topic spring_cloud_config_channel \
  --bootstrap-server localhost:9095 \
  --partitions 3 \
  --replication-factor 1
```
#### 3. Setup Git Repository
Create a Git repository with configuration files:
```yml
spring-cloud-config-repository/
├── application-dev/
│   └── application.yml
├── application-prod/
│   └── application.yml
└── myservice/
    ├── dev/
    │   └── myservice.yml
    └── prod/
        └── myservice.yml
```
### Installation
#### 1. Clone and Build
```bash
git clone <repository-url>
cd spring-cloud-config-server
mvn clean install
```

#### 2. Configure Application

application.yaml:
```yaml
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        composite:
          # Couchbase backend (Priority 1)
          - type: couchbase
            connectionString: couchbase://localhost
            userName: Administrator
            password: password
            bucketName: configuration-service
            scopeName: technical
            collectionName: application-properties
            order: 1
          # Git backend (Priority 2)
          - type: git
            uri: https://github.com/username/config-repo.git
            search-paths:
              - '{application}/{profile}'
            order: 2
    bus:
      enabled: true
      id: my-config-server
      destination: spring_cloud_config_channel
    stream:
      kafka:
        binder:
          brokers: localhost:9095

management:
  endpoints:
    web:
      exposure:
        include: health, busrefresh, bus-env, refresh, env
```

#### 3. Run Config Server
```bash
mvn spring-boot:run
```
Server starts on http://localhost:8080

### Fetch Configuration
Request:

```bash
# Pattern: /{application}/{profile}[/{label}]
curl http://localhost:8080/myservice/dev
```

Response:
```json
{
  "name": "myservice",
  "profiles": ["dev"],
  "label": null,
  "version": "abc123",
  "state": null,
  "propertySources": [
    {
      "name": "couchbasemyservice",
      "source": {
        "database.url": "jdbc:mysql://localhost:3306/dev",
        "database.username": "dev_user"
      }
    },
    {
      "name": "https://github.com/.../myservice-dev.yml",
      "source": {
        "app.name": "My Service",
        "app.version": "1.0.0"
      }
    }
  ]
}
```

### Refresh All Clients
Request:

```bash
curl -X POST http://localhost:8080/actuator/busrefresh
```

### Client Configuration
Bootstrap Configuration
bootstrap.yml:

```yaml
spring:
  application:
    name: myservice
  profiles:
    active: dev
  cloud:
    config:
      uri: http://localhost:8080
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
    bus:
      enabled: true
```

#### Enable Refresh Scope
```java
@RestController
@RefreshScope  // Enable dynamic refresh
public class MyController {
    
    @Value("${database.url}")
    private String databaseUrl;
    
    @GetMapping("/config")
    public String getConfig() {
        return databaseUrl;
    }
}
```

## Key Features

### Custom Couchbase Environment Repository

Implements Spring Cloud Config's `EnvironmentRepository` interface:

```java
public class CouchbaseEnvironmentRepository implements EnvironmentRepository, Ordered {

    private final Collection collection;
    private int order;

    @Override
    public Environment findOne(String application, String profile, String label) {
        Environment environment = new Environment(application, profile, label, null, null);
        
        // Check if configuration exists in Couchbase
        if (collection.exists(application).exists()) {
            GetResult result = collection.get(application);
            Map properties = flattenJson(result);
            environment.add(new PropertySource("couchbase" + application, properties));
        }
        
        return environment;
    }

    private Map flattenJson(GetResult result) {
        JsonNode node = objectMapper.convertValue(
            result.contentAs(JsonNode.class), 
            new TypeReference<>() {});
        return JsonMapFlattener.flatten(node);
    }
}
```

### Configuration Factory

```java
public class CouchbaseEnvironmentRepositoryFactory 
        implements EnvironmentRepositoryFactory {

    @Override
    public CouchbaseEnvironmentRepository build(CouchbaseEnvironmentProperties props) {
        Collection collection = new CouchbaseClient().build(props);
        CouchbaseEnvironmentRepository repository = 
            new CouchbaseEnvironmentRepository(collection, props);
        repository.setOrder(props.getOrder());
        return repository;
    }
}
```

### Composite Configuration

Spring Cloud Config checks backends in order until configuration is found:

```yaml
composite:
  - type: couchbase    # Checked first (order: 1)
    order: 1
  - type: git          # Checked second (order: 2)
    order: 2
```

## Learn More

For detailed implementation guides and best practices, check out following medium article:

**Spring Cloud Config Deep Dives:**

[Building Centralized Configuration with Spring Cloud Config](https://blog.stackademic.com/couchbase-as-a-custom-composite-environment-repository-in-spring-cloud-config-server-e94606e6272f)

