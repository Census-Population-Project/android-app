package ru.iclouddev.censuspopulation.api.models;

import com.google.gson.annotations.SerializedName;

public class Events {
    @SerializedName("events")
    private Event[] events;
    @SerializedName("total")
    private int total;

    public Event[] getEvents() {
        return events;
    }

    public int getTotal() {
        return total;
    }
} 