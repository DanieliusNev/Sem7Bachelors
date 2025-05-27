package com.example.bachelorsrealwear.domain.repository;

import java.io.File;

public interface CloudUploadRepository {
    void uploadPdf(File file, String fileName);
}
