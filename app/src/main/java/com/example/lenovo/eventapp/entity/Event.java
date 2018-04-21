package com.example.lenovo.eventapp.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import static com.example.lenovo.eventapp.entity.Event.TABLE_NAME;

/**
 * Created by arun on 20-04-2018.
 */

@Entity(tableName = TABLE_NAME)
public class Event {


    public static final String TABLE_NAME = "events";


    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String date;

    public Event() {

    }

    @Ignore
    public Event(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }


    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

}
