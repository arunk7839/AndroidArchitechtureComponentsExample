package com.example.lenovo.eventapp.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.lenovo.eventapp.entity.Event;

import java.util.List;


/**
 * Created by arun on 20-04-2018.
 */

@Dao
public interface EventDao {

    @Query("SELECT * FROM " + Event.TABLE_NAME )
    List<Event> getEvents();

    @Insert
    void addEvent(Event event);

    @Delete
    void deleteEvent(Event event);

    @Update
    void updateEvent(Event event);

}
