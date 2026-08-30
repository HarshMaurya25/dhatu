package com.project.dhatu.service.uploadDta;

import org.springframework.web.multipart.MultipartFile;

public interface DataInputService {

    Boolean uploadPDF(MultipartFile pdf, String code);

}
