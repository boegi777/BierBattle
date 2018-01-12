package com.fantavier.bierbattle.bierbattle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantavier.bierbattle.bierbattle.model.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registrierung extends AppCompatActivity {

    private static final String TAG = "Registrierung";

    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;

    private Button registrierungButton;
    private TextView email;
    private TextView username;
    private TextView password;
    private TextView password_repeat;

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader = null;
    private HashMap<String, List<String>> listHash = null;
    private List<String> categorys = null;
    private Integer categoryId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrierung);

        mAuth = FirebaseAuth.getInstance();
        mDbRef = FirebaseDatabase.getInstance().getReference("users");
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        initCategorys();

        email = (TextView) findViewById(R.id.email);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        password_repeat = (TextView) findViewById(R.id.password_repeat);
        registrierungButton = (Button) findViewById(R.id.registrierung_button);

        listView = (ExpandableListView) findViewById(R.id.category);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                for(int j=1; j < listView.getAdapter().getCount(); j++){
                    CheckBox item = (CheckBox) expandableListView.getChildAt(j).findViewById(R.id.ListItem);
                    if(j == i1+1){
                        item.setChecked(true);
                        categoryId = j;
                    } else {
                        item.setChecked(false);
                    }
                }
                return true;
            }
        });

        registrierungButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registrierung.this.onRegistration();
            }
        });
    }

    private void initCategorys(){
        listDataHeader.add("Studiengang");
        DatabaseReference catRef = FirebaseDatabase.getInstance().getReference("categorys");
        catRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categorys = (List<String>) dataSnapshot.getValue();

                categorys.remove(0);

                listHash.put(listDataHeader.get(0), categorys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    public void onRegistration(){
        if(password.getText().toString().equals(password_repeat.getText().toString())) {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Map<String, String> userData = new HashMap<String, String>();
                                userData.put("uid", mAuth.getCurrentUser().getUid());
                                userData.put("username", Registrierung.this.getUsernameString());

                                userData.put("category", categoryId.toString());

                                UserProvider.createUser(userData);
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(Registrierung.this, "Registration completed",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Registrierung.this, "Registration failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(Registrierung.this, "Different Passwords.",
                    Toast.LENGTH_SHORT).show();
            password.setText("");
            password_repeat.setText("");
        }
    }

    public String getUsernameString(){
        return username.getText().toString();
    }
}
