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
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.TextStyle;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.runtime.image.ImageProvider;

import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.api.models.City;
import ru.iclouddev.censuspopulation.api.models.Event;
import ru.iclouddev.censuspopulation.api.models.Events;
import ru.iclouddev.censuspopulation.api.models.Region;
import ru.iclouddev.censuspopulation.databinding.FragmentMapBinding;
import ru.iclouddev.censuspopulation.dialogs.EventsDialog;
import ru.iclouddev.censuspopulation.utils.Utils;
import ru.iclouddev.censuspopulation.api.models.EventInfo;
import ru.iclouddev.censuspopulation.dialogs.EventInfoDialog;
import ru.iclouddev.censuspopulation.api.APIRepository;
import ru.iclouddev.censuspopulation.ContainerIRSActivity;
import ru.iclouddev.censuspopulation.viewmodels.CensusViewModel;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";
    private FragmentMapBinding binding;
    private MapView mapView;
    private EventsDialog eventsDialog;
    private List<Event> events;
    private Event selectedEvent;
    private Utils utils;
    private MapObjectCollection mapObjects;
    private List<PlacemarkMapObject> regionPlacemarks;
    private List<PlacemarkMapObject> cityPlacemarks;
    private List<MapObjectTapListener> mapObjectTapListeners;
    private static final float REGIONS_ZOOM_LEVEL = 8.5f;
    private static final float CITIES_ZOOM_LEVEL = 8.5f;
    private static final float REGION_TEXT_VISIBILITY_ZOOM = 7.5f; // Уровень зума, при котором становятся видны названия регионов
    private CameraListener cameraListener;
    private APIRepository apiRepository;
    private ProgressBar progressBar;
    private CensusViewModel censusViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        events = new ArrayList<>();
        regionPlacemarks = new ArrayList<>();
        cityPlacemarks = new ArrayList<>();
        mapObjectTapListeners = new ArrayList<>();
        apiRepository = new APIRepository();
        utils = new Utils();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = binding.mapView;
        FloatingActionButton selectCensusButton = binding.selectCensusButton;
        progressBar = requireView().findViewById(R.id.progressBar);

        // Подписываемся на изменения выбранной переписи
        if (getActivity() instanceof ContainerIRSActivity) {
            censusViewModel = ((ContainerIRSActivity) getActivity()).getCensusViewModel();
            censusViewModel.getSelectedCensusEvent().observe(getViewLifecycleOwner(), event -> {
                if (event != null) {
                    selectedEvent = event;
                    loadCensusEventDetails(event.getId());
                }
            });
        }

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

        // Обновляем видимость регионов и их названий
        for (PlacemarkMapObject placemark : regionPlacemarks) {
            boolean markerVisible = zoom <= CITIES_ZOOM_LEVEL;
            boolean textVisible = zoom <= REGION_TEXT_VISIBILITY_ZOOM;
            placemark.setVisible(markerVisible);
            if (textVisible) {
                placemark.setText("");
            } else {
                String regionName = placemark.getUserData() instanceof Region
                        ? ((Region) placemark.getUserData()).getName()
                        : "";
                placemark.setText(regionName);
            }
        }

        // Обновляем видимость городов
        for (PlacemarkMapObject placemark : cityPlacemarks) {
            boolean visible = zoom > REGIONS_ZOOM_LEVEL;
            placemark.setVisible(visible);
        }
    }

    private void displayCensusEventData(Event event) {
        // Очищаем предыдущие маркеры
        mapObjects.clear();
        regionPlacemarks.clear();
        cityPlacemarks.clear();
        mapObjectTapListeners.clear();

        TextStyle textStyle = new TextStyle();
        textStyle.setColor(R.color.white);
        textStyle.setSize(10);
        textStyle.setOffsetFromIcon(true);
        textStyle.setPlacement(TextStyle.Placement.TOP);

        IconStyle iconStyle = new IconStyle();
        iconStyle.setAnchor(new PointF(0.5f, 0.5f));
        iconStyle.setScale(0.3f);

        float currentZoom = mapView.getMapWindow().getMap().getCameraPosition().getZoom();

        // Добавляем маркеры регионов
        if (event.getRegions() != null) {
            for (Region region : event.getRegions()) {
                PlacemarkMapObject placemark = mapObjects.addPlacemark();
                placemark.setGeometry(new Point(region.getLatitude(), region.getLongitude()));
                placemark.setIcon(ImageProvider.fromResource(getContext(), R.drawable.ic_region), iconStyle);
                placemark.setText(region.getName(), textStyle);
                placemark.setVisible(currentZoom <= CITIES_ZOOM_LEVEL);
                placemark.setUserData(region); // Сохраняем данные региона
                MapObjectTapListener tapListener = (mapObject, point) -> {
                    loadRegionPopulationInfo(event.getId(), region.getId(), region.getName());
                    return true;
                };
                mapObjectTapListeners.add(tapListener);
                placemark.addTapListener(tapListener);
                regionPlacemarks.add(placemark);
            }
        }

        // Добавляем маркеры городов
        if (event.getCities() != null) {
            for (City city : event.getCities()) {
                PlacemarkMapObject placemark = mapObjects.addPlacemark();
                placemark.setGeometry(new Point(city.getLatitude(), city.getLongitude()));
                placemark.setIcon(ImageProvider.fromResource(getContext(), R.drawable.ic_city), iconStyle);
                placemark.setText(city.getName(), textStyle);
                placemark.setVisible(currentZoom > REGIONS_ZOOM_LEVEL);
                placemark.setUserData(city); // Сохраняем данные города
                MapObjectTapListener tapListener = (mapObject, point) -> {
                    loadCityPopulationInfo(event.getId(), city.getId(), city.getName());
                    return true;
                };
                mapObjectTapListeners.add(tapListener);
                placemark.addTapListener(tapListener);
                cityPlacemarks.add(placemark);
            }
        }
    }

    private void showCensusEventDialog() {
        eventsDialog = new EventsDialog(requireContext(), this::onCensusEventSelected, selectedEvent);
        eventsDialog.show();
    }

    private void onCensusEventSelected(Event event) {
        selectedEvent = event;
        if (getActivity() instanceof ContainerIRSActivity) {
            ((ContainerIRSActivity) getActivity()).getCensusViewModel().setSelectedCensusEvent(event);
        }
        loadCensusEventDetails(event.getId());
    }

    private void loadCensusEventDetails(String eventId) {
        apiRepository.getCensusEventDetails(eventId, new APIRepository.ApiCallback<Event>() {
            @Override
            public void onSuccess(Event result) {
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
        apiRepository.getCensusEvents(10, 0, new APIRepository.ApiCallback<Events>() {
            @Override
            public void onSuccess(Events result) {
                events.clear();
                events.addAll(List.of(result.getEvents()));
                Log.d(TAG, "Loaded " + events.size() + " census events");
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
        apiRepository.getRegionPopulationInfo(eventId, regionId, new APIRepository.ApiCallback<EventInfo>() {
            @Override
            public void onSuccess(EventInfo result) {
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
        apiRepository.getCityPopulationInfo(eventId, cityId, new APIRepository.ApiCallback<EventInfo>() {
            @Override
            public void onSuccess(EventInfo result) {
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

    private void showPopulationInfoDialog(String title, EventInfo info) {
        EventInfoDialog dialog = new EventInfoDialog(getContext(), title, info);
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