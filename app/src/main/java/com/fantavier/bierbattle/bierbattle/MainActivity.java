package com.fantavier.bierbattle.bierbattle;

import android.app.Notification;
import android.content.Intent;
import android.support.design.widget.TabLayout;
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
        initDataManagement();
    }

    @Override
    public void onResume(){
        super.onResume();
        //dataProvider.getActiveGroup().checkAppointmentStatus();
    }

    @Override
    public void onDestroy(){
        moveTaskToBack(true);
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

    private void setGroupDataListener(){
        MainActivity.dataProvider.setGroupDataListener(new DataProvider.GroupDataListener() {
            @Override
            public void onGroupeDataChanged() {
                MainActivity.dataProvider.setAppointmentDataListener(new DataProvider.AppointmentDataListener() {
                    @Override
                    public void onAppointmentDataChangedListener() {
                        List<String> appointments = dataProvider.getActiveGroup().getAppointmentTitles();
                        ArrayAdapter<String> appointmentAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, appointments);
                        TermineTab.appointmentList.setAdapter(appointmentAdapter);
                    }
                });

                MainActivity.dataProvider.setMemberDataListener(new DataProvider.MemberDataListener() {
                    @Override
                    public void onMemberDataChangedListener() {
                        List<String> members = dataProvider.getActiveGroup().getMemberTitles();
                        ArrayAdapter<String> groupMemberAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, members);
                        GruppeTab.groupList.setAdapter(groupMemberAdapter);
                    }
                });
            }
        });
    }

    private void setAppointmentListener(){
        MainActivity.dataProvider.setAppointmentStartListener(new DataProvider.AppointmentStartListener() {
            @Override
            public void onAppointmentStart(Appointment appointment) {
                appointment.checkAppointmentStatus();
                appointment.cancelWatcher();
                Notification.Builder notificationBuilder = notificationHelper.getNotification1("Termin gestartet", "Termin "+appointment.getTitle()+ " ist gestartet");
                notificationHelper.notify(102, notificationBuilder);
            }
        });

        MainActivity.dataProvider.setVotingEndsListener(new DataProvider.VotingEndsListener() {
            @Override
            public void onVotingEnds(Appointment appointment) {
                appointment.checkAppointmentStatus();
                appointment.cancelWatcher();
                Notification.Builder notificationBuilder = notificationHelper.getNotification1("Abstimmung beendet", "Abstimmung für "+appointment.getTitle()+ " wurde beendet");
                notificationHelper.notify(101, notificationBuilder);
            }
        });
    }
}



