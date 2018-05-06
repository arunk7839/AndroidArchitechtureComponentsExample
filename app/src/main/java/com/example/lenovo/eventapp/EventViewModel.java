package com.example.lenovo.eventapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.example.lenovo.eventapp.db.EventDatabase;
import com.example.lenovo.eventapp.entity.Event;
import java.util.List;

/**
 * Created by Arun on 01-05-2018.
 */

public class EventViewModel extends AndroidViewModel {

    private EventDatabase eventDatabase;

    private LiveData<List<Event>> eventList;

    public EventViewModel(@NonNull Application application) {
        super(application);

        eventDatabase = EventDatabase.getEventDatabase(this.getApplication());

        eventList = eventDatabase.eventDao().getEvents();
    }

    public LiveData<List<Event>> getEventList() {
        return eventList;
    }

    public EventDatabase getEventDatabase() {
        return eventDatabase;
    }

}
