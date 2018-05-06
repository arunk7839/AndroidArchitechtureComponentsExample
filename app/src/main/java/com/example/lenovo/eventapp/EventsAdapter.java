package com.example.lenovo.eventapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.eventapp.entity.Event;

import java.util.List;

/**
 * Created by Arun on 01-05-2018.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private final Context context;
    private List<Event> items;


    public EventsAdapter(List<Event> items, Context context) {
        this.items = items;
        this.context = context;

    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView descriptionTextView;

        EventViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.tv_title);
            dateTextView = (TextView) v.findViewById(R.id.tv_timestamp);
            descriptionTextView = (TextView) v.findViewById(R.id.tv_discription);

        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.list_item_event, parent, false);

        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event item = items.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());
        holder.dateTextView.setText(item.getDate().toLocaleString().substring(0, 12));
    }


    @Override
    public int getItemCount() {

        return items.size();
    }


}
