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

public class Login extends AppCompatActivity {

    private static final String TAG = "Login";

    public FirebaseAuth mAuth;
    private Button loginButton;
    private Button registrierungButton;
    private TextView email;
    private TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);
        registrierungButton = (Button) findViewById(R.id.registrierung_button);
        loginButton = (Button) findViewById(R.id.login_button);

        registrierungButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login.this.onRegistration();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login.this.onLogin();
            }
        });
    }

    public void onLogin(){
        try {
            mAuth.signInWithEmailAndPassword(Login.this.email.getText().toString(), Login.this.password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Login.this, "Anmeldung erfolgreich",
                                        Toast.LENGTH_SHORT).show();
                                Login.this.finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Anmeldung fehlgeschlagen",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        } catch (Exception ex){
            Log.w(TAG, "signInWithEmail:failure", ex);
            Toast.makeText(Login.this, "Ein Fehler ist aufgetreten",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onRegistration(){
        Intent startRegistrierung = new Intent(Login.this, Registrierung.class);
        startActivity(startRegistrierung);
    }
}
