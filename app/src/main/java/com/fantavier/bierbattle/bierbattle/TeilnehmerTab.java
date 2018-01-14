package com.fantavier.bierbattle.bierbattle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Mir on 14.12.2017.
 */

public class TeilnehmerTab extends Fragment {

    private static final String TAG = "TeilnehmerTab";
    public static ListView memberList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teilnehmer_tab, container, false);
        memberList = (ListView) rootView.findViewById(R.id.MemberList);
        return rootView;
    }
}
