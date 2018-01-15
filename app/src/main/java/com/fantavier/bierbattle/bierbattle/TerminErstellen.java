package com.fantavier.bierbattle.bierbattle;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TerminErstellen extends AppCompatActivity {

    public static final String TAG = "TerminErstellen";

    private TextView activityTitle;
    private TextView title;
    private TextView date;
    private TextView time;
    private TextView location;
    private CheckBox weekly;
    private Button create;
    private DatePickerDialog.OnDateSetListener datePicker;
    private TimePickerDialog.OnTimeSetListener timePicker;
    public Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termin_erstellen);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        activityTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actiontitle);
        activityTitle.setText("Termin erstellen");

        title = (TextView) findViewById(R.id.titleText);
        date = (TextView) findViewById(R.id.dateText);
        time = (TextView) findViewById(R.id.timeText);
        location = (TextView) findViewById(R.id.locationText);
        weekly = (CheckBox) findViewById(R.id.appointmentWeekly);
        create = (Button) findViewById(R.id.createButton);

        calendar = Calendar.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();

        datePicker = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                updateDate();
            }

        };


        timePicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                 calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE), i, i1);
                updateTime();
            }
        };



        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(TerminErstellen.this, datePicker, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(TerminErstellen.this, timePicker, calendar
                .get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    boolean result = MainActivity.dataProvider.getActiveGroup().createAppointment(
                            title.getText().toString(),
                            date.getText().toString(),
                            time.getText().toString(),
                            location.getText().toString(),
                            weekly.isChecked()
                    );
                    Toast.makeText(TerminErstellen.this, "Termin erstellt",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } catch(Exception | ExceptionInInitializerError e){
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(TerminErstellen.this, "Termin konnte nicht erstellt werden.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDate() {
        String format = "dd.MM.yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.GERMANY);

        date.setText(sdf.format(calendar.getTime()));
    }

    private void updateTime() {
        String format = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.GERMANY);

        time.setText(sdf.format(calendar.getTime()));
    }
}
