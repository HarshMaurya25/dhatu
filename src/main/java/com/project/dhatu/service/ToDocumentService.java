package com.project.dhatu.service;


import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ToDocumentService {

    List<Document> toDocument(MultipartFile file);

}
