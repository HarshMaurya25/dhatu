package com.project.dhatu.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface SplitterService {

    List<Document> splitDocument(List<Document> documents);
}
