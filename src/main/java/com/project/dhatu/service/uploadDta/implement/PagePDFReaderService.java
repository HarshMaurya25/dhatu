package com.project.dhatu.service.uploadDta.implement;

import com.project.dhatu.service.uploadDta.ToDocumentService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Primary
public class PagePDFReaderService implements ToDocumentService {
    @Override
    public List<Document> toDocument(MultipartFile file) {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(file.getResource(),
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());

        return pdfReader.read();
    }
}
