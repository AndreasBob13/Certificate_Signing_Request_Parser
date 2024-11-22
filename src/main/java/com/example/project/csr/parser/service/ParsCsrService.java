package com.example.project.csr.parser.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.project.csr.parser.model.Csr;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for parsing and extracting information from PKCS#10 Certificate Signing Requests (CSR).
 * This class processes CSR files and extracts relevant information such as Subject Names,
 * Public Key Algorithms, and Subject Alternative Names.
 *
 */
@Service
@Slf4j
public class ParsCsrService {


    /**
     * Map containing standard X.500 attribute mappings for subject names
     */
    private static final Map<String, String> SUBJECT_NAME = new HashMap<>() {{
        put("2.5.4.6", "Country Name (C)");
        put("2.5.4.8", "State or Province Name (ST)");
        put("2.5.4.7", "Locality Name (L)");
        put("2.5.4.10", "Organization Name (O)");
        put("2.5.4.11", "Organizational Unit (OU)");
        put("2.5.4.3", "Common Name (CN)");
    }};

    /**
     * Map containing standard X.500 attribute mappings for subject alternative names
     */
    private static final Map<String, String> SUBJECT_ALTERNATIVE_NAMES  = new HashMap<>() {{
        // Standard X.500 Attribute
        put("2.5.4.4",  "Surname");
        put("2.5.4.42", "Given Name");
        put("2.5.4.12", "Title");
        put("1.2.840.113549.1.9.1", "Email Address");
    }};

    /**
     * Map containing algorithm OID mappings to their human-readable names
     */
    private static final Map<String, String> ALGORITHM_MAP = new HashMap<>() {{
        // Standard X.500 Attribute
        put("1.2.840.113549.1.1.5", "SHA-1 with RSA");
        put("1.2.840.113549.1.1.11", "SHA-256 with RSA");
        put("1.2.840.113549.1.1.12", "SHA-384 with RSA");
        put("1.2.840.113549.1.1.13", "SHA-512 with RSA");
        put("1.2.840.10045.4.3.2", "SHA-256 with ECDSA");
    }};

    /**
     * Parses a CSR file and extracts the contained information.
     *
     * @param csrFile MultipartFile containing the CSR in PEM format
     * @return Csr object containing the extracted information
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the input is not a valid PKCS#10 CSR
     */
    public Csr parsePKCS10CertificationRequest(MultipartFile csrFile) throws IOException {
        log.info("Starting to parse CSR file: {}", csrFile.getOriginalFilename());
        try (InputStreamReader reader = new InputStreamReader(csrFile.getInputStream())) {
            PEMParser pemParser = new PEMParser(reader);
            Object object = pemParser.readObject();
            if (object instanceof PKCS10CertificationRequest) {
                log.debug("Successfully parsed CSR file as PKCS#10");
                return extractCsr((PKCS10CertificationRequest) object);
            } else {
                log.error("Failed to parse CSR: Input is not a valid PKCS#10 CSR");
                throw new IllegalArgumentException("Input is not a valid PKCS#10 CSR");
            }
        } catch (CertificateException | NoSuchProviderException e) {
            log.error("Error processing CSR: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process CSR: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts information from a PKCS10CertificationRequest.
     *
     * @param csr The CSR to process
     * @return Csr object containing the extracted information
     * @throws CertificateException if certificate processing fails
     * @throws NoSuchProviderException if the security provider is not available
     */
    private Csr extractCsr(PKCS10CertificationRequest csr) throws CertificateException, NoSuchProviderException {
        log.debug("Extracting information from CSR");
        List<String> subject = getSubjectNames(csr);
        String publicKeyAlgorithm = getKeyAlgorithm(csr);
        List<String> subjectAltNames = getSubjectAltNames(csr);

        log.info("Successfully extracted CSR information. Subject names count: {}, Algorithm: {}, Alt Names count: {}",
                subject.size(), publicKeyAlgorithm, subjectAltNames.size());
        return new Csr(subject, publicKeyAlgorithm, subjectAltNames);
    }

    /**
     * Extracts subject names from the CSR.
     *
     * @param csr The CSR to process
     * @return List of subject names in the format "type= value"
     */
    private List<String> getSubjectNames(PKCS10CertificationRequest csr) {
        log.debug("Processing subject names");
        return processRDNs(csr, SUBJECT_NAME);
    }

    /**
     * Extracts subject alternative names from the CSR.
     *
     * @param csr The CSR to process
     * @return List of subject alternative names in the format "type= value"
     */
    private List<String> getSubjectAltNames(PKCS10CertificationRequest csr) {
        log.debug("Processing subject alternative names");
        return processRDNs(csr, SUBJECT_ALTERNATIVE_NAMES);
    }

    /**
     * Processes Relative Distinguished Names (RDNs) from the CSR using the provided mapping.
     *
     * @param csr The CSR to process
     * @param nameMappings Map of OID to human-readable name mappings
     * @return List of processed names in the format "type= value"
     */
    private List<String> processRDNs(PKCS10CertificationRequest csr, Map<String, String> nameMappings) {
        List<String> result = new ArrayList<String>();
        for(RDN rdn: csr.getSubject().getRDNs()){
            String rdnType = rdn.getFirst().getType().toString();
            String rdnValue = rdn.getFirst().getValue().toString();

            if(nameMappings.containsKey(rdnType)){
                result.add(nameMappings.get(rdnType) + "= " + rdnValue);
                log.trace("Processed RDN: {}= {}", nameMappings.get(rdnType), rdnValue);
            } else {
                log.debug("Following Subject  name Id: " + rdnType + " not present");
            }
        }
        return result;
    }


    /**
     * Extracts the key algorithm from the CSR.
     *
     * @param csr The CSR to process
     * @return String representation of the key algorithm
     *
     * */
    private String getKeyAlgorithm(PKCS10CertificationRequest csr) {
        String algorithmId = csr.getSignatureAlgorithm().getAlgorithm().getId();
        if (ALGORITHM_MAP.containsKey(algorithmId)) {
            String algorithm = ALGORITHM_MAP.get(algorithmId);
            log.debug("Identified key algorithm: {}", algorithm);
            return algorithm;
        } else {
            log.info("Unrecognized Algorithm ID: {}", algorithmId);
            return "Unknown Algorithm";
        }
    }


}
