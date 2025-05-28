package ru.iclouddev.censuspopulation.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.iclouddev.censuspopulation.ContainerIRSActivity;
import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.api.models.Gender;
import ru.iclouddev.censuspopulation.api.models.Language;
import ru.iclouddev.censuspopulation.api.APIRepository;
import ru.iclouddev.censuspopulation.api.models.Event;
import ru.iclouddev.censuspopulation.api.models.Statistics;
import ru.iclouddev.censuspopulation.api.models.City;
import ru.iclouddev.censuspopulation.api.models.Region;
import ru.iclouddev.censuspopulation.databinding.FragmentInfoBinding;
import ru.iclouddev.censuspopulation.dialogs.EventsDialog;
import ru.iclouddev.censuspopulation.utils.Utils;
import ru.iclouddev.censuspopulation.viewmodels.CensusViewModel;

public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;
    private APIRepository apiRepository;
    private Utils utils;
    private Event selectedEvent;
    private ProgressBar progressBar;
    private CensusViewModel censusViewModel;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
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

        // Подписываемся на изменения выбранной переписи
        if (getActivity() instanceof ContainerIRSActivity) {
            censusViewModel = ((ContainerIRSActivity) getActivity()).getCensusViewModel();
            censusViewModel.getSelectedCensusEvent().observe(getViewLifecycleOwner(), this::updateSelectedCensusInfo);
        }

        selectCensusButton.setOnClickListener(v -> showCensusEventDialog());

        // Настройка слушателей для спиннеров
        binding.regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Пропускаем первый элемент (подсказку)
                    binding.citySpinner.setSelection(0);

                    Region selectedRegion = (Region) parent.getItemAtPosition(position);
                    loadRegionStatistics(selectedEvent.getId(), selectedRegion.getId());

                    if (binding.generalInfoContent.getVisibility() != View.VISIBLE) {
                        binding.generalInfoContent.setVisibility(View.VISIBLE);
                        binding.generalInfoExpandIcon.setRotation(180);
                    }
                } else {
                    showNoDataMessage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showNoDataMessage();
            }
        });

        binding.citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    binding.regionSpinner.setSelection(0);

                    City selectedCity = (City) parent.getItemAtPosition(position);
                    loadCityStatistics(selectedEvent.getId(), selectedCity.getId());

                    if (binding.generalInfoContent.getVisibility() != View.VISIBLE) {
                        binding.generalInfoContent.setVisibility(View.VISIBLE);
                        binding.generalInfoExpandIcon.setRotation(180);
                    }
                } else {
                    showNoDataMessage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showNoDataMessage();
            }
        });

        // Настройка раскрывающихся секций
        setupExpandableSection(binding.generalInfoHeader, binding.generalInfoContent, binding.generalInfoExpandIcon);
        setupExpandableSection(binding.demographicsHeader, binding.demographicsContent, binding.demographicsExpandIcon);
        setupExpandableSection(binding.educationHeader, binding.educationContent, binding.educationExpandIcon);

        binding.regionSpinner.setEnabled(false);
        binding.citySpinner.setEnabled(false);

        // Показываем начальное сообщение "Нет данных"
        showNoDataMessage();
        updateRegionsList(new ArrayList<Region>());
        updateCitiesList(new ArrayList<City>());
    }

    private void setupExpandableSection(View header, View content, View expandIcon) {
        header.setOnClickListener(v -> {
            boolean isExpanded = content.getVisibility() == View.VISIBLE;
            content.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandIcon.setRotation(isExpanded ? 0 : 180);
        });
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
            loadCensusEventDetails(event.getId());
            binding.regionSpinner.setEnabled(true);
            binding.citySpinner.setEnabled(true);
        } else {
            binding.selectedCensusCard.setVisibility(View.GONE);
            binding.selectedCensusName.setText("");
            binding.selectedCensusYear.setText("");
        }
    }

    private void loadCensusEventDetails(String eventId) {
        showLoading();
        apiRepository.getCensusEventDetails(eventId, new APIRepository.ApiCallback<Event>() {
            @Override
            public void onSuccess(Event result) {
                hideLoading();
                updateRegionsList(result.getRegions());
                updateCitiesList(result.getCities());
            }

            @Override
            public void onError(int errorResourceId) {
                hideLoading();
                if (getContext() == null) return;
                String errorMessage = getContext().getResources().getString(errorResourceId);
                utils.showError(getContext(), errorMessage);
            }
        });
    }

    private void loadRegionStatistics(String eventId, String regionId) {
        showLoading();
        apiRepository.getRegionStatistics(eventId, regionId, new APIRepository.ApiCallback<Statistics>() {
            @Override
            public void onSuccess(Statistics result) {
                hideLoading();
                if (result != null) {
                    updateStatisticsDisplay(result);
                } else {
                    showNoDataMessage();
                }
            }

            @Override
            public void onError(int errorResourceId) {
                hideLoading();
                if (getContext() == null) return;
                String errorMessage = getContext().getResources().getString(errorResourceId);
                utils.showError(getContext(), errorMessage);
                showNoDataMessage();
            }
        });
    }

    private void loadCityStatistics(String eventId, String cityId) {
        showLoading();
        apiRepository.getCityStatistics(eventId, cityId, new APIRepository.ApiCallback<Statistics>() {
            @Override
            public void onSuccess(Statistics result) {
                hideLoading();
                if (result != null) {
                    updateStatisticsDisplay(result);
                } else {
                    showNoDataMessage();
                }
            }

            @Override
            public void onError(int errorResourceId) {
                hideLoading();
                if (getContext() == null) return;
                String errorMessage = getContext().getResources().getString(errorResourceId);
                utils.showError(getContext(), errorMessage);
                showNoDataMessage();
            }
        });
    }

    private void updateRegionsList(List<Region> regions) {
        List<Region> items = new ArrayList<>();
        Region hintRegion = new Region();
        hintRegion.setId("");
        hintRegion.setName("Выберите регион");
        items.add(hintRegion);
        items.addAll(regions);

        ArrayAdapter<Region> regionAdapter = new ArrayAdapter<Region>(requireContext(), android.R.layout.simple_spinner_item, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setText(getItem(position).getName());
                return textView;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setText(getItem(position).getName());
                return textView;
            }
        };
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.regionSpinner.setAdapter(regionAdapter);
        binding.regionSpinner.setSelection(0);
    }

    private void updateCitiesList(List<City> cities) {
        List<City> items = new ArrayList<>();
        City hintCity = new City();
        hintCity.setId("");
        hintCity.setName("Выберите город");
        items.add(hintCity);
        items.addAll(cities);

        ArrayAdapter<City> cityAdapter = new ArrayAdapter<City>(requireContext(), android.R.layout.simple_spinner_item, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setText(getItem(position).getName());
                return textView;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setText(getItem(position).getName());
                return textView;
            }
        };
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.citySpinner.setAdapter(cityAdapter);
        binding.citySpinner.setSelection(0);
    }

    private void updateStatisticsDisplay(Statistics statistics) {
        if (statistics == null) {
            showNoDataMessage();
            return;
        }

        // Секция общей информации
        TextView totalPopulationView = new TextView(requireContext());
        totalPopulationView.setText(String.format("Общая численность населения: %,d чел.", statistics.getTotalPopulation()));
        totalPopulationView.setGravity(Gravity.START);
        binding.generalInfoContent.removeAllViews();
        binding.generalInfoContent.addView(totalPopulationView);

        TextView householdsView = new TextView(requireContext());
        householdsView.setText(String.format("Количество домохозяйств: %,d\nСреднее количество человек в домохозяйстве: %.1f",
                statistics.getTotalHouseholds(), statistics.getAvgPersonsPerHousehold()));
        householdsView.setGravity(Gravity.START);
        binding.generalInfoContent.addView(householdsView);

        // Секция демографии
        TextView genderView = new TextView(requireContext());
        StringBuilder genderText = new StringBuilder("Распределение по полу:\n");
        for (Gender gender : statistics.getGenderDistribution()) {
            String genderType = gender.getType();
            String russianGender;
            switch (genderType.toLowerCase()) {
                case "female":
                    russianGender = "Женский";
                    break;
                case "male":
                    russianGender = "Мужской";
                    break;
                default:
                    russianGender = genderType;
                    break;
            }
            genderText.append(String.format("• %s: %,d чел.\n", russianGender, gender.getCount()));
        }
        genderView.setText(genderText.toString());
        genderView.setGravity(Gravity.START);
        binding.demographicsContent.removeAllViews();
        binding.demographicsContent.addView(genderView);

        TextView ageView = new TextView(requireContext());
        StringBuilder ageText = new StringBuilder();
        ageText.append(String.format("Средний возраст: %.1f лет\n", statistics.getAverageAge()));
        ageText.append(String.format("Количество детей (до 18 лет): %,d чел.\n", statistics.getChildrenCount()));
        ageText.append(String.format("Количество пожилых (65+ лет): %,d чел.", statistics.getElderlyCount()));
        ageView.setText(ageText.toString());
        ageView.setGravity(Gravity.START);
        binding.demographicsContent.addView(ageView);

        // Секция образования
        TextView educationView = new TextView(requireContext());
        StringBuilder educationText = new StringBuilder("Распределение по уровню образования:\n");
        for (Map.Entry<String, Integer> entry : statistics.getEducationDistribution().entrySet()) {
            educationText.append(String.format("• %s: %,d чел.\n", entry.getKey(), entry.getValue()));
        }
        educationView.setText(educationText.toString());
        educationView.setGravity(Gravity.START);
        binding.educationContent.removeAllViews();
        binding.educationContent.addView(educationView);

        TextView languagesView = new TextView(requireContext());
        StringBuilder languagesText = new StringBuilder();
        languagesText.append(String.format("Процент владеющих русским языком: %d%%\n", statistics.getPercentSpeaksRussian()));
        languagesText.append(String.format("Количество лиц с двойным гражданством: %,d чел.\n\n", statistics.getDualCitizenshipCount()));
        languagesText.append("Топ 5 других языков:\n");

        List<Language> languages = statistics.getTopOtherLanguages();
        int count = Math.min(languages.size(), 5); // Ограничиваем до 5 языков
        for (int i = 0; i < count; i++) {
            Language language = languages.get(i);
            languagesText.append(String.format("%d) %s: %,d чел.\n", i + 1, language.getType(), language.getCount()));
        }
        languagesView.setText(languagesText.toString());
        languagesView.setGravity(Gravity.START);
        binding.educationContent.addView(languagesView);
    }

    private void showNoDataMessage() {
        TextView noDataGeneral = new TextView(requireContext());
        noDataGeneral.setText("Нет данных");
        noDataGeneral.setGravity(Gravity.CENTER);
        binding.generalInfoContent.removeAllViews();
        binding.generalInfoContent.addView(noDataGeneral);

        TextView noDataDemographics = new TextView(requireContext());
        noDataDemographics.setText("Нет данных");
        noDataDemographics.setGravity(Gravity.CENTER);
        binding.demographicsContent.removeAllViews();
        binding.demographicsContent.addView(noDataDemographics);

        TextView noDataEducation = new TextView(requireContext());
        noDataEducation.setText("Нет данных");
        noDataEducation.setGravity(Gravity.CENTER);
        binding.educationContent.removeAllViews();
        binding.educationContent.addView(noDataEducation);
    }

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }
} 