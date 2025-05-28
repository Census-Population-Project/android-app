package ru.iclouddev.censuspopulation.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.iclouddev.censuspopulation.api.models.AllStatistics;
import ru.iclouddev.censuspopulation.api.models.Response;
import ru.iclouddev.censuspopulation.api.models.Event;
import ru.iclouddev.censuspopulation.api.models.Events;
import ru.iclouddev.censuspopulation.api.models.EventInfo;
import ru.iclouddev.censuspopulation.api.models.Statistics;

public interface APIService {
    @GET("api/v1/census/events")
    Call<Response<Events>> getCensusEvents(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("api/v1/census/events/{eventId}")
    Call<Response<Event>> getCensusEventDetails(
            @Path("eventId") String eventId
    );

    @GET("api/v1/census/events/{eventId}/statistics")
    Call<Response<AllStatistics>> getCensusEventAllStatistics(
            @Path("eventId") String eventId
    );

    @GET("api/v1/census/events/{eventId}/region/{regionId}/statistics")
    Call<Response<Statistics>> getRegionStatistics(
            @Path("eventId") String eventId,
            @Path("regionId") String regionId
    );

    @GET("api/v1/census/events/{eventId}/city/{cityId}/statistics")
    Call<Response<Statistics>> getCityStatistics(
            @Path("eventId") String eventId,
            @Path("cityId") String cityId
    );

    @GET("api/v1/census/events/{eventId}/region/{regionId}")
    Call<Response<EventInfo>> getRegionPopulationInfo(
            @Path("eventId") String eventId,
            @Path("regionId") String regionId
    );

    @GET("api/v1/census/events/{eventId}/city/{cityId}")
    Call<Response<EventInfo>> getCityPopulationInfo(
            @Path("eventId") String eventId,
            @Path("cityId") String cityId
    );
} 