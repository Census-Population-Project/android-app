package ru.iclouddev.censuspopulation.api.models;

import com.google.gson.annotations.SerializedName;

public class RegionStatistics {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("population_count")
    private int populationCount;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPopulationCount() {
        return populationCount;
    }
}
