package com.fantavier.bierbattle.bierbattle.model;

import android.provider.ContactsContract;

import com.fantavier.bierbattle.bierbattle.helper.DateHelper;
import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Appointment implements DataProvider.DatabaseReferenceObject {

    private DatabaseReference dbRef = null;
    private HashMap<String, Boolean> votings = null;
    private Group parentRef = null;
    private Long createtime = 0l;
    private String appointmentId = "";
    private String title = "";
    private String date = "";
    private String time = "";
    private String location = "";
    private Boolean votingend = false;
    private Boolean weekly = false;
    private Boolean active = false;

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
    public HashMap<String, Boolean> getVotings(){ return this.votings; }

    public void setVoting(String uid, Boolean vote){
        dbRef.child("votings").child(uid).setValue(vote);
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
                DataProvider.appointmentListener.onAppointmentDataChangedListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
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

    public Long getVotingtimeLeftInMilli(){
        Long plusTime = this.getCreatetime() + 600000;
        return plusTime - System.currentTimeMillis();
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

    private HashMap<String, Boolean> initVotings(DataSnapshot votingsDS){
        HashMap<String, Boolean> votings = new HashMap<>();

        for(DataSnapshot voting : votingsDS.getChildren()){
            boolean value = (boolean) voting.getValue();
            votings.put(voting.getKey().toString(), value);
        }

        return votings;

    }

}
