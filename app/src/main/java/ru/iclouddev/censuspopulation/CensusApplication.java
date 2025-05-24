package ru.iclouddev.censuspopulation;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.iclouddev.censuspopulation.api.APIService;

public class CensusApplication extends Application {
    private static CensusApplication instance;
    private APIService apiService;

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

        apiService = retrofit.create(APIService.class);
    }

    public static CensusApplication getInstance() {
        return instance;
    }

    public APIService getApiService() {
        return apiService;
    }
} 