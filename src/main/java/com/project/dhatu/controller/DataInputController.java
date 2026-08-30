package com.project.dhatu.controller;

import com.project.dhatu.service.DataInputService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ai/admin")
public class DataInputController {

    private final DataInputService dataInputService;

    public DataInputController(DataInputService dataInputService) {
        this.dataInputService = dataInputService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Boolean> uploadPDF(
            @RequestBody(required = true)MultipartFile pdf,
            @RequestParam("code") String code
            ){

        return ResponseEntity
                .accepted()
                .body(dataInputService.uploadPDF(pdf , code));
    }
}
