package ru.iclouddev.censuspopulation.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.api.models.Event;

public class CensusEventsAdapter extends RecyclerView.Adapter<CensusEventsAdapter.ViewHolder> {
    private static final String TAG = "CensusEventsAdapter";
    private final List<Event> events;
    private final OnCensusEventClickListener listener;
    private Event selectedEvent;
    private SimpleDateFormat dateFormat;

    public interface OnCensusEventClickListener {
        void onCensusEventClick(Event event);
    }

    public CensusEventsAdapter(List<Event> events, OnCensusEventClickListener listener) {
        this.events = events;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    }

    public void setSelectedEvent(Event event) {
        Log.d(TAG, "Setting selected event: " + (event != null ? event.getName() : "null"));
        this.selectedEvent = event;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.nameTextView.setText(event.getName());

        String dateRange = String.format("%s — %s",
                dateFormat.format(event.getStartDateTime()),
                dateFormat.format(event.getEndDateTime()));
        holder.dateTextView.setText(dateRange);

        // Устанавливаем выделение для выбранной переписи
        boolean isSelected = selectedEvent != null && selectedEvent.getId().equals(event.getId());
        Log.d(TAG, String.format("Position %d: %s, isSelected: %b",
                position, event.getName(), isSelected));

        holder.itemView.setBackgroundResource(isSelected ?
                R.drawable.selected_item_background : android.R.color.transparent);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCensusEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEvents(List<Event> newEvents) {
        events.clear();
        events.addAll(newEvents);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView dateTextView;

        ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.nameTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
        }
    }
} 