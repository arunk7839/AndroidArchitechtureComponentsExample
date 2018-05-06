package com.example.lenovo.eventapp.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.lenovo.eventapp.entity.Event;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


/**
 * Created by Arun on 01-05-2018.
 */

@Dao
public interface EventDao {

    @Query("SELECT * FROM " + Event.TABLE_NAME)
    LiveData<List<Event>> getEvents();

    @Insert(onConflict = REPLACE)
    void addEvent(Event event);

    @Delete
    void deleteEvent(Event event);

    @Update(onConflict = REPLACE)
    void updateEvent(Event event);

}
