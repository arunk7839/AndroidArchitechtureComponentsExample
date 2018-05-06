package com.example.lenovo.eventapp.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Arun on 01-05-2018.
 */

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
