package com.example.project.csr.parser.service;

import static com.example.project.csr.parser.container.Container.SAMPLE_CSR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.project.csr.parser.model.Csr;


@ExtendWith(MockitoExtension.class)
class ParsCsrServiceTest {

    @InjectMocks
    private ParsCsrService parsCsrService;

    @Test
    void shouldSuccessfullyParseValidCSR() throws IOException {
        // Given
        MultipartFile mockFile = new MockMultipartFile(
                "test.csr",
                "test.csr",
                "application/x-pem-file",
                SAMPLE_CSR.getBytes()
        );

        // When
        Csr result = parsCsrService.parsePKCS10CertificationRequest(mockFile);

        // Then
        assertNotNull(result);
        assertTrue(result.getSubject().contains("Country Name (C)= DE"));
        assertTrue(result.getSubject().contains("Organization Name (O)= MyCompany"));
        assertTrue(result.getSubject().contains("Common Name (CN)= www.domain.de"));
        assertEquals("SHA-256 with RSA", result.getPublicKeyAlgorithm());
    }


    @Test
    void shouldThrowExceptionForInvalidCSR() {
        // Given
        MultipartFile mockFile = new MockMultipartFile(
                "invalid.csr",
                "invalid.csr",
                "application/x-pem-file",
                "invalid content".getBytes()
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                parsCsrService.parsePKCS10CertificationRequest(mockFile)
        );
    }

    @Test
    void shouldHandleEmptyFile() {
        // Given
        MultipartFile mockFile = new MockMultipartFile(
                "empty.csr",
                "empty.csr",
                "application/x-pem-file",
                new byte[0]
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                parsCsrService.parsePKCS10CertificationRequest(mockFile)
        );
    }
}