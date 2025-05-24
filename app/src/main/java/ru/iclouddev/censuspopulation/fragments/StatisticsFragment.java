package ru.iclouddev.censuspopulation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.iclouddev.censuspopulation.ContainerIRSActivity;
import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.api.APIRepository;
import ru.iclouddev.censuspopulation.api.models.Event;
import ru.iclouddev.censuspopulation.api.models.Events;
import ru.iclouddev.censuspopulation.databinding.FragmentStatisticsBinding;
import ru.iclouddev.censuspopulation.dialogs.EventsDialog;
import ru.iclouddev.censuspopulation.utils.Utils;
import ru.iclouddev.censuspopulation.viewmodels.CensusViewModel;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {
    private FragmentStatisticsBinding binding;
    private APIRepository apiRepository;
    private Utils utils;
    private List<Event> events;
    private Event selectedEvent;
    private ProgressBar progressBar;
    private CensusViewModel censusViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        events = new ArrayList<>();
        apiRepository = new APIRepository();
        utils = new Utils();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        FloatingActionButton selectCensusButton = binding.selectCensusButton;
        progressBar = requireView().findViewById(R.id.progressBar);

        if (getActivity() instanceof ContainerIRSActivity) {
            censusViewModel = ((ContainerIRSActivity) getActivity()).getCensusViewModel();
            censusViewModel.getSelectedCensusEvent().observe(getViewLifecycleOwner(), event -> {
                selectedEvent = event;
                if (event != null) {
                    loadStatistics(event.getId());
                }
            });
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

    private void loadStatistics(String eventId) {}

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