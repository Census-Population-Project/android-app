package ru.iclouddev.censuspopulation.api.models;

import com.google.gson.annotations.SerializedName;

public class Region {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}