package ru.iclouddev.censuspopulation.api.models;

import com.google.gson.annotations.SerializedName;

public class Language {
    @SerializedName("type")
    private String type;

    @SerializedName("count")
    private int count;

    public String getType() {
        return type;
    }

    public int getCount() {
        return count;
    }
}