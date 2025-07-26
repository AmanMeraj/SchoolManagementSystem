package com.school.schoolmanagement.Model;

public class ResultPdfResponse {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPdfData() {
        return pdfData;
    }

    public void setPdfData(String pdfData) {
        this.pdfData = pdfData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int status;
    public String message;
    public String pdfData;
    public String fileName;
}
