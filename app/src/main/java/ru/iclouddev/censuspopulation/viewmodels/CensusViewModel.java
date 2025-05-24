package ru.iclouddev.censuspopulation.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.iclouddev.censuspopulation.api.models.Event;

public class CensusViewModel extends ViewModel {
    private final MutableLiveData<Event> selectedCensusEvent = new MutableLiveData<>();

    public LiveData<Event> getSelectedCensusEvent() {
        return selectedCensusEvent;
    }

    public void setSelectedCensusEvent(Event event) {
        selectedCensusEvent.setValue(event);
    }
}