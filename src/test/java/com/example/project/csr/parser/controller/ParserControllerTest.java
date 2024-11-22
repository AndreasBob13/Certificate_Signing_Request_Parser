package com.example.project.csr.parser.controller;

import static com.example.project.csr.parser.container.Container.SAMPLE_CSR;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Unit tests for the CSR Parser Controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ParserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private MockMultipartFile validCsrFile;
    private MockMultipartFile emptyFile;

    @BeforeEach
    void setUp() {

        validCsrFile = new MockMultipartFile(
                "csr",
                "test.csr",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                SAMPLE_CSR.getBytes()
        );

        emptyFile = new MockMultipartFile(
                "csr",
                "empty.csr",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new byte[0]
        );
    }

    @Test
    void parseCsr_ValidFile_ReturnsSuccessResponse() throws Exception {

        mockMvc.perform(multipart("/api/parse-csr")
                        .file(validCsrFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject", hasSize(6)))
                .andExpect(jsonPath("$.subject[0]", is( "Country Name (C)= DE")))
                .andExpect(jsonPath("$.subject[1]", is("State or Province Name (ST)= Berlin")))
                .andExpect(jsonPath("$.subject[2]", is("Locality Name (L)= Berlin")))
                .andExpect(jsonPath("$.subject[3]", is("Organization Name (O)= MyCompany")))
                .andExpect(jsonPath("$.subject[4]", is("Organizational Unit (OU)= IT")))
                .andExpect(jsonPath("$.subject[5]", is("Common Name (CN)= www.domain.de")))
                .andExpect(jsonPath("$.publicKeyAlgorithm", is("SHA-256 with RSA")))
                .andExpect(jsonPath("$.subjectAltName", hasSize(1)))
                .andExpect(jsonPath("$.subjectAltName[0]", is("Email Address=  info@domain.de")));
    }

    @Test
    void parseCsr_EmptyFile_ThrowsIllegalArgumentException() throws Exception {
        mockMvc.perform(multipart("/api/parse-csr")
                        .file(emptyFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void parseCsr_InvalidCsr_ThrowsIllegalArgumentException() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "csr",
                "invalid.csr",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "invalid content".getBytes()
        );

        mockMvc.perform(multipart("/api/parse-csr")
                        .file(invalidFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }


    @Test
    void parseCsr_MissingFile_ReturnsBadRequest() throws Exception {
        mockMvc.perform(multipart("/api/parse-csr"))
                .andExpect(status().isBadRequest());
    }

}