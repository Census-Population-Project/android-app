package ru.iclouddev.censuspopulation.api.models;

import com.google.gson.annotations.SerializedName;

public class AllStatistics extends Statistics {
    @SerializedName("regions_statistics")
    RegionStatistics[] regions;

    @SerializedName("cities_statistics")
    CityStatistics[] cities;

    public RegionStatistics[] getRegions() {
        return regions;
    }

    public CityStatistics[] getCities() {
        return cities;
    }
}
