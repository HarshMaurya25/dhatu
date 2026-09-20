package com.project.dhatu.service.uploadDta.implement;

import com.project.dhatu.service.uploadDta.DataInputService;
import com.project.dhatu.service.uploadDta.ToDocumentService;
import com.project.dhatu.service.uploadDta.SplitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
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
    private final DocumentTransformer whitespaceCleanerTransformer;

    public MultipartfileDataInputService(ToDocumentService toDocumentService, SplitterService splitterService, VectorStore vectorStore, DocumentTransformer whitespaceCleanerTransformer) {
        this.toDocumentService = toDocumentService;
        this.splitterService = splitterService;
        this.vectorStore = vectorStore;
        this.whitespaceCleanerTransformer = whitespaceCleanerTransformer;
    }

    private static final String PDF_CONTENT_TYPE = "application/pdf";

    @Override
    public Boolean uploadPDF(MultipartFile pdf, String code) {

        log.info("PDF with code {} is provided", code);
        if (!code.equals(secretCode)){
            log.info("PDF with code {} is rejected", code);
            return Boolean.FALSE;
        }

        if (pdf.isEmpty() || !PDF_CONTENT_TYPE.equals(pdf.getContentType())) {
            return Boolean.FALSE;
        }

        log.info("Processing PDF: name={}, size={} bytes", pdf.getOriginalFilename(), pdf.getSize());

        List<Document> documents = toDocumentService.toDocument(pdf);
        documents = whitespaceCleanerTransformer.transform(documents);
        List<Document> splitdocument = splitterService.splitDocument(documents);

        this.vectorStore.add(splitdocument);
        log.info("Stored {} chunks for file '{}'", splitdocument.size(), pdf.getOriginalFilename());

        return Boolean.TRUE;
    }

}
