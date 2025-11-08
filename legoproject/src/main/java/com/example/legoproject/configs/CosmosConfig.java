package com.example.legoproject.configs;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosmosConfig {
    @Value("${azure.cosmos.uri}")
    private String uri;

    @Value("${azure.cosmos.key}")
    private String key;

    @Value("${azure.cosmos.database}")
    private String databaseName;

    @Bean
    public CosmosClient cosmosClient() {
        return new CosmosClientBuilder()
                .endpoint(uri)
                .key(key)
                .consistencyLevel(com.azure.cosmos.ConsistencyLevel.EVENTUAL)
                .buildClient();
    }

    @Bean
    public CosmosDatabase cosmosDB() {
        CosmosClient c = cosmosClient();

        return c.getDatabase(databaseName);
    }

    @Bean
    public String getDBName() {
        return databaseName;
    }
}
