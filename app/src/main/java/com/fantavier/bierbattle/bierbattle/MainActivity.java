package com.fantavier.bierbattle.bierbattle;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;
import com.fantavier.bierbattle.bierbattle.helper.NotificationHelper;
import com.fantavier.bierbattle.bierbattle.model.Appointment;
import com.fantavier.bierbattle.bierbattle.model.DataProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DataProvider dataProvider = null;
    public static NotificationHelper notificationHelper = null;
    public static Location location = null;

    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Intent location_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataProvider = new DataProvider();
        notificationHelper = new NotificationHelper(MainActivity.this);
        location = new Location();

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setOffscreenPageLimit(4);

    }

    @Override
    public void onStart(){
        super.onStart();
        if (!requestLocationUpdates()) {
            location_service = new Intent(getApplicationContext(), Location.class);
            startService(location_service);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        initDataManagement();
        requestLocationUpdates();
    }

    @Override
    public void onDestroy(){
        moveTaskToBack(true);
        stopService(location_service);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        if (id == R.id.action_intro) {
            startActivity(new Intent(this, IntroActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            switch(position){
                case 0:
                    MenueTab main = new MenueTab();
                    return main;
                case 1:
                    GruppeTab gruppe = new GruppeTab();
                    return gruppe;
                case 2:
                    TermineTab termine = new TermineTab();
                    return termine;
                case 3:
                    TeilnehmerTab teilnehmer = new TeilnehmerTab();
                    return teilnehmer;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    public void initDataManagement(){
        try {
            setUserDataListener();
            setGroupDataListener();
            setAppointmentListener();
            setRankingDataListener();
            MainActivity.dataProvider.loadData();
        } catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void setUserDataListener(){
        MainActivity.dataProvider.setUserDataListener(new DataProvider.UserDataListener(){
            @Override
            public void onUserDataChanged() {
                MenueTab.username.setText(dataProvider.getActiveUser().getUsername());
            }
        });
    }

    private void setRankingDataListener(){
        MainActivity.dataProvider.setRankingDataListener(new DataProvider.RankingDataListener() {
            @Override
            public void onRankingDataListenerChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> users = dataProvider.getRankingStrings();
                        ArrayAdapter<String> rankingMemberAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, users);
                        TeilnehmerTab.memberList.setAdapter(rankingMemberAdapter);
                    }
                });
            }
        });
    }

    private void setGroupDataListener(){
        MainActivity.dataProvider.setGroupDataListener(new DataProvider.GroupDataListener() {
            @Override
            public void onGroupeDataChanged() {
            MainActivity.dataProvider.setAppointmentDataListener(new DataProvider.AppointmentDataListener() {
                @Override
                public void onAppointmentDataChangedListener() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> appointments = dataProvider.getActiveGroup().getAppointmentTitles();
                        ArrayAdapter<String> appointmentAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, appointments);
                        TermineTab.appointmentList.setAdapter(appointmentAdapter);
                    }
                });
                }
            });

            MainActivity.dataProvider.setMemberDataListener(new DataProvider.MemberDataListener() {
                @Override
                public void onMemberDataChangedListener() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> members = dataProvider.getActiveGroup().getMemberTitles();
                        ArrayAdapter<String> groupMemberAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, members);
                        GruppeTab.groupList.setAdapter(groupMemberAdapter);
                        try {
                            MenueTab.rank.setText(MainActivity.dataProvider.getUserrank());
                        } catch (NullPointerException e){
                            Log.w(TAG, e.getMessage());
                        } catch (ExceptionHelper.MemberNotFoundException e) {
                            Log.w(TAG, e.getMessage());
                            Toast.makeText(MainActivity.this, "Platzierung konnte nicht ermittelt werden.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                }
            });
            }
        });
    }

    private void setAppointmentListener(){
        MainActivity.dataProvider.setAppointmentCreatedListener(new DataProvider.AppointmentCreatedListener() {
            @Override
            public void onAppointmentCreatedListener() {
                NotificationCompat.Builder notificationBuilder = notificationHelper.getNotification1("Termin erstellt", "Ein Termin wurde erstellt");
                notificationHelper.notify(105, notificationBuilder);
            }
        });
        MainActivity.dataProvider.setAppointmentStartListener(new DataProvider.AppointmentStartListener() {
            @Override
            public void onAppointmentStart(Appointment appointment) {
                NotificationCompat.Builder notificationBuilder = notificationHelper.getNotification1("Termin gestartet", "Termin "+appointment.getTitle()+ " ist gestartet");
                notificationHelper.notify(104, notificationBuilder);
                appointment.checkAppointmentStatus();
            }
        });

        MainActivity.dataProvider.setAppointmentEndsListener(new DataProvider.AppointmentEndsListener() {
            @Override
            public void onAppointmentEnds(Appointment appointment) {
                NotificationCompat.Builder notificationBuilder = notificationHelper.getNotification1("Termin beendet", "Termin "+appointment.getTitle()+ " ist beendet");
                notificationHelper.notify(103, notificationBuilder);
                appointment.checkAppointmentStatus();
            }
        });

        MainActivity.dataProvider.setVotingEndsListener(new DataProvider.VotingEndsListener() {
            @Override
            public void onVotingEnds(Appointment appointment) {
                NotificationCompat.Builder notificationBuilder = notificationHelper.getNotification1("Abstimmung beendet", "Abstimmung für "+appointment.getTitle()+ " wurde beendet");
                notificationHelper.notify(101, notificationBuilder);
                appointment.checkAppointmentStatus();
            }
        });
    }

    private boolean requestLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET}, 1);
                return true;
            }
        }
        return false;
    }



    public void onRequestPermissionResults(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates();
                }
                break;
        }
    }

}



