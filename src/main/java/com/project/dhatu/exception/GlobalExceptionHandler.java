package com.project.dhatu.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Void> handleFileTooLarge(MaxUploadSizeExceededException ex) {
        // Frontend admin.html already catches 413 and shows t.errTooLarge
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Void> handleMultipart(MultipartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);
        // Frontend admin.html catches !res.ok and shows t.errServer
        return ResponseEntity.internalServerError().build();
    }
}
