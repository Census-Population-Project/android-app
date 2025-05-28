package ru.iclouddev.censuspopulation.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.iclouddev.censuspopulation.ContainerIRSActivity;
import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.api.APIRepository;
import ru.iclouddev.censuspopulation.api.models.AllStatistics;
import ru.iclouddev.censuspopulation.api.models.CityStatistics;
import ru.iclouddev.censuspopulation.api.models.Event;
import ru.iclouddev.censuspopulation.api.models.Events;
import ru.iclouddev.censuspopulation.api.models.Gender;
import ru.iclouddev.censuspopulation.api.models.RegionStatistics;
import ru.iclouddev.censuspopulation.api.models.Statistics;
import ru.iclouddev.censuspopulation.databinding.FragmentStatisticsBinding;
import ru.iclouddev.censuspopulation.dialogs.EventsDialog;
import ru.iclouddev.censuspopulation.utils.Utils;
import ru.iclouddev.censuspopulation.viewmodels.CensusViewModel;

public class StatisticsFragment extends Fragment {
    private FragmentStatisticsBinding binding;
    private APIRepository apiRepository;
    private Utils utils;
    private List<Event> events;
    private Event selectedEvent;
    private ProgressBar progressBar;
    private CensusViewModel censusViewModel;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        events = new ArrayList<>();
        apiRepository = new APIRepository();
        utils = new Utils();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton selectCensusButton = binding.selectCensusButton;
        progressBar = requireView().findViewById(R.id.progressBar);

        if (getActivity() instanceof ContainerIRSActivity) {
            censusViewModel = ((ContainerIRSActivity) getActivity()).getCensusViewModel();
            censusViewModel.getSelectedCensusEvent().observe(getViewLifecycleOwner(), this::updateSelectedCensusInfo);
        }

        selectCensusButton.setOnClickListener(v -> showCensusEventDialog());
        loadCensusEvents();
    }

    private void showCensusEventDialog() {
        EventsDialog dialog = new EventsDialog(requireContext(), this::onCensusEventSelected, selectedEvent);
        dialog.show();
    }

    private void onCensusEventSelected(Event event) {
        selectedEvent = event;
        if (censusViewModel != null) {
            censusViewModel.setSelectedCensusEvent(event);
        }
    }

    private void updateSelectedCensusInfo(Event event) {
        if (event != null) {
            binding.selectedCensusCard.setVisibility(View.VISIBLE);
            binding.selectedCensusName.setText(event.getName());
            String dateRange = String.format("Период проведения: %s — %s",
                    dateFormat.format(event.getStartDateTime()),
                    dateFormat.format(event.getEndDateTime()));
            binding.selectedCensusYear.setText(dateRange);
            this.selectedEvent = event;
            loadStatistics(event.getId());
        } else {
            binding.selectedCensusCard.setVisibility(View.GONE);
            binding.selectedCensusName.setText("");
            binding.selectedCensusYear.setText("");
        }
    }

    private void loadStatistics(String eventId) {
        showLoading();
        apiRepository.getCensusEventAllStatistics(eventId, new APIRepository.ApiCallback<AllStatistics>() {
            @Override
            public void onSuccess(AllStatistics result) {
                hideLoading();
                updateCharts(result);
            }

            @Override
            public void onError(int errorResourceId) {
                hideLoading();
                String errorMessage = getContext().getResources().getString(errorResourceId);
                utils.showError(getContext(), errorMessage);
            }
        });
    }

    private void updateCharts(AllStatistics statistics) {
        updateGenderChart(statistics.getGenderDistribution());
        updateRegionsChart(statistics.getRegions());
        updateCitiesChart(statistics.getCities());
    }

    private void updateGenderChart(List<Gender> genderDistribution) {
        if (genderDistribution == null || genderDistribution.isEmpty()) return;

        List<PieEntry> entries = new ArrayList<>();
        for (Gender gender : genderDistribution) {
            entries.add(new PieEntry(gender.getCount(), gender.getType()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.genderChart));

        binding.genderChart.setData(data);
        binding.genderChart.setUsePercentValues(true);
        binding.genderChart.getDescription().setEnabled(false);
        binding.genderChart.setDrawEntryLabels(false);
        binding.genderChart.getLegend().setEnabled(true);
        binding.genderChart.animateY(1000);
        binding.genderChart.invalidate();
    }

    private void updateRegionsChart(RegionStatistics[] regions) {
        if (regions == null || regions.length == 0) return;

        List<PieEntry> entries = new ArrayList<>();
        for (RegionStatistics region : regions) {
            entries.add(new PieEntry(region.getPopulationCount(), region.getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.regionsChart));

        binding.regionsChart.setData(data);
        binding.regionsChart.setUsePercentValues(true);
        binding.regionsChart.getDescription().setEnabled(false);
        binding.regionsChart.setDrawEntryLabels(false);
        binding.regionsChart.getLegend().setEnabled(true);
        binding.regionsChart.animateY(1000);
        binding.regionsChart.invalidate();
    }

    private void updateCitiesChart(CityStatistics[] cities) {
        if (cities == null || cities.length == 0) return;

        List<PieEntry> entries = new ArrayList<>();
        for (CityStatistics city : cities) {
            entries.add(new PieEntry((int)city.getPopulationCount(), city.getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.citiesChart));

        binding.citiesChart.setData(data);
        binding.citiesChart.setUsePercentValues(true);
        binding.citiesChart.getDescription().setEnabled(false);
        binding.citiesChart.setDrawEntryLabels(false);
        binding.citiesChart.getLegend().setEnabled(true);
        binding.citiesChart.animateY(1000);
        binding.citiesChart.invalidate();
    }

    private void loadCensusEvents() {
        apiRepository.getCensusEvents(10, 0, new APIRepository.ApiCallback<Events>() {
            @Override
            public void onSuccess(Events result) {
                events.clear();
                events.addAll(List.of(result.getEvents()));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (apiRepository != null) {
            apiRepository.shutdown();
        }
    }
} 