package com.cargocrew.cargoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cargocrew.cargoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by RENT on 2017-05-30.
 */


public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    public FirebaseAuth getAuth() {
        return auth;
    }
    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    @BindView(R.id.sign_in_button)
    Button btnSignIn;

    @BindView(R.id.sign_up_button)
    Button btnSignUp;

    @BindView(R.id.email)
    EditText inputEmail;

    @BindView(R.id.password)
    EditText inputPassword;

    @BindView(R.id.btn_reset_password)
    Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
    }

    @OnClick (R.id.btn_reset_password)
    public void btnResetPasswordClick() {
        startActivity(new Intent(RegistrationActivity.this, ResetPasswordActivity.class));
    }

    @OnClick(R.id.sign_in_button)
    public void signInButtonClick() {
        finish();
    }

    @OnClick(R.id.sign_up_button)
    public void signUpButtonClick() {

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter inputEmail address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegistrationActivity.this, "RegistrationActivity succeed!" , Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            startActivity(new Intent(RegistrationActivity.this, MapsActivity.class));
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}





