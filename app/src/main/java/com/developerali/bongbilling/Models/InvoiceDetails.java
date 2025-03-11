package com.developerali.bongbilling.Models;

import java.util.List;

public class InvoiceDetails {
    private String status;
    private String message;
    private List<InvoiceData> data;
    private String nextPageToken;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<InvoiceData> getData() {
        return data;
    }

    public void setData(List<InvoiceData> data) {
        this.data = data;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}

