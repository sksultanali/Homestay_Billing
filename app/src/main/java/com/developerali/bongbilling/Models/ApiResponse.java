package com.developerali.bongbilling.Models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("count")
    private int count;

    @SerializedName("data")
    private Object data;

    @SerializedName("nextPageToken")
    private Integer nextPageToken;

    // Constructor, Getters, and Setters

    public ApiResponse() {
    }

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(Integer nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}
