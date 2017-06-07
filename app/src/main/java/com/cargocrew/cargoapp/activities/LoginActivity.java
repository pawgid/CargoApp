package com.cargocrew.cargoapp.activities;

/**
 * Created by RENT on 2017-05-30.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cargocrew.cargoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @BindView(R.id.email)
    EditText inputEmail;

    @BindView(R.id.password)
    EditText inputPassword;

    @BindView(R.id.btn_signup)
    Button btnSignup;

    @BindView(R.id.btn_login)
    Button btnLogin;

    @BindView(R.id.btn_reset_password)
    Button btnReset;

    @BindView(R.id.progressBarLogin)
    LinearLayout progressBarLogin;

    @OnClick(R.id.btn_signup)
    public void btn_signupClick() {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    @OnClick(R.id.btn_reset_password)
    public void btn_reset_passwordClick() {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    @OnClick(R.id.btn_login)
    public void btn_loginClick() {
        String email = this.inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter inputEmail address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }
}