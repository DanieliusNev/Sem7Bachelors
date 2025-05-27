package com.example.bachelorsrealwear.domain.service;

public interface PdfService {
    void fillAndSavePdf(String template, String mapping, String outputName) throws Exception;
}
