package com.fantavier.bierbattle.bierbattle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fantavier.bierbattle.bierbattle.helper.DateHelper;
import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;
import com.fantavier.bierbattle.bierbattle.model.Appointment;
import com.fantavier.bierbattle.bierbattle.model.DataProvider;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TerminDetail extends AppCompatActivity {

    public TextView appointmentTitle;
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
    public String appointmentTitleText;

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

        appointmentTitle = (TextView) findViewById(R.id.votingTitle);
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
        setAppointmentViewData();
        running = true;
    };

    public void onPause(){
        super.onPause();
    }
    public void onDestroy(){
        super.onDestroy();
        running = false;
    }
    public Appointment getAppointment(){
        return appointment;
    }

    private void setAppointmentViewData(){
        appointment = MainActivity.dataProvider.getActiveGroup().getAppointment(Integer.parseInt(index));

        title.setText(appointment.getTitle());
        datum.setText(appointment.getDate());
        time.setText(appointment.getTime());
        weekly.setChecked(appointment.getWeekly());
        location.setText(appointment.getLocation());

        TerminDetail.this.positivText.setText(appointment.getPositivVotings());
        TerminDetail.this.negativText.setText(appointment.getNegativVotings());

        setVotingListener();
        setAppointmentListener();

        //if(!appointment.getVotingend() || appointment.isStarted()){
            setTitle();

            positivButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid = MainActivity.dataProvider.getActiveUser().getUserId();
                    TerminDetail.this.getAppointment().setVoting(uid, true);
                }
            });
            negativButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid = MainActivity.dataProvider.getActiveUser().getUserId();
                    TerminDetail.this.getAppointment().setVoting(uid, false);
                }
            });
        //} else {
            //appointmentTitle.setText("Abstimmung\n beendet");
        //}
    }

    private void setVotingListener(){
        MainActivity.dataProvider.setGroupDataListener(new DataProvider.GroupDataListener() {
            @Override
            public void onGroupeDataChanged() {
                if(appointment.getVotings() != null){
                    TerminDetail.this.positivText.setText(appointment.getPositivVotings());
                    TerminDetail.this.negativText.setText(appointment.getNegativVotings());
                }
            }
        });
    }

    private void setTitle(){
        running = true;
        if (refreshThread == null) {
            refreshThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        try {
                            if(appointment.isStarted()){
                                appointmentTitleText = getActiveCounterText();
                            } else if(!appointment.getVotingend()){
                                appointmentTitleText = getVotingText();
                            } else {
                                appointmentTitleText = "Abstimmung\n beendet";
                            }

                            Thread.sleep(TerminDetail.SLEEPTIME);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TerminErstellen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                appointmentTitle.setText(appointmentTitleText);
                            }
                        });
                    }
                }
            });
            refreshThread.start();
        }
    }

    private String getActiveCounterText(){
        try {
            HashMap<String, String> timeDiff = appointment.getActivetimeLeft();
            return "Termin aktiv\n" + timeDiff.get("hours") + ":" + timeDiff.get("minutes") + ":" + timeDiff.get("seconds");
        } catch(ExceptionHelper.StarttimeException ex) {
            return ex.getMessage();
        }
    }

    private String getVotingText(){
        try {
            HashMap<String, String> timeDiff = appointment.getVotingtimeLeft();
            return "Abstimmung\n" + timeDiff.get("hours") + ":" + timeDiff.get("minutes") + ":" + timeDiff.get("seconds");
        } catch(ExceptionHelper.VotingendException ex){
            return ex.getMessage();
        }
    }

    private void setAppointmentListener(){
        MainActivity.dataProvider.setVotingEndsListener(new DataProvider.VotingEndsListener() {
            @Override
            public void onVotingEnds(Appointment appointment) {
                positivButton.setOnClickListener(null);
                negativButton.setOnClickListener(null);
            }
        });
    }
}
