package ru.iclouddev.censuspopulation.api.models;

import com.google.gson.annotations.SerializedName;

public class CityStatistics {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("population_count")
    private double populationCount;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPopulationCount() {
        return populationCount;
    }
}
