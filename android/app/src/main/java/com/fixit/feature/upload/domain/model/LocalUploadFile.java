package com.fixit.feature.upload.domain.model;

import java.io.File;


//Model đại diện cho file local Android chuẩn bị upload.
public class LocalUploadFile {
    private final File file;
    private final String originalFileName;
    private final String contentType;
    private final long fileSize;
    private final String purpose;

    public LocalUploadFile(
            File file,
            String originalFileName,
            String contentType,
            long fileSize,
            String purpose
    ) {
        this.file = file;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.purpose = purpose;
    }

    public File getFile() {
        return file;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getPurpose() {
        return purpose;
    }
}