package com.example.project.csr.parser.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.project.csr.parser.exceptions.throwable.InvalidCsrFileExceptions;
import com.example.project.csr.parser.model.Csr;
import com.example.project.csr.parser.service.ParsCsrService;

import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for handling Certificate Signing Request (CSR) file operations.
 * Provides endpoints for parsing and analyzing CSR files.
 *
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ParserController {

    private final ParsCsrService parsCsrService;

    /**
     * Constructs a new ParserController with the required service dependency.
     *
     * @param parsCsrService The service responsible for parsing CSR files
     */
    @Autowired
    public ParserController(ParsCsrService parsCsrService) {
        this.parsCsrService = parsCsrService;
    }


    /**
     * Endpoint for parsing a Certificate Signing Request (CSR) file.
     * Accepts a CSR file in PEM format and returns the parsed information.
     *
     * @param multipartFile The CSR file to be parsed (must be in PEM format)
     * @return ResponseEntity containing the parsed CSR information
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the file is not a valid CSR
     */
    @PostMapping(value = "/parse-csr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Csr> parseCsr(@RequestParam("csr") MultipartFile multipartFile) throws IOException {
        log.info("Received CSR parsing request for file: {}, size: {} bytes",
                multipartFile.getOriginalFilename(),
                multipartFile.getSize());

        if (multipartFile.isEmpty()) {
            log.error("Received empty file");
            throw new InvalidCsrFileExceptions("The provided file is empty", HttpStatus.BAD_REQUEST );
        }

        try {
            Csr csr = parsCsrService.parsePKCS10CertificationRequest(multipartFile);
            log.info("Successfully parsed CSR file. Subject count: {}", csr.getSubject().size());
            return new ResponseEntity<>(csr, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid CSR file provided: {}", e.getMessage());
            throw new InvalidCsrFileExceptions(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error processing CSR file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process CSR file", e);
        }
    }

}
