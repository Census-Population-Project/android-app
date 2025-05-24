package ru.iclouddev.censuspopulation.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import ru.iclouddev.censuspopulation.CensusApplication;
import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.api.models.Response;
import ru.iclouddev.censuspopulation.api.models.Event;
import ru.iclouddev.censuspopulation.api.models.Events;
import ru.iclouddev.censuspopulation.api.models.EventInfo;
import ru.iclouddev.censuspopulation.api.models.Statistics;

public class APIRepository {
    private static final String TAG = "ApiRepository";
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public interface ApiCallback<T> {
        void onSuccess(T result);

        void onError(int errorResourceId);
    }

    public APIRepository() {
        executorService = Executors.newFixedThreadPool(4);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void getCensusEvents(int limit, int offset, ApiCallback<Events> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getCensusEvents(limit, offset)
                    .enqueue(new Callback<Response<Events>>() {
                        @Override
                        public void onResponse(@NonNull Call<Response<Events>> call,
                                               @NonNull retrofit2.Response<Response<Events>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Response<Events> apiResponse = response.body();
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
                        public void onFailure(@NonNull Call<Response<Events>> call, @NonNull Throwable t) {
                            Log.e(TAG, "Error loading census events", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }

    public void getCensusEventDetails(String eventId, ApiCallback<Event> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getCensusEventDetails(eventId)
                    .enqueue(new Callback<Response<Event>>() {
                        @Override
                        public void onResponse(@NonNull Call<Response<Event>> call,
                                               @NonNull retrofit2.Response<Response<Event>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Response<Event> apiResponse = response.body();
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
                        public void onFailure(@NonNull Call<Response<Event>> call, @NonNull Throwable t) {
                            Log.e(TAG, "Error loading census event details", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }

    public void getRegionPopulationInfo(String eventId, String regionId, ApiCallback<EventInfo> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getRegionPopulationInfo(eventId, regionId)
                    .enqueue(new Callback<Response<EventInfo>>() {
                        @Override
                        public void onResponse(@NonNull Call<Response<EventInfo>> call, @NonNull retrofit2.Response<Response<EventInfo>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Response<EventInfo> apiResponse = response.body();
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
                        public void onFailure(@NonNull Call<Response<EventInfo>> call, @NonNull Throwable t) {
                            Log.e(TAG, "Error loading region population info", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }

    public void getRegionStatistics(String eventId, String regionId, ApiCallback<Statistics> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getRegionStatistics(eventId, regionId)
                    .enqueue(new Callback<Response<Statistics>>() {
                        @Override
                        public void onResponse(@NonNull Call<Response<Statistics>> call,
                                               @NonNull retrofit2.Response<Response<Statistics>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Response<Statistics> apiResponse = response.body();
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
                        public void onFailure(@NonNull Call<Response<Statistics>> call, @NonNull Throwable t) {
                            Log.e(TAG, "Error loading region statistics", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }

    public void getCityPopulationInfo(String eventId, String cityId, ApiCallback<EventInfo> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getCityPopulationInfo(eventId, cityId)
                    .enqueue(new Callback<Response<EventInfo>>() {
                        @Override
                        public void onResponse(@NonNull Call<Response<EventInfo>> call, @NonNull retrofit2.Response<Response<EventInfo>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Response<EventInfo> apiResponse = response.body();
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
                        public void onFailure(@NonNull Call<Response<EventInfo>> call, @NonNull Throwable t) {
                            Log.e(TAG, "Error loading city population info", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }

    public void getCityStatistics(String eventId, String cityId, ApiCallback<Statistics> callback) {
        executorService.execute(() -> {
            CensusApplication.getInstance().getApiService()
                    .getCityStatistics(eventId, cityId)
                    .enqueue(new Callback<Response<Statistics>>() {
                        @Override
                        public void onResponse(@NonNull Call<Response<Statistics>> call,
                                               @NonNull retrofit2.Response<Response<Statistics>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Response<Statistics> apiResponse = response.body();
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
                        public void onFailure(@NonNull Call<Response<Statistics>> call, @NonNull Throwable t) {
                            Log.e(TAG, "Error loading city statistics", t);
                            mainHandler.post(() -> callback.onError(R.string.error_network));
                        }
                    });
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
} 