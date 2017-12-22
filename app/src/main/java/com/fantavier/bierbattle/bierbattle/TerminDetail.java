package com.fantavier.bierbattle.bierbattle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

public class TerminDetail extends AppCompatActivity {

    private GroupProvider.Appointment appointment;

    private static final String TAG = "TerminDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termin_detail);

        String index = getIntent().getStringExtra("Index");
        appointment = MainActivity.activeGroup.getAppointment(index);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actiontitle);
        title.setText(appointment.getTitle());

        TextView datum = (TextView) findViewById(R.id.appointmentDate);
        TextView time = (TextView) findViewById(R.id.appointmentTime);
        TextView location = (TextView) findViewById(R.id.appointmentLocation);
        CheckBox weekly = (CheckBox) findViewById(R.id.appointmentWeekly);

        datum.setText(appointment.getDate());
        time.setText(appointment.getTime());
        weekly.setChecked(appointment.getWeekly());
        location.setText(appointment.getLocation());
    }
}
