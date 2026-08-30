package com.project.dhatu.service.implement;

import com.project.dhatu.service.ToDocumentService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ParagraphPDFReaderService implements ToDocumentService {

    @Override
    public List<Document> toDocument(MultipartFile file) {
        ParagraphPdfDocumentReader pdfReader =
                new ParagraphPdfDocumentReader(file.getResource(),
                        PdfDocumentReaderConfig
                                .builder()
                                .withPageTopMargin(0)
                                .withPageExtractedTextFormatter(ExtractedTextFormatter
                                        .builder()
                                        .withNumberOfTopTextLinesToDelete(0)
                                        .build()
                                )
                                .withPagesPerDocument(1)
                                .build()
                        );

        return pdfReader.read();

    }
}
