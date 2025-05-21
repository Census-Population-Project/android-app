package ru.iclouddev.censuspopulation.fragments;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.TextStyle;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.runtime.image.ImageProvider;

import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.data.model.CensusEvent;
import ru.iclouddev.censuspopulation.data.model.CensusEvents;
import ru.iclouddev.censuspopulation.databinding.FragmentMapBinding;
import ru.iclouddev.censuspopulation.dialogs.CensusEventsDialog;
import ru.iclouddev.censuspopulation.utils.Utils;
import ru.iclouddev.censuspopulation.data.model.PopulationInfo;
import ru.iclouddev.censuspopulation.dialogs.PopulationInfoDialog;
import ru.iclouddev.censuspopulation.data.api.ApiRepository;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";
    private FragmentMapBinding binding;
    private MapView mapView;
    private CensusEventsDialog censusEventsDialog;
    private List<CensusEvent> censusEvents;
    private CensusEvent selectedCensusEvent;
    private Utils utils;
    private MapObjectCollection mapObjects;
    private List<PlacemarkMapObject> regionPlacemarks;
    private List<PlacemarkMapObject> cityPlacemarks;
    private static final float REGIONS_ZOOM_LEVEL = 8.0f;
    private static final float CITIES_ZOOM_LEVEL = 8.0f;
    private CameraListener cameraListener;
    private ApiRepository apiRepository;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        censusEvents = new ArrayList<>();
        regionPlacemarks = new ArrayList<>();
        cityPlacemarks = new ArrayList<>();
        apiRepository = new ApiRepository();
        utils = new Utils();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = binding.mapView;
        FloatingActionButton selectCensusButton = binding.selectCensusButton;
        progressBar = requireView().findViewById(R.id.progressBar);

        String mapStyle = utils.readRawResource(getContext(), R.raw.map_style);
        mapView.getMapWindow().getMap().setMapStyle(mapStyle);

        // Устанавливаем начальный зум для отображения регионов
        mapView.getMapWindow().getMap().move(
                new CameraPosition(new Point(55.751244, 37.618423), REGIONS_ZOOM_LEVEL, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null
        );

        // Добавляем слушатель изменения зума
        cameraListener = new CameraListener() {
            @Override
            public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean finished) {
                updateMarkersVisibility(cameraPosition.getZoom());
            }
        };
        mapView.getMapWindow().getMap().addCameraListener(cameraListener);

        // Инициализируем коллекцию объектов карты
        mapObjects = mapView.getMapWindow().getMap().getMapObjects().addCollection();

        selectCensusButton.setOnClickListener(v -> showCensusEventDialog());

        loadCensusEvents();
    }

    private void updateMarkersVisibility(float zoom) {
        Log.d(TAG, "Updating markers visibility for zoom: " + zoom);

        // Обновляем видимость регионов
        for (PlacemarkMapObject placemark : regionPlacemarks) {
            boolean visible = zoom <= CITIES_ZOOM_LEVEL;
            placemark.setVisible(visible);
        }

        // Обновляем видимость городов
        for (PlacemarkMapObject placemark : cityPlacemarks) {
            boolean visible = zoom > REGIONS_ZOOM_LEVEL;
            placemark.setVisible(visible);
        }
    }

    private void displayCensusEventData(CensusEvent event) {
        // Очищаем предыдущие маркеры
        mapObjects.clear();
        regionPlacemarks.clear();
        cityPlacemarks.clear();

        TextStyle textStyle = new TextStyle();
        textStyle.setColor(R.color.white);
        textStyle.setSize(15);
        textStyle.setOffsetFromIcon(true);
        textStyle.setPlacement(TextStyle.Placement.TOP);

        IconStyle iconStyle = new IconStyle();
        iconStyle.setAnchor(new PointF(0.5f, 0.5f));
        iconStyle.setScale(0.3f);

        float currentZoom = mapView.getMapWindow().getMap().getCameraPosition().getZoom();

        // Добавляем маркеры регионов
        if (event.getRegions() != null) {
            for (CensusEvent.Region region : event.getRegions()) {
                PlacemarkMapObject placemark = mapObjects.addPlacemark();
                placemark.setGeometry(new Point(region.getLatitude(), region.getLongitude()));
                placemark.setIcon(ImageProvider.fromResource(getContext(), R.drawable.ic_region), iconStyle);
                placemark.setText(region.getName(), textStyle);
                placemark.setVisible(currentZoom <= CITIES_ZOOM_LEVEL);
                placemark.setUserData(region); // Сохраняем данные региона
                placemark.addTapListener((mapObject, point) -> {
                    loadRegionPopulationInfo(event.getId(), region.getId(), region.getName());
                    return true;
                });
                regionPlacemarks.add(placemark);
            }
        }

        // Добавляем маркеры городов
        if (event.getCities() != null) {
            for (CensusEvent.City city : event.getCities()) {
                PlacemarkMapObject placemark = mapObjects.addPlacemark();
                placemark.setGeometry(new Point(city.getLatitude(), city.getLongitude()));
                placemark.setIcon(ImageProvider.fromResource(getContext(), R.drawable.ic_city), iconStyle);
                placemark.setText(city.getName(), textStyle);
                placemark.setVisible(currentZoom > REGIONS_ZOOM_LEVEL);
                placemark.setUserData(city); // Сохраняем данные города
                placemark.addTapListener((mapObject, point) -> {
                    loadCityPopulationInfo(event.getId(), city.getId(), city.getName());
                    return true;
                });
                cityPlacemarks.add(placemark);
            }
        }
    }

    private void showCensusEventDialog() {
        censusEventsDialog = new CensusEventsDialog(requireContext(), this::onCensusEventSelected, selectedCensusEvent);
        censusEventsDialog.show();
    }

    private void onCensusEventSelected(CensusEvent event) {
        selectedCensusEvent = event;
        loadCensusEventDetails(event.getId());
    }

    private void loadCensusEventDetails(String eventId) {
        apiRepository.getCensusEventDetails(eventId, new ApiRepository.ApiCallback<CensusEvent>() {
            @Override
            public void onSuccess(CensusEvent result) {
                displayCensusEventData(result);
            }

            @Override
            public void onError(int errorResourceId) {
                String errorMessage = getContext().getResources().getString(errorResourceId);
                utils.showError(getContext(), errorMessage);
            }
        });
    }

    private void loadCensusEvents() {
        apiRepository.getCensusEvents(10, 0, new ApiRepository.ApiCallback<CensusEvents>() {
            @Override
            public void onSuccess(CensusEvents result) {
                censusEvents.clear();
                censusEvents.addAll(List.of(result.getEvents()));
                Log.d(TAG, "Loaded " + censusEvents.size() + " census events");
            }

            @Override
            public void onError(int errorResourceId) {
                String errorMessage = getContext().getResources().getString(errorResourceId);
                utils.showError(getContext(), errorMessage);
            }
        });
    }

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void loadRegionPopulationInfo(String eventId, String regionId, String regionName) {
        showLoading();
        apiRepository.getRegionPopulationInfo(eventId, regionId, new ApiRepository.ApiCallback<PopulationInfo>() {
            @Override
            public void onSuccess(PopulationInfo result) {
                hideLoading();
                if (!isAdded() || getActivity() == null) return;
                showPopulationInfoDialog(regionName, result);
            }

            @Override
            public void onError(int errorResourceId) {
                hideLoading();
                if (!isAdded() || getActivity() == null) return;
                String errorMessage = getContext().getResources().getString(errorResourceId);
                utils.showError(getContext(), errorMessage);
            }
        });
    }

    private void loadCityPopulationInfo(String eventId, String cityId, String cityName) {
        showLoading();
        apiRepository.getCityPopulationInfo(eventId, cityId, new ApiRepository.ApiCallback<PopulationInfo>() {
            @Override
            public void onSuccess(PopulationInfo result) {
                hideLoading();
                if (!isAdded() || getActivity() == null) return;
                showPopulationInfoDialog(cityName, result);
            }

            @Override
            public void onError(int errorResourceId) {
                hideLoading();
                if (!isAdded() || getActivity() == null) return;
                String errorMessage = getContext().getResources().getString(errorResourceId);
                utils.showError(getContext(), errorMessage);
            }
        });
    }

    private void showPopulationInfoDialog(String title, PopulationInfo info) {
        PopulationInfoDialog dialog = new PopulationInfoDialog(getContext(), title, info);
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null && cameraListener != null) {
            mapView.getMapWindow().getMap().removeCameraListener(cameraListener);
        }
        if (apiRepository != null) {
            apiRepository.shutdown();
        }
    }
} 