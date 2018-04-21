package com.example.lenovo.eventapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by arun on 20-04-2018.
 */

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noEventsFound;
    FloatingActionButton fab;
    EventsAdapter adapter;
    List<Event> events = new ArrayList<>();
    private static TextView tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noEventsFound = (TextView) findViewById(R.id.tv_no_events_found);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_list_events);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        //show list of events if exists in database
        new DatabaseAsync().execute(null, -1, null, null, null);

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

    private class DatabaseAsync extends AsyncTask<Object, Void, List<Event>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Perform pre-adding operation here.
        }

        @Override
        protected List<Event> doInBackground(Object... params) {

            Boolean shouldUpdate = (Boolean) params[0];
            int position = (int) params[1];
            String title = (String) params[2];
            String detail = (String) params[3];
            String date = (String) params[4];

            //check whether to add add or update event
            if (shouldUpdate != null) {
                //update event
                if (shouldUpdate) {
                    Event event = events.get(position);
                    event.setTitle(title);
                    event.setDescription(detail);
                    event.setDate(date);

                    //update event into the database
                    EventDatabase.getEventDatabase(getApplicationContext()).eventDao().updateEvent(event);

                } else {
                    //add event
                    Event event = new Event();
                    event.setTitle(title);
                    event.setDescription(detail);
                    event.setDate(date);

                    //add event into the database
                    EventDatabase.getEventDatabase(getApplicationContext()).eventDao().addEvent(event);
                }

            } else {
                //delete event
                if (position != -1) {
                    Event event = events.get(position);

                    //delete event from database
                    EventDatabase.getEventDatabase(getApplicationContext()).eventDao().deleteEvent(event);
                }
            }

            //get events from database
            List<Event> events = EventDatabase.getEventDatabase(getApplicationContext()).eventDao().getEvents();
            return events;

        }

        @Override
        protected void onPostExecute(List<Event> items) {

//get list of events from doInBackground()
            events = items;

            adapter = new EventsAdapter(events, getApplicationContext());

            recyclerView.setAdapter(adapter);

            //shows NO EVENTS FOUND when list is empty
            checkListEmptyOrNot();
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

        new DatabaseAsync().execute(null, position, null, null, null);
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
                showDateDialog();
            }
        });

        dialog_title.setText(!shouldUpdate ? "New Event" : "Edit Event");

        //in case of update we want all the
        //fields  to be set bydefault with text
        if (shouldUpdate && event != null) {
            edt_title.setText(event.getTitle());
            edt_discription.setText(event.getDescription());
            tv_date.setText(event.getDate());
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
                }
                else if (!TextUtils.isEmpty(edt_title.getText().toString()) && TextUtils.isEmpty(edt_discription.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter description!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(edt_title.getText().toString()) && TextUtils.isEmpty(edt_discription.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter title and description!", Toast.LENGTH_SHORT).show();
                }
                else {
                    alertDialog.dismiss();
                }

                //Update or add data into the database only when both field are  filled(i.e title,description)
                if (!TextUtils.isEmpty(edt_title.getText().toString()) && !TextUtils.isEmpty(edt_discription.getText().toString())) {

                    // check if user updating note

                    if (shouldUpdate && event != null) {
                        // update event
                        new DatabaseAsync().execute(shouldUpdate, position, edt_title.getText().toString(), edt_discription.getText().toString(), tv_date.getText().toString());


                    } else {
                        // create new event
                        new DatabaseAsync().execute(shouldUpdate, -1, edt_title.getText().toString(), edt_discription.getText().toString(), tv_date.getText().toString());


                    }
                }
            }
        });


    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String monthVal, dayVal = "" + day;
            if ((month + 1) < 10) {
                month += 1;
                monthVal = "0" + month;
            } else {
                month += 1;
                monthVal = "" + month;
            }
            if (day < 10) {
                dayVal = "0" + day;
            }
            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
            Date date = new Date(year, month, day - 3);
            String dayOfWeek = simpledateformat.format(date);


            tv_date.setText(dayOfWeek + "\n" + dayVal + "-" + monthVal + "-" + year);

        }
    }

    private void showDateDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void checkListEmptyOrNot() {
        if (events.isEmpty())
            noEventsFound.setVisibility(View.VISIBLE);
        else
            noEventsFound.setVisibility(View.GONE);
    }
}




