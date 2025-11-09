package com.example.legoproject.configs;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlobConfig {

    @Value("${blob.container.tkn}")
    private String token;

    @Value("${blob.container.uri}")
    private String uri;

    @Bean
    public BlobContainerClient blobContainerClient() {
        return new BlobContainerClientBuilder()
                .endpoint(uri)
                        //.credential(new DefaultAzureCredentialBuilder().build())
                        .buildClient();
    }

}
