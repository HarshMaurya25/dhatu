package com.project.dhatu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pdf.splitter")
public record TokenSplitterConfig(
        int chunkSize,
        int minChunkSizeChars,
        int minChunkLengthToEmbed,
        int maxNumChunks,
        boolean keepSeparator
) {}
