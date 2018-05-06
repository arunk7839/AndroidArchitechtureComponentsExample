package com.example.lenovo.eventapp.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.lenovo.eventapp.dao.EventDao;
import com.example.lenovo.eventapp.entity.Event;

/**
 * Created by Arun on 01-05-2018.
 */

@Database(entities = {Event.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase {
    private static final String DB_NAME = "Event_Database.db";
    private static EventDatabase INSTANCE;


    public static EventDatabase getEventDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, EventDatabase.class, DB_NAME).build();

        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract EventDao eventDao();
}
