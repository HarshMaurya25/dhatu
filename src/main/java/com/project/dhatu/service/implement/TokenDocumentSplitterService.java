package com.project.dhatu.service.implement;

import com.project.dhatu.config.TokenSplitterConfig;
import com.project.dhatu.service.SplitterService;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenDocumentSplitterService implements SplitterService {

    private final TokenSplitterConfig tokenSplitterConfig;
    public TokenDocumentSplitterService(TokenSplitterConfig tokenSplitterConfig) {
        this.tokenSplitterConfig = tokenSplitterConfig;
    }

    @Override
    public List<Document> splitDocument(List<Document> documents) {
        TokenTextSplitter textSplitter = TokenTextSplitter
                .builder()
                .withChunkSize(tokenSplitterConfig.getChunkSize())
                .withMinChunkSizeChars(tokenSplitterConfig.getMinChunkSizeChars())
                .withMinChunkLengthToEmbed(tokenSplitterConfig.getMinChunkLengthToEmbed())
                .withMaxNumChunks(tokenSplitterConfig.getMaxNumChunks())
                .withKeepSeparator(tokenSplitterConfig.isKeepSeparator())
                .build();

        return textSplitter.split(documents);
    }
}
