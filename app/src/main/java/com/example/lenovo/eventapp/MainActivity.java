package com.example.lenovo.eventapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lenovo.eventapp.db.EventDatabase;
import com.example.lenovo.eventapp.entity.Event;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Arun on 01-05-2018.
 */

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerView;
    private Date date;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private EventDatabase eventDatabase;
    private EventViewModel viewModel;
    private TextView noEventsFound;
    private FloatingActionButton fab;
    private EventsAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        viewModel = ViewModelProviders.of(this).get(EventViewModel.class);

        eventDatabase = viewModel.getEventDatabase();

        noEventsFound = (TextView) findViewById(R.id.tv_no_events_found);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_list_events);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        viewModel.getEventList().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable List<Event> eventList) {
                events = eventList;

                adapter = new EventsAdapter(events, getApplicationContext());

                recyclerView.setAdapter(adapter);

                checkListEmptyOrNot();
            }
        });

        //shows NO EVENTS FOUND when list is empty
        checkListEmptyOrNot();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

                //show Alertdialog to edit or update the event
                showActionsDialog(position);
            }
        }));

        //add listener on FloatingActionButton to add event
        fab = (FloatingActionButton) findViewById(R.id.fab_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //adding  new event
                showEventDialog(false, null, -1);
            }
        });
    }

    private class DatabaseAsync extends AsyncTask<Object, Void, Void> {

        private EventDatabase db;

        DatabaseAsync(EventDatabase eventDatabase) {
            db = eventDatabase;
        }

        @Override
        protected Void doInBackground(Object... params) {

            Boolean shouldUpdate = (Boolean) params[0];
            int position = (int) params[1];
            String title = (String) params[2];
            String detail = (String) params[3];
            Date date = (Date) params[4];

            //check whether to add add or update event
            if (shouldUpdate != null) {
                //update event
                if (shouldUpdate) {
                    Event event = events.get(position);
                    event.setTitle(title);
                    event.setDescription(detail);
                    event.setDate(date);

                    //update event into the database
                    db.eventDao().updateEvent(event);


                } else {
                    //add event
                    Event event = new Event();
                    event.setTitle(title);
                    event.setDescription(detail);
                    event.setDate(date);

                    //add event into the database
                    db.eventDao().addEvent(event);
                }

            } else {
                //delete event
                if (position != -1) {
                    Event event = events.get(position);

                    //delete event from database
                    db.eventDao().deleteEvent(event);
                }
            }
            return null;

        }
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                    //show Alertdialog to update the event
                    showEventDialog(true, events.get(position), position);
                } else {

                    //delete event from database
                    deleteEvent(position);
                }
            }
        });
        builder.show();
    }

    private void deleteEvent(int position) {

        new DatabaseAsync(eventDatabase).execute(null, position, null, null, null);
    }

    private void showEventDialog(final Boolean shouldUpdate, final Event event, final int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.event_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(view);

        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);

        final EditText edt_title = (EditText) view.findViewById(R.id.edt_title);

        final EditText edt_discription = (EditText) view.findViewById(R.id.edt_discription);

        tv_date = (TextView) view.findViewById(R.id.tv_date);

        Button btn_setdate = (Button) view.findViewById(R.id.btn_setdate);

        //add listener to button to open datepickerdialog
        btn_setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //open datepickerdialog
                datePickerDialog.show();
            }
        });

        dialog_title.setText(!shouldUpdate ? "New Event" : "Edit Event");

        //in case of update we want all the
        //fields  to be set bydefault with text
        if (shouldUpdate && event != null) {
            edt_title.setText(event.getTitle());
            edt_discription.setText(event.getDescription());
            tv_date.setText(event.getDate().toLocaleString().substring(0, 11));
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show toast message when no text is entered
                if (TextUtils.isEmpty(edt_title.getText().toString()) && !TextUtils.isEmpty(edt_discription.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter title!", Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(edt_title.getText().toString()) && TextUtils.isEmpty(edt_discription.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter description!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edt_title.getText().toString()) && TextUtils.isEmpty(edt_discription.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter title and description!", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                }

                //Update or add data into the database only when both field are  filled(i.e title,description)
                if (!TextUtils.isEmpty(edt_title.getText().toString()) && !TextUtils.isEmpty(edt_discription.getText().toString())) {

                    // check if user updating note

                    if (shouldUpdate && event != null) {
                        // update event
                        new DatabaseAsync(eventDatabase).execute(shouldUpdate, position, edt_title.getText().toString(), edt_discription.getText().toString(), date);


                    } else {
                        // create new event
                        new DatabaseAsync(eventDatabase).execute(shouldUpdate, -1, edt_title.getText().toString(), edt_discription.getText().toString(), date);


                    }
                }
            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date = calendar.getTime();
        tv_date.setText(date.toLocaleString().substring(0, 12));

    }


    public void checkListEmptyOrNot() {
        if (events.isEmpty())
            noEventsFound.setVisibility(View.VISIBLE);
        else
            noEventsFound.setVisibility(View.GONE);
    }


}




