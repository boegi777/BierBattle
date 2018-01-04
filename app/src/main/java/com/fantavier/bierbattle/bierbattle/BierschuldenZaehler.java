package com.fantavier.bierbattle.bierbattle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by Mir on 14.12.2017.
 */

public class BierschuldenZaehler extends AppCompatActivity {
    ListView listView ;
    ListView min;
    Button button_zurueck;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bierschulden);

        button_zurueck = (Button) findViewById(R.id.zurueck);
        listView = (ListView) findViewById(R.id.plusview);
        min = (ListView) findViewById(R.id.minusview);

        String[] plus = new String[] { "Paul   3", "Juri   1", "Peter   2"};
        String[] minus = new String[] { "Ursula   1", "Anja   5", "Frank   1", "Ulf   2"};
/*
        public void showSimplePopUp(){

            AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
            helpBuilder.setTitle("Pop Up");
            helpBuilder.setMessage("This is a Simple Pop Up");
            helpBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) { }
                    });

            // Remember, create doesn't show the dialog
            AlertDialog helpDialog = helpBuilder.create();
            helpDialog.show();
        }
*/

        button_zurueck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

/*
        min.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick (AdapterView <?> adapter, View item,int pos, long id){
                showSimplePopUp();
                return true;
            }
    });
*/



    }
}
