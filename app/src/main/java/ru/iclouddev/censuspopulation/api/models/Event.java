package ru.iclouddev.censuspopulation.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Event {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("start_datetime")
    private Date startDateTime;

    @SerializedName("end_datetime")
    private Date endDateTime;

    @SerializedName("regions_count")
    private int regionsCount;

    @SerializedName("cities_count")
    private int citiesCount;

    @SerializedName("population_count")
    private int populationCount;

    @SerializedName("genders")
    private Gender[] genders;

    @SerializedName("regions")
    private List<Region> regions;

    @SerializedName("cities")
    private List<City> cities;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public int getRegionsCount() {
        return regionsCount;
    }

    public int getCitiesCount() {
        return citiesCount;
    }

    public int getPopulationCount() {
        return populationCount;
    }

    public Gender[] getGenders() {
        return genders;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public List<City> getCities() {
        return cities;
    }
} 