package ru.iclouddev.censuspopulation.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.adapters.CensusEventsAdapter;
import ru.iclouddev.censuspopulation.api.APIRepository;
import ru.iclouddev.censuspopulation.api.models.Event;
import ru.iclouddev.censuspopulation.api.models.Events;
import ru.iclouddev.censuspopulation.utils.Utils;

public class EventsDialog extends Dialog {
    private static final String TAG = "CensusEventsDialog";
    private static final int PAGE_SIZE = 10;

    private final APIRepository apiRepository;
    private final Utils utils;
    private final OnCensusEventSelectedListener listener;
    private final List<Event> events;
    private final CensusEventsAdapter adapter;
    private final Event selectedEvent;

    private RecyclerView eventsRecyclerView;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private TextView pageInfoTextView;
    private ImageButton refreshButton;
    private ImageButton closeButton;

    private int currentPage = 0;
    private int totalItems = 0;

    public interface OnCensusEventSelectedListener {
        void onCensusEventSelected(Event event);
    }

    public EventsDialog(@NonNull Context context, OnCensusEventSelectedListener listener, Event selectedEvent) {
        super(context);
        this.listener = listener;
        this.selectedEvent = selectedEvent;
        this.events = new ArrayList<>();
        this.adapter = new CensusEventsAdapter(events, this::onEventSelected);
        this.apiRepository = new APIRepository();
        this.utils = new Utils();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_events);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }

        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageInfoTextView = findViewById(R.id.pageInfoTextView);
        refreshButton = findViewById(R.id.refreshButton);
        closeButton = findViewById(R.id.closeButton);

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecyclerView.setAdapter(adapter);

        prevButton.setOnClickListener(v -> loadPreviousPage());
        nextButton.setOnClickListener(v -> loadNextPage());
        refreshButton.setOnClickListener(v -> refreshData());
        closeButton.setOnClickListener(v -> dismiss());

        loadCensusEvents();
    }

    private void loadCensusEvents() {
        Log.d(TAG, "Loading census events, selected event: " +
                (selectedEvent != null ? selectedEvent.getName() : "null"));

        apiRepository.getCensusEvents(PAGE_SIZE, currentPage * PAGE_SIZE, new APIRepository.ApiCallback<Events>() {
            @Override
            public void onSuccess(Events response) {
                events.clear();
                events.addAll(List.of(response.getEvents()));
                totalItems = response.getTotal();

                // Если есть выбранная перепись, находим её и прокручиваем к ней
                if (selectedEvent != null) {
                    int position = findSelectedEventPosition();
                    Log.d(TAG, "Found selected event at position: " + position);
                    if (position != -1) {
                        eventsRecyclerView.scrollToPosition(position);
                    }
                }

                adapter.setSelectedEvent(selectedEvent);
                adapter.notifyDataSetChanged();
                updatePaginationControls();
            }

            @Override
            public void onError(int errorResourceId) {
                String errorMessage = getContext().getResources().getString(errorResourceId);
                utils.showError(getContext(), errorMessage);
            }
        });
    }

    private int findSelectedEventPosition() {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(selectedEvent.getId())) {
                return i;
            }
        }
        return -1;
    }

    private void loadPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadCensusEvents();
        }
    }

    private void loadNextPage() {
        if ((currentPage + 1) * PAGE_SIZE < totalItems) {
            currentPage++;
            loadCensusEvents();
        }
    }

    private void updatePaginationControls() {
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        pageInfoTextView.setText(String.format("Страница %d из %d", currentPage + 1, totalPages));

        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled((currentPage + 1) * PAGE_SIZE < totalItems);
    }

    private void refreshData() {
        currentPage = 0;
        loadCensusEvents();
    }

    private void onEventSelected(Event event) {
        if (listener != null) {
            listener.onCensusEventSelected(event);
        }
        dismiss();
    }
} 