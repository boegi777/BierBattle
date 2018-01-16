package com.fantavier.bierbattle.bierbattle.ui;

import android.view.View;
import android.widget.Toast;

import com.fantavier.bierbattle.bierbattle.Login;
import com.fantavier.bierbattle.bierbattle.MainActivity;

/**
 * Created by paul on 16.01.18.
 */

public class PopupListener implements View.OnClickListener {
    Integer index = null;

    public PopupListener(Integer i){
        index = i;
    }
    @Override
    public void onClick(View view) {
        MainActivity.dataProvider.setPayedDebtsForUser(index);
        Toast.makeText(view.getContext(), "Schuld wurde beglichen!",
                Toast.LENGTH_SHORT).show();
    }
}
