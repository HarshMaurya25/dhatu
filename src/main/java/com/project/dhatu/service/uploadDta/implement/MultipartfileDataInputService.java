package com.project.dhatu.service.uploadDta.implement;

import com.project.dhatu.service.uploadDta.DataInputService;
import com.project.dhatu.service.uploadDta.ToDocumentService;
import com.project.dhatu.service.uploadDta.SplitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class MultipartfileDataInputService implements DataInputService {

    private static final Logger log = LoggerFactory.getLogger(MultipartfileDataInputService.class);

    @Value("${admin.code}")
    private String secretCode;

    private final ToDocumentService toDocumentService;
    private final SplitterService splitterService;
    private final VectorStore vectorStore;

    public MultipartfileDataInputService(ToDocumentService toDocumentService, SplitterService splitterService, VectorStore vectorStore) {
        this.toDocumentService = toDocumentService;
        this.splitterService = splitterService;
        this.vectorStore = vectorStore;
    }

    @Override
    public Boolean uploadPDF(MultipartFile pdf, String code) {

        log.info("PDF with code {} is provided", code);
        if (!code.equals(secretCode)){
            log.info("PDF with code {} is rejected", code);
            return Boolean.FALSE;
        }

        if (pdf.isEmpty() || Objects.equals(pdf.getContentType(), "pdf")){
            return Boolean.FALSE;
        }

        List<Document> documents = toDocumentService.toDocument(pdf);
        List<Document> splitdocument = splitterService.splitDocument(documents);

//        this.vectorStore.add(splitdocument);
        return null;
    }

}
