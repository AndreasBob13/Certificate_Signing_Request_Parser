package com.example.project.csr.parser.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Csr {
    private final List<String> subject;
    private final String publicKeyAlgorithm;
    private final List<String> subjectAltName;

    public Csr(List<String> subject, String publicKeyAlgorithm, List<String> subjectAltName) {
        this.subject = subject;
        this.publicKeyAlgorithm = publicKeyAlgorithm;
        this.subjectAltName = subjectAltName;
    }
}
