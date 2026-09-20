package com.project.dhatu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vector.values")
public record VectorStoreConfig(
        Double similarityThreshold,
        int topK
) {}
