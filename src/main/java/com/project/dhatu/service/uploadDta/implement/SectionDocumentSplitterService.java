package com.project.dhatu.service.uploadDta.implement;

import com.project.dhatu.config.TokenSplitterConfig;
import com.project.dhatu.service.uploadDta.SplitterService;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SectionDocumentSplitterService implements SplitterService {
    private final TokenSplitterConfig tokenSplitterConfig;
    private final TokenTextSplitter tokenTextSplitter;

    private static final Pattern SECTION_PATTERN = Pattern.compile(
            "(?=\\d+\\.\\d+\\s+[A-Z])",
            Pattern.MULTILINE
    );

    public SectionDocumentSplitterService(TokenSplitterConfig tokenSplitterConfig) {
        this.tokenSplitterConfig = tokenSplitterConfig;
        this.tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(tokenSplitterConfig.chunkSize())
                .withMinChunkSizeChars(tokenSplitterConfig.minChunkSizeChars())
                .withMinChunkLengthToEmbed(tokenSplitterConfig.minChunkLengthToEmbed())
                .withMaxNumChunks(tokenSplitterConfig.maxNumChunks())
                .withKeepSeparator(tokenSplitterConfig.keepSeparator())
                .build();
    }

    @Override
    public List<Document> splitDocument(List<Document> documents) {
        List<Document> sectionChunks = documents.stream()
                .flatMap(doc -> splitIntoSections(doc).stream())
                .collect(Collectors.toList());

        return tokenTextSplitter.split(sectionChunks);
    }

    private List<Document> splitIntoSections(Document document) {
        String content = document.getText();
        String[] sections = SECTION_PATTERN.split(content);

        List<Document> result = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int maxChars = tokenSplitterConfig.chunkSize() * 4;

        for (String section : sections) {
            if (isJunk(section)) continue;
            if (section.length() > maxChars) {
                if (!currentChunk.isEmpty()) {
                    result.add(new Document(
                            currentChunk.toString().trim(),
                            new HashMap<>(document.getMetadata())));
                    currentChunk = new StringBuilder();
                }
                result.add(new Document(
                        section.trim(),
                        new HashMap<>(document.getMetadata())));
                continue;
            }
            if (currentChunk.length() + section.length() > maxChars) {
                result.add(new Document(
                        currentChunk.toString().trim(),
                        new HashMap<>(document.getMetadata())));
                currentChunk = new StringBuilder();
            }
            currentChunk.append(section).append("\n\n");
        }

        if (!currentChunk.isEmpty()) {
            result.add(new Document(
                    currentChunk.toString().trim(),
                    new HashMap<>(document.getMetadata())));
        }

        return result.isEmpty() ? List.of(document) : result;
    }

    private boolean isJunk(String section) {
        String trimmed = section.trim();
        return trimmed.isEmpty()
                || trimmed.length() < 100
                || trimmed.startsWith("A Combined Knowledge")
                || trimmed.startsWith("Manual of the Geology");
    }
}
