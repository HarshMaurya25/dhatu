package com.project.dhatu.service;

import com.project.dhatu.service.uploadDta.SplitterService;
import com.project.dhatu.service.uploadDta.ToDocumentService;
import com.project.dhatu.service.uploadDta.implement.MultipartfileDataInputService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultipartfileDataInputServiceTest {

    @Mock
    private ToDocumentService toDocumentService;

    @Mock
    private SplitterService splitterService;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private DocumentTransformer whitespaceCleanerTransformer;

    private MultipartfileDataInputService service;

    private static final String SECRET_CODE = "123456";

    @BeforeEach
    void setUp() {
        service = new MultipartfileDataInputService(
                toDocumentService,
                splitterService,
                vectorStore,
                whitespaceCleanerTransformer
        );
        ReflectionTestUtils.setField(service, "secretCode", SECRET_CODE);
    }

    @Test
    void shouldRejectEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]);

        Boolean result = service.uploadPDF(emptyFile, SECRET_CODE);

        assertFalse(result);
    }

    @Test
    void shouldRejectNonPdfContentType() {
        MockMultipartFile jpegFile = new MockMultipartFile(
                "file", "image.jpg", "image/jpeg", "fake image content".getBytes());

        Boolean result = service.uploadPDF(jpegFile, SECRET_CODE);

        assertFalse(result);
    }

    @Test
    void shouldRejectNullContentType() {
        MockMultipartFile nullContentTypeFile = new MockMultipartFile(
                "file", "test.pdf", null, "some content".getBytes());

        Boolean result = service.uploadPDF(nullContentTypeFile, SECRET_CODE);

        assertFalse(result);
    }

    @Test
    void shouldProcessValidPdfAndReturnTrue() {
        MockMultipartFile validPdf = new MockMultipartFile(
                "file", "sample.pdf", "application/pdf", "%PDF-1.4 sample content".getBytes());

        Document doc = new Document("sample content");
        List<Document> docs = List.of(doc);

        when(toDocumentService.toDocument(validPdf)).thenReturn(docs);
        when(whitespaceCleanerTransformer.transform(docs)).thenReturn(docs);
        when(splitterService.splitDocument(docs)).thenReturn(docs);

        Boolean result = service.uploadPDF(validPdf, SECRET_CODE);

        assertTrue(result);
        verify(vectorStore).add(docs);
    }
}
