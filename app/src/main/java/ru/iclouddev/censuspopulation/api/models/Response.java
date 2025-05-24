package ru.iclouddev.censuspopulation.api.models;

import com.google.gson.annotations.SerializedName;

public class Response<T> {
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