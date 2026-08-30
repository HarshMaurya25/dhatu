package com.project.dhatu.service.implement;

import com.project.dhatu.service.DataInputService;
import com.project.dhatu.service.SplitterService;
import com.project.dhatu.service.ToDocumentService;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class MultipartfileDataInputService implements DataInputService {

    @Value("admin.code")
    private String secretCode;

    private ToDocumentService toDocumentService;
    private SplitterService splitterService;

    @Override
    public Boolean uploadPDF(MultipartFile pdf, String code) {

        if (!code.equals(secretCode)){
            return Boolean.FALSE;
        }

        if (pdf.isEmpty() || Objects.equals(pdf.getContentType(), "pdf")){
            return Boolean.FALSE;
        }

        List<Document> documents = toDocumentService.toDocument(pdf);
        List<Document> splitdocument = splitterService.splitDocument(documents);

        System.out.println(splitdocument.toString());

        return null;
    }

}
