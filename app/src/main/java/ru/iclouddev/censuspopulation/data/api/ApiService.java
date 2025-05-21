package ru.iclouddev.censuspopulation.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.iclouddev.censuspopulation.data.model.ApiResponse;
import ru.iclouddev.censuspopulation.data.model.CensusEvent;
import ru.iclouddev.censuspopulation.data.model.CensusEvents;
import ru.iclouddev.censuspopulation.data.model.PopulationInfo;

public interface ApiService {
    @GET("api/v1/census/events")
    Call<ApiResponse<CensusEvents>> getCensusEvents(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("api/v1/census/events/{id}")
    Call<ApiResponse<CensusEvent>> getCensusEventDetails(
            @Path("id") String id
    );

    @GET("api/v1/census/events/{eventId}/region/{regionId}")
    Call<ApiResponse<PopulationInfo>> getRegionPopulationInfo(
            @Path("eventId") String eventId,
            @Path("regionId") String regionId
    );

    @GET("api/v1/census/events/{eventId}/city/{cityId}")
    Call<ApiResponse<PopulationInfo>> getCityPopulationInfo(
            @Path("eventId") String eventId,
            @Path("cityId") String cityId
    );
} 