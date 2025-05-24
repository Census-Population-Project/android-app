package ru.iclouddev.censuspopulation.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Statistics {
    @SerializedName("total_population")
    private int totalPopulation;

    @SerializedName("total_households")
    private int totalHouseholds;

    @SerializedName("avg_persons_per_household")
    private double avgPersonsPerHousehold;

    @SerializedName("gender_distribution")
    private List<Gender> genderDistribution;

    @SerializedName("average_age")
    private double averageAge;

    @SerializedName("children_count")
    private int childrenCount;

    @SerializedName("elderly_count")
    private int elderlyCount;

    @SerializedName("education_distribution")
    private Map<String, Integer> educationDistribution;

    @SerializedName("percent_speaks_russian")
    private int percentSpeaksRussian;

    @SerializedName("dual_citizenship_count")
    private int dualCitizenshipCount;

    @SerializedName("top_other_languages")
    private List<Language> topOtherLanguages;

    public int getTotalPopulation() {
        return totalPopulation;
    }

    public int getTotalHouseholds() {
        return totalHouseholds;
    }

    public double getAvgPersonsPerHousehold() {
        return avgPersonsPerHousehold;
    }

    public List<Gender> getGenderDistribution() {
        return genderDistribution;
    }

    public double getAverageAge() {
        return averageAge;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public int getElderlyCount() {
        return elderlyCount;
    }

    public Map<String, Integer> getEducationDistribution() {
        return educationDistribution;
    }

    public int getPercentSpeaksRussian() {
        return percentSpeaksRussian;
    }

    public int getDualCitizenshipCount() {
        return dualCitizenshipCount;
    }

    public List<Language> getTopOtherLanguages() {
        return topOtherLanguages;
    }
} 