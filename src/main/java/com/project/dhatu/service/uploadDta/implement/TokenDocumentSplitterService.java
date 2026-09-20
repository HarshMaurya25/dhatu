package com.project.dhatu.service.uploadDta.implement;

import com.project.dhatu.config.TokenSplitterConfig;
import com.project.dhatu.service.uploadDta.SplitterService;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenDocumentSplitterService implements SplitterService {

    private final TokenTextSplitter tokenTextSplitter;

    public TokenDocumentSplitterService(TokenSplitterConfig config) {
        this.tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(config.chunkSize())
                .withMinChunkSizeChars(config.minChunkSizeChars())
                .withMinChunkLengthToEmbed(config.minChunkLengthToEmbed())
                .withMaxNumChunks(config.maxNumChunks())
                .withKeepSeparator(config.keepSeparator())
                .build();
    }

    @Override
    public List<Document> splitDocument(List<Document> documents) {
        return tokenTextSplitter.split(documents);
    }
}
