package ru.iclouddev.censuspopulation.data.model;

import com.google.gson.annotations.SerializedName;

public class CensusEvents {
    @SerializedName("events")
    private CensusEvent[] events;
    @SerializedName("total")
    private int total;

    public CensusEvent[] getEvents() {
        return events;
    }

    public int getTotal() {
        return total;
    }
} 