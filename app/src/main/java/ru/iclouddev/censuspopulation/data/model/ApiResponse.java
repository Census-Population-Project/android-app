package ru.iclouddev.censuspopulation.data.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("status")
    private String status;

    @SerializedName("result")
    private T result;

    public String getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }

    public boolean isSuccess() {
        return "success".equals(status);
    }
} 