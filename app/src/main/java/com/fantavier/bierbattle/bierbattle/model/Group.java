package com.fantavier.bierbattle.bierbattle.model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class Group implements GroupProvider.DatabaseReferenceObject{
    private static final String TAG = "Group";
    private DatabaseReference dbRef;
    private String groupId;
    private String category;
    private String starttime;
    private String endtime;
    private boolean active;
    private List<Member> members = null;
    private List<Appointment> appointments = null;

    public Group(String groupId) {
        try {
            this.initObjectProperties(groupId);
        } catch(Exception e){
            throw e;
        }
    }

    public String getGroupId(){ return this.groupId; }

    public void setActive(boolean active){
        dbRef.child("active").setValue(active); }

    public void setCategory(String category){
        dbRef.child("category").setValue(category);
    }

    public void setStarttime(String starttime){
        dbRef.child("starttime").setValue(starttime);
    }

    public void setEndtime(String endtime){
        dbRef.child("endtime").setValue(endtime);
    }

    public void setMembers(List<Member> members){
        dbRef.child("members").setValue(members);
    }

    public void setAppointments(List<Appointment> appointments){
        dbRef.child("appointments").setValue(appointments);
    }

    public Appointment getAppointment(String index){
        return this.appointments.get(Integer.parseInt(index));
    }

    public ArrayList<String> getMemberTitles(){
        ArrayList<String> memberStrings = new ArrayList<String>();

        Collections.sort(members);
        for(Member member : this.members){
            memberStrings.add(member.toString());
        }
        return memberStrings;
    }


    public ArrayList<String> getAppointmentTitles(){
        ArrayList<String> appointmentStrings = new ArrayList<String>();
        try {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        groupId = Group.this.getGroupId();
                        URL url = new URL("https://us-central1-bierbattle.cloudfunctions.net/checkAppointments");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");
                        //urlConnection.setRequestProperty("Content-Type", "application/json");

                        BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                        httpRequestBodyWriter.write("groupId="+groupId);
                        httpRequestBodyWriter.close();

                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        //JSONObject timeObj = new JSONObject(in.readLine());

                    } catch(IOException ex){
                        Log.d(TAG, ex.getMessage());
                    }
                }
            });
            thread.start();

            for (Appointment appointment : this.appointments) {
                if(appointment.getActive() || !appointment.getVotingend())
                    appointmentStrings.add(appointment.toString());
            }

        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return appointmentStrings;
    }

    public boolean createAppointment(
            String title,
            String date,
            String time,
            String location,
            Boolean weekly
    ){
        try {
            HashMap<String, String> appointment = new HashMap<>();

            if(title.isEmpty() || date.isEmpty() || time.isEmpty()){
                throw new ExceptionInInitializerError("missing values");
            }
            Long currentTime = new Date().getTime();

            appointment.put("title", title);
            appointment.put("date", date);
            appointment.put("time", time);
            appointment.put("location", location);
            appointment.put("votingend", "false");
            appointment.put("weekly", weekly.toString());
            appointment.put("createtime", currentTime.toString());

            return dbRef.child("appointments").push().setValue(appointment).isSuccessful();
        } catch(Exception  e){
            throw e;
        }
    }

    @Override
    public DatabaseReference getDbRef() {
        return FirebaseDatabase.getInstance().getReference("groups").child(groupId);
    }

    @Override
    public void initObjectProperties(String id) {
        this.groupId = id;
        this.dbRef = getDbRef();
        this.dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot groupDS) {
                for (DataSnapshot groupData : groupDS.getChildren()) {
                    switch (groupData.getKey()) {
                        case "active":
                            Group.this.active = Boolean.parseBoolean(groupData.getValue().toString());
                            break;
                        case "category":
                            Group.this.category = groupData.getValue().toString();
                            break;
                        case "starttime":
                            Group.this.starttime = groupData.getValue().toString();
                            break;
                        case "endtime":
                            Group.this.endtime = groupData.getValue().toString();
                            break;
                        case "members":
                            Group.this.members = getMembers(groupData);
                            break;
                        case "appointments":
                            Group.this.appointments = getAppointments(groupData);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private List<Appointment> getAppointments(DataSnapshot appointmentsDS){
        List<Appointment> appointments = new ArrayList<Appointment>();
        for(DataSnapshot appointmentDS : appointmentsDS.getChildren()){
            appointments.add(new Appointment(appointmentDS.getKey(), this));
        }
        return appointments;
    }

    private List<Member> getMembers(DataSnapshot membersDS){
        List<Member> members = new ArrayList<Member>();
        for(DataSnapshot memberDS : membersDS.getChildren()){
            members.add(new Member(memberDS.getKey(), this));
        }
        return members;
    }
}
