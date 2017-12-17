package com.fantavier.bierbattle.bierbattle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrierung);

        mAuth = FirebaseAuth.getInstance();
        mDbRef = FirebaseDatabase.getInstance().getReference("users");

        email = (TextView) findViewById(R.id.email);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        password_repeat = (TextView) findViewById(R.id.password_repeat);
        registrierungButton = (Button) findViewById(R.id.registrierung_button);

        registrierungButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registrierung.this.onRegistration();
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
