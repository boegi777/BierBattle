package com.fantavier.bierbattle.bierbattle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mir on 14.12.2017.
 */

public class GruppeTab extends Fragment {

    private static final String TAG = "GruppeTab";
    private GroupProvider.Group activeGroup;

    public ListView groupList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gruppe_tab, container, false);
        groupList = (ListView) rootView.findViewById(R.id.GroupList);
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        getShit();
    }
    @Override
    public void onResume(){
        super.onResume();
        getShit();
    }

    private void getShit(){
        if(MainActivity.userProvider == null){
            MainActivity.userProvider = new UserProvider();
        }
        MainActivity.userProvider.setActiveGroupListener(new UserProvider.ActiveGroupListener() {
            @Override
            public void onActiveGroupChanged(String groupId) {
                Log.d(TAG, groupId);
                MainActivity.activeGroupId = groupId;
                if(MainActivity.groupProvider == null){
                    MainActivity.groupProvider = new GroupProvider(MainActivity.activeGroupId);
                }
                MainActivity.groupProvider.setGroupDataListener(new GroupProvider.GroupDataListener() {
                    @Override
                    public void onGroupeDataChanged(GroupProvider.Group group) {
                        activeGroup = group;
                        MainActivity.groupProvider.setMemberNameListener(new GroupProvider.MemberNameListener() {
                            @Override
                            public void onMemberNameListener() {
                                List<String> members = activeGroup.getMemberStrings();
                                ArrayAdapter<String> groupMemberAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, members);

                                GruppeTab.this.groupList.setAdapter(groupMemberAdapter);
                            }
                        });
                    }
                });
            }
        });
    }
}
