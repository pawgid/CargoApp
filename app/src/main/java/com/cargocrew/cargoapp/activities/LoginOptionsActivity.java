package com.cargocrew.cargoapp.activities;

/**
 * Created by RENT on 2017-05-30.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cargocrew.cargoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginOptionsActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @BindView(R.id.change_email_button)
    Button btnChangeEmail;

    @BindView(R.id.change_password_button)
    Button btnChangePassword;

    @BindView(R.id.sending_pass_reset_button)
    Button btnSendResetEmail;

    @BindView(R.id.remove_user_button)
    Button btnRemoveUser;

    @BindView(R.id.changeEmail)
    Button changeEmail;

    @BindView(R.id.changePass)
    Button changePassword;

    @BindView(R.id.send)
    Button sendEmail;

    @BindView(R.id.remove)
    Button remove;

    @BindView(R.id.sign_out)
    Button signOut;

    @BindView(R.id.old_email)
    EditText oldEmail;

    @BindView(R.id.new_email)
    EditText newEmail;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.newPassword)
    EditText newPassword;

    @OnClick(R.id.change_email_button)
    public void changeEmailButtonClick() {
        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.VISIBLE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.VISIBLE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);
    }

    @OnClick(R.id.changeEmail)
    public void changeEmailClick() {
        if (user != null && !newEmail.getText().toString().trim().equals("")) {
            user.updateEmail(newEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginOptionsActivity.this, "Email address is updated. Please sign in with new inputEmail id!", Toast.LENGTH_LONG).show();
                                signOut();
                            } else {
                                Toast.makeText(LoginOptionsActivity.this, "Failed to update inputEmail!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else if (newEmail.getText().toString().trim().equals("")) {
            newEmail.setError("Enter inputEmail");
        }
    }

    @OnClick(R.id.change_password_button)
    public void changePasswordButtonClick() {
        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.VISIBLE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.VISIBLE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);
    }

    @OnClick(R.id.changePass)
    public void changePassClick() {
        if (user != null && !newPassword.getText().toString().trim().equals("")) {
            if (newPassword.getText().toString().trim().length() < 6) {
                newPassword.setError("Password too short, enter minimum 6 characters");
            } else {
                user.updatePassword(newPassword.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginOptionsActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                    signOut();
                                } else {
                                    Toast.makeText(LoginOptionsActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else if (newPassword.getText().toString().trim().equals("")) {
            newPassword.setError("Enter password");
        }
    }

    @OnClick(R.id.sending_pass_reset_button)
    public void sendingPassResetButtonClick() {
        oldEmail.setVisibility(View.VISIBLE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.VISIBLE);
        remove.setVisibility(View.GONE);
    }

    @OnClick(R.id.send)
    public void sendClick() {
        if (!oldEmail.getText().toString().trim().equals("")) {
            auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginOptionsActivity.this, "Reset password inputEmail is sent!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginOptionsActivity.this, "Failed to send reset inputEmail!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            oldEmail.setError("Enter inputEmail");
        }
    }

    @OnClick(R.id.remove_user_button)
    public void removeUserButtonClick() {
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginOptionsActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginOptionsActivity.this, RegistrationActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginOptionsActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_options);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(LoginOptionsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public FirebaseAuth.AuthStateListener getAuthListener() {
        return authListener;
    }

    public void setAuthListener(FirebaseAuth.AuthStateListener authListener) {
        this.authListener = authListener;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }
}
