package ru.iclouddev.censuspopulation.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CensusEvent {
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

    @SerializedName("population")
    private int population;

    @SerializedName("genders")
    private Gender[] genders;

    @SerializedName("regions")
    private Region[] regions;

    @SerializedName("cities")
    private City[] cities;

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

    public int getPopulation() {
        return population;
    }

    public Gender[] getGenders() {
        return genders;
    }

    public Region[] getRegions() {
        return regions;
    }

    public City[] getCities() {
        return cities;
    }

    public static class Gender {
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

    public static class Region {
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
    }

    public static class City {
        @SerializedName("id")
        private String id;

        @SerializedName("region_id")
        private String regionId;

        @SerializedName("name")
        private String name;

        @SerializedName("lat")
        private double latitude;

        @SerializedName("lon")
        private double longitude;

        public String getId() {
            return id;
        }

        public String getRegionId() {
            return regionId;
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
    }
} 