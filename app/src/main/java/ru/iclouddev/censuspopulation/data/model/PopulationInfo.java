package ru.iclouddev.censuspopulation.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PopulationInfo {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("start_datetime")
    private String startDateTime;

    @SerializedName("end_datetime")
    private String endDateTime;

    @SerializedName("population")
    private int population;

    @SerializedName("genders")
    private List<GenderInfo> genders;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public int getPopulation() {
        return population;
    }

    public int getMaleCount() {
        for (GenderInfo gender : genders) {
            if ("male".equals(gender.getType())) {
                return gender.getCount();
            }
        }
        return 0;
    }

    public int getFemaleCount() {
        for (GenderInfo gender : genders) {
            if ("female".equals(gender.getType())) {
                return gender.getCount();
            }
        }
        return 0;
    }

    public static class GenderInfo {
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
} 