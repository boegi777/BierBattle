package com.fantavier.bierbattle.bierbattle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fantavier.bierbattle.bierbattle.model.Appointment;
import com.fantavier.bierbattle.bierbattle.model.Group;
import com.fantavier.bierbattle.bierbattle.model.DataProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TerminDetail extends AppCompatActivity {

    public TextView votingTitle;
    public TextView title;
    public TextView datum;
    public TextView time;
    public TextView location;
    public TextView positivText;
    public TextView negativText;
    public CheckBox weekly;
    public ImageButton positivButton;
    public ImageButton negativButton;
    public boolean running;
    public static final long SLEEPTIME = 10;
    public String votingText;
    public Long serverTime = 0l;

    private static final String TAG = "TerminDetail";
    private static String index;
    private Appointment appointment;
    private Thread refreshThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termin_detail);

        index = getIntent().getStringExtra("Index");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actiontitle);

        votingTitle = (TextView) findViewById(R.id.votingTitle);
        datum = (TextView) findViewById(R.id.appointmentDate);
        time = (TextView) findViewById(R.id.appointmentTime);
        location = (TextView) findViewById(R.id.appointmentLocation);
        positivText = (TextView) findViewById(R.id.votingPositiv);
        negativText = (TextView) findViewById(R.id.votingNegativ);
        weekly = (CheckBox) findViewById(R.id.appointmentWeekly);
        positivButton = findViewById(R.id.positivButton);
        negativButton = findViewById(R.id.negativButton);

    }

    public void onResume(){
        super.onResume();

        MainActivity.dataProvider.setGroupDataListener(new DataProvider.GroupDataListener() {
            @Override
            public void onGroupeDataChanged(Group group) {
                setAppointmentViewData(group);
            }
        });

        setAppointmentViewData(MainActivity.activeGroup);
    }

    public void onPause(){
        super.onPause();

        running = false;
    }
    public void onDestroy(){
        super.onDestroy();

        running = false;
    }
    private void setAppointmentViewData(Group group){
        Integer positiv = 0;
        Integer negativ = 0;

        appointment = group.getAppointment(index);

        title.setText(appointment.getTitle());
        datum.setText(appointment.getDate());
        time.setText(appointment.getTime());
        weekly.setChecked(appointment.getWeekly());
        location.setText(appointment.getLocation());

        if(appointment.getVotings() != null){
            Iterator it = appointment.getVotings().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if((Boolean) pair.getValue()){
                    positiv += 1;
                } else {
                    negativ += 1;
                }
            }
        }

        TerminDetail.this.positivText.setText(positiv.toString());
        TerminDetail.this.negativText.setText(negativ.toString());

        if(!appointment.getVotingend()){
            setVotingTitle();

            positivButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    TerminDetail.this.appointment.setVoting(uid, true);
                }
            });
            negativButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    TerminDetail.this.appointment.setVoting(uid, false);
                }
            });
        } else {
            votingTitle.setText("Termin aktiv");
        }
    }

    private void setVotingTitle(){
        running = true;

        if (refreshThread == null) {
            refreshThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        try {
                            votingText = getVotingText();
                            Thread.sleep(TerminDetail.SLEEPTIME);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TerminErstellen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                votingTitle.setText(votingText);
                            }
                        });
                    }
                }
            });
            refreshThread.start();
        }
    }

    private String getVotingText(){
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long plus24 = appointment.getCreatetime() + daysInMilli;

        serverTime = System.currentTimeMillis();

        long timeDiff = plus24 - serverTime;

        if(timeDiff > 0) {

            timeDiff = timeDiff % daysInMilli;

            Long hours = timeDiff / hoursInMilli;
            timeDiff = timeDiff % hoursInMilli;

            Long minutes = timeDiff / minutesInMilli;
            timeDiff = timeDiff % minutesInMilli;

            Long seconds = timeDiff / secondsInMilli;

            return "Abstimmung\n" + hours + ":" + minutes + ":" + seconds;
        } else {
            return "Abstimmung beendet";
        }
    }
}
