package com.fantavier.bierbattle.bierbattle.model;

import android.provider.ContactsContract;
import android.util.Log;

import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataProvider {

    public static UserDataListener userListener = null;
    public static GroupDataListener groupListener = null;
    public static MemberDataListener memberDataListener = null;
    public static AppointmentDataListener appointmentListener = null;
    public static AppointmentStartListener appointmentStartListener = null;

    private static final String TAG = "DataProvider";
    private static String groupId = "";
    private static DatabaseReference mDbRef = null;
    private static Group group = null;
    private static User user = null;
    private static Boolean appointmentWatcherActive = true;

    public DataProvider(){
        DataProvider.group = new Group();
        DataProvider.user = new User();
    }

    public interface DatabaseReferenceObject{
        DatabaseReference getDbRef();
        void loadObjectProperties(String id);
    }

    public interface UserDataListener {
        void onUserDataChanged();
    }

    public interface GroupDataListener {
        void onGroupeDataChanged();
    }

    public interface MemberDataListener {
        void onMemberDataChangedListener();
    }

    public interface AppointmentDataListener {
        void onAppointmentDataChangedListener();
    }

    public interface AppointmentStartListener{
        void onAppointmentStart(Appointment appointment);
    }

    public void setUserDataListener(UserDataListener listener) { userListener = listener; }
    public void setGroupDataListener(GroupDataListener listener){ groupListener = listener; }
    public void setMemberDataListener(MemberDataListener listener) { memberDataListener = listener; }
    public void setAppointmentDataListener(AppointmentDataListener listener) { appointmentListener = listener; }
    public void setAppointmentStartListener(AppointmentStartListener listener) { appointmentStartListener = listener; }

    public static void createUser(Map<String, String> userData) {
        try {
            /* Daten überprüfen */
            mDbRef = FirebaseDatabase.getInstance().getReference("users").child(userData.get("uid"));
            userData.remove("uid");
            mDbRef.setValue(userData);
        } catch(Exception ex){
            throw ex;
        }
    }

    public Group getActiveGroup(){
        return group;
    }

    public User getActiveUser(){
        return user;
    }

    public void setAppointmentWatcherActive(Boolean active){
        appointmentWatcherActive = active;
    }

    public void loadData() {
        loadUserData();
        setActiveGroupId();
        watchActiveGroupAppointments();
    }

    private void watchActiveGroupAppointments(){
        for(final Appointment appointment : group.getAppointments()){
            if(!appointment.getVotingend()){
                final Long timeLeft = appointment.getVotingtimeLeftInMilli();
                try {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(timeLeft);
                                Log.d(TAG, "votingend!");
                                DataProvider.appointmentStartListener.onAppointmentStart(appointment);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                } catch(Exception ex){
                    Log.d(TAG, ex.getMessage());
                }
            }
        }

        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Long appointmentTime = 0l;
                    Long plusHour = 0l;
                    Long currentTime = 0l;
                    while(appointmentWatcherActive) {
                        currentTime = Calendar.getInstance().getTimeInMillis();
                        plusHour = appointmentTime + 3600000;
                        if(group.getAppointments() != null) {
                            for (Appointment appointment : group.getAppointments()) {
                                appointmentTime = appointment.getDateInMilliSec();
                                if(appointment.getVotingend() && appointment.getActive()){
                                    if (currentTime  >= appointmentTime && currentTime < plusHour) {
                                        DataProvider.appointmentStartListener.onAppointmentStart(appointment);
                                    }
                                } else if(!appointment.getVotingend()) {

                                }
                            }
                        }
                        Thread.sleep(10000);
                    }
                } catch (Exception ex) {
                    Log.d(TAG, ex.getMessage());
                }
            }
        });

        thread.start();*/
    }

    private void loadUserData(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user.loadObjectProperties(uid);
    }

    private void loadGroupData(){
        DataProvider.group.loadObjectProperties(groupId);
    }

    private void setActiveGroupId(){
        user.getDbRef().child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> groups = (HashMap<String, Boolean>) dataSnapshot.getValue();
                for (Map.Entry<String, Boolean> entry : groups.entrySet()) {
                    if (entry.getValue() == true) {
                        DataProvider.groupId = entry.getKey().toString();
                        loadGroupData();
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
}