package ru.iclouddev.censuspopulation.data.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.iclouddev.censuspopulation.CensusApplication;
import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.data.model.ApiResponse;
import ru.iclouddev.censuspopulation.data.model.CensusEvent;
import ru.iclouddev.censuspopulation.data.model.CensusEvents;
import ru.iclouddev.censuspopulation.data.model.PopulationInfo;

public class ApiRepository {
    private static final String TAG = "ApiRepository";
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public interface ApiCallback<T> {
        void onSuccess(T result);

        void onError(int errorResourceId);
    }

    public ApiRepository() {
        executorService = Executors.newFixedThreadPool(4);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void getCensusEvents(int limit, int offset, ApiCallback<CensusEvents> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getCensusEvents(limit, offset)
                    .enqueue(new Callback<ApiResponse<CensusEvents>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<CensusEvents>> call,
                                               @NonNull Response<ApiResponse<CensusEvents>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<CensusEvents> apiResponse = response.body();
                                if (apiResponse.isSuccess() && apiResponse.getResult() != null) {
                                    mainHandler.post(() -> callback.onSuccess(apiResponse.getResult()));
                                } else {
                                    mainHandler.post(() -> callback.onError(R.string.error_no_data_for_view));
                                }
                            } else {
                                mainHandler.post(() -> callback.onError(R.string.error_loading_data));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<CensusEvents>> call, @NonNull Throwable t) {
                            Log.e(TAG, "Error loading census events", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }

    public void getCensusEventDetails(String eventId, ApiCallback<CensusEvent> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getCensusEventDetails(eventId)
                    .enqueue(new Callback<ApiResponse<CensusEvent>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<CensusEvent>> call,
                                               @NonNull Response<ApiResponse<CensusEvent>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<CensusEvent> apiResponse = response.body();
                                if (apiResponse.isSuccess() && apiResponse.getResult() != null) {
                                    mainHandler.post(() -> callback.onSuccess(apiResponse.getResult()));
                                } else {
                                    mainHandler.post(() -> callback.onError(R.string.error_no_data_for_view));
                                }
                            } else {
                                mainHandler.post(() -> callback.onError(R.string.error_loading_data));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<CensusEvent>> call, @NonNull Throwable t) {
                            Log.e(TAG, "Error loading census event details", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }

    public void getRegionPopulationInfo(String eventId, String regionId, ApiCallback<PopulationInfo> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getRegionPopulationInfo(eventId, regionId)
                    .enqueue(new Callback<ApiResponse<PopulationInfo>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<PopulationInfo>> call, @NonNull Response<ApiResponse<PopulationInfo>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<PopulationInfo> apiResponse = response.body();
                                if (apiResponse.isSuccess() && apiResponse.getResult() != null) {
                                    mainHandler.post(() -> callback.onSuccess(apiResponse.getResult()));
                                } else {
                                    mainHandler.post(() -> callback.onError(R.string.error_no_data_for_view));
                                }
                            } else {
                                mainHandler.post(() -> callback.onError(R.string.error_loading_data));
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<PopulationInfo>> call, Throwable t) {
                            Log.e(TAG, "Error loading region population info", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }


    public void getCityPopulationInfo(String eventId, String cityId, ApiCallback<PopulationInfo> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getCityPopulationInfo(eventId, cityId)
                    .enqueue(new Callback<ApiResponse<PopulationInfo>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<PopulationInfo>> call, @NonNull Response<ApiResponse<PopulationInfo>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<PopulationInfo> apiResponse = response.body();
                                if (apiResponse.isSuccess() && apiResponse.getResult() != null) {
                                    mainHandler.post(() -> callback.onSuccess(apiResponse.getResult()));
                                } else {
                                    mainHandler.post(() -> callback.onError(R.string.error_no_data_for_view));
                                }
                            } else {
                                mainHandler.post(() -> callback.onError(R.string.error_loading_data));
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<PopulationInfo>> call, Throwable t) {
                            Log.e(TAG, "Error loading city population info", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
} 