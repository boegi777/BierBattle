package com.fantavier.bierbattle.bierbattle.model;

import android.provider.ContactsContract;
import android.util.Log;

import com.fantavier.bierbattle.bierbattle.helper.DateHelper;
import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Appointment implements DataProvider.DatabaseReferenceObject {

    public static int count = 0;

    private static final String TAG = "Appointment";
    private static List<Thread> threads = null;
    private DatabaseReference dbRef = null;
    private HashMap<String, Boolean> votings = null;
    private Group parentRef = null;
    private DataProvider.PropertiesLoaded propertiesLoaded = null;
    private Long createtime = 0l;
    private String appointmentId = "";
    private String title = "";
    private String date = "";
    private String time = "";
    private String location = "";
    private Boolean votingend = false;
    private Boolean weekly = false;
    private Boolean active = false;
    private Boolean started = false;
    private Boolean loaded = false;
    private Boolean blocked = false;

    public Appointment(Group parentRef){
        this.parentRef = parentRef;
    }

    public Long getCreatetime() { return this.createtime; }
    public String getTitle() { return this.title; }
    public String getDate(){
        return this.date;
    }
    public String getTime(){
        return this.time;
    }
    public String getLocation(){
        return this.location;
    }
    public Boolean getVotingend(){ return this.votingend; }
    public Boolean getActive(){ return this.active; }
    public Boolean getWeekly(){
        return this.weekly;
    }
    public Boolean isLoaded() { return this.loaded; }
    public Boolean isBlocked() { return this.blocked; }
    public HashMap<String, Boolean> getVotings(){ return this.votings; }

    public void setBlocked(Boolean blocked){
        this.blocked = blocked;
    }

    public void setVoting(String uid, Boolean vote){
        dbRef.child("votings").child(uid).setValue(vote);
    }

    public static void clearThreadList(){
        if(threads == null){
            threads = new ArrayList<Thread>();
        }
        for(Thread thread : threads){
            if(thread != null)
                thread.interrupt();
        }
        threads.clear();
    }

    public Boolean isStarted(){
        return  this.started;
    }

    @Override
    public void loadObjectProperties(String id) {
        this.appointmentId = id;
        this.dbRef = getDbRef();
        this.dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot appointmentDS) {
                for(DataSnapshot appointmentData : appointmentDS.getChildren()) {
                    switch (appointmentData.getKey()) {
                        case "title":
                            Appointment.this.title = appointmentData.getValue().toString();
                            break;
                        case "createtime":
                            Appointment.this.createtime = Long.parseLong(appointmentData.getValue().toString());
                            break;
                        case "date":
                            Appointment.this.date = appointmentData.getValue().toString();
                            break;
                        case "time":
                            Appointment.this.time = appointmentData.getValue().toString();
                            break;
                        case "votingend":
                            Appointment.this.votingend = Boolean.parseBoolean(appointmentData.getValue().toString());
                            break;
                        case "votings":
                            Appointment.this.votings = initVotings(appointmentData);
                            break;
                        case "weekly":
                            Appointment.this.weekly  = Boolean.parseBoolean(appointmentData.getValue().toString());
                            break;
                        case "location":
                            Appointment.this.location = appointmentData.getValue().toString();
                            break;
                        case "active":
                            Appointment.this.active  = Boolean.parseBoolean(appointmentData.getValue().toString());
                            break;
                    }
                }
                loaded = true;
                if(Appointment.this.getActive() || !Appointment.this.getVotingend()){
                    Appointment.this.watchAppointment();
                } else {
                    parentRef.getAppointments().remove(appointmentId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Override
    public void setPropertiesLoaded(DataProvider.PropertiesLoaded listener) {
        propertiesLoaded = listener;
    }

    @Override
    public DatabaseReference getDbRef() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("groups");
        DatabaseReference groupRef = dbRef.child(parentRef.getGroupId());
        return groupRef.child("appointments").child(this.appointmentId);
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.title);
        if(this.votingend == false){
            builder.append("   -- Abstimmung -- ");
        }
        return builder.toString();
    }

    public Long getDateInMilliSec() {
        return DateHelper.convertDateToMilliSec(getDate(), getTime());
    }

    public String getPositivVotings(){
        if(this.getVotings() == null){
            return "0";
        }
        Integer positiv = 0;
        Iterator it = this.getVotings().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if((Boolean) pair.getValue())
                positiv += 1;
        }
        return positiv.toString();
    }

    public String getNegativVotings(){
        if(this.getVotings() == null){
            return "0";
        }
        Integer negativ = 0;
        Iterator it = this.getVotings().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(!(Boolean) pair.getValue())
                negativ += 1;
        }
        return negativ.toString();
    }

    public Long getActivetimeLeftInMilli(){
        Long starttimeMilli = DateHelper.convertDateToMilliSec(this.getDate(), this.getTime());
        Long plusTime = starttimeMilli + 600000;
        return plusTime - System.currentTimeMillis();
    }

    public Long getVotingtimeLeftInMilli(){
        Long plusTime = this.getCreatetime() + 60000;
        return plusTime - System.currentTimeMillis();
    }

    public Long getTimeUntilStart() {
        Long startTimeMilli = DateHelper.convertDateToMilliSec(getDate(), getTime());
        return startTimeMilli - System.currentTimeMillis();
    }

    public HashMap<String, String> getActivetimeLeft() throws ExceptionHelper.StarttimeException{
        HashMap<String, String> timeDiffStrings = new HashMap<>();
        HashMap<String, Long> timeDiffLongs = new HashMap<>();
        Long timeDiffMilli = this.getActivetimeLeftInMilli();

        if(timeDiffMilli > 0) {
            timeDiffLongs = DateHelper.getTimeLeft(timeDiffMilli);
            timeDiffStrings.put("hours", timeDiffLongs.get("hours").toString());
            timeDiffStrings.put("minutes", timeDiffLongs.get("minutes").toString());
            timeDiffStrings.put("seconds", timeDiffLongs.get("seconds").toString());
            return timeDiffStrings;
        } else {
            throw new ExceptionHelper.StarttimeException();
        }

    }

    public HashMap<String, String> getVotingtimeLeft() throws ExceptionHelper.VotingendException{
        HashMap<String, String> timeDiffStrings = new HashMap<>();
        HashMap<String, Long> timeDiffLongs = new HashMap<>();
        Long timeDiffMilli = this.getVotingtimeLeftInMilli();

        if(timeDiffMilli > 0) {
            timeDiffLongs = DateHelper.getTimeLeft(timeDiffMilli);
            timeDiffStrings.put("hours", timeDiffLongs.get("hours").toString());
            timeDiffStrings.put("minutes", timeDiffLongs.get("minutes").toString());
            timeDiffStrings.put("seconds", timeDiffLongs.get("seconds").toString());
            return timeDiffStrings;
        } else {
            throw new ExceptionHelper.VotingendException();
        }

    }

    public void watchAppointment(){
        if(!getVotingend()) {
            watchVotingEnd();
        } else if(getActive()){
            watchAppointmentStart();
        }
    }

    public void checkAppointmentStatus(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String appointmentId = Appointment.this.appointmentId;
                    String groupId = Appointment.this.parentRef.getGroupId();
                    URL url = new URL("https://us-central1-bierbattle.cloudfunctions.net/checkAppointment");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                    httpRequestBodyWriter.write("groupId="+groupId+"&appointmentId="+appointmentId);
                    httpRequestBodyWriter.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    Thread.interrupted();
                } catch(IOException ex){
                    Thread.interrupted();
                    Log.d(TAG, ex.getMessage());
                }
            }
        });
        thread.start();
    }

    private HashMap<String, Boolean> initVotings(DataSnapshot votingsDS){
        HashMap<String, Boolean> votings = new HashMap<>();

        for(DataSnapshot voting : votingsDS.getChildren()){
            boolean value = (boolean) voting.getValue();
            votings.put(voting.getKey().toString(), value);
        }

        return votings;

    }

    private void watchVotingEnd(){
        final Long timeLeft = this.getVotingtimeLeftInMilli();
        if (timeLeft > 0) {
            createVotingWatcherThread(timeLeft);
        }
    }

    private void watchAppointmentStart(){
        final Long timeLeft = this.getTimeUntilStart();
        if(timeLeft > 0){
            createWatcherStartThread(timeLeft, false);
        }
        if(timeLeft < 0 && timeLeft > -600000){
            createWatcherStartThread(timeLeft + 600000, true);
        }
    }

    private void createVotingWatcherThread(final Long timeLeft){
        if(!checkThreadIsRunning("voting")) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(timeLeft);
                        DataProvider.votingEndsListener.onVotingEnds(Appointment.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setName(Appointment.this.appointmentId + "-voting");
            Appointment.threads.add(thread);
            thread.start();
        }
    }

    private void createWatcherStartThread(final Long timeLeft, final Boolean alreadyStarts) {
        if(!checkThreadIsRunning("start")) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (alreadyStarts) {
                            Appointment.this.started = true;
                        }
                        Thread.sleep(timeLeft);
                        if (alreadyStarts) {
                            DataProvider.appointmentEndsListener.onAppointmentEnds(Appointment.this);
                            Appointment.this.started = false;
                        } else {
                            Appointment.this.started = true;
                            DataProvider.appointmentStartListener.onAppointmentStart(Appointment.this);
                            Thread.sleep(600000);
                            DataProvider.appointmentEndsListener.onAppointmentEnds(Appointment.this);
                            Appointment.this.started = false;
                        }
                        Appointment.this.setBlocked(false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setName(Appointment.this.appointmentId + "-start");
            Appointment.threads.add(thread);
            thread.start();
        }
    }

    private Boolean checkThreadIsRunning(String threadSuffix){
        if(threads == null){
            threads = new ArrayList<>();
        } else {
            for(Thread thread : threads){
                if(thread.getName().equals(Appointment.this.appointmentId + "-" + threadSuffix)){
                    return true;
                }
            }
        }
        return false;
    }
}
