package ru.iclouddev.censuspopulation;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.iclouddev.censuspopulation.data.api.ApiService;

public class CensusApplication extends Application {
    private static CensusApplication instance;
    private ApiService apiService;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize Yandex MapKit
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
        MapKitFactory.initialize(this);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static CensusApplication getInstance() {
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
} 