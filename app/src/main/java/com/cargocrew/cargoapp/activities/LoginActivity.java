package com.cargocrew.cargoapp.activities;

/**
 * Created by RENT on 2017-05-30.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.widget.Toast;

import com.cargocrew.cargoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {



    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private Button btnSignup, btnLogin, btnReset;
    private ProgressBar progressBarLogin;
    private LinearLayout greyLayout;


    private ImageView imgToAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);




        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        progressBarLogin = (ProgressBar) findViewById(R.id.progressBarLogin);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Registration.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError("Your password is too short!");
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    progressBarLogin.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }
                        });
            }
        });
    }
}