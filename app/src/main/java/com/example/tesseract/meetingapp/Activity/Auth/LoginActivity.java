package com.example.tesseract.meetingapp.Activity.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tesseract.meetingapp.Activity.Main.MeetingsActivity;
import com.example.tesseract.meetingapp.Models.User;
import com.example.tesseract.meetingapp.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private List<User> userList = new ArrayList<>();
    private boolean phoneNumberHaveNickname = false;
    private PhoneAuthCredential authCredential;
    private String curentNickname;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private ProgressDialog progressDialog;

    private final String TAG = "LoginActivity";

    @BindView(R.id.button_start_verification)
    public Button startButton;
    @BindView(R.id.button_verify_phone)
    public Button verifyButton;
    @BindView(R.id.button_resend)
    public Button resendButton;
    @BindView(R.id.button_set_nickname)
    public Button nicknameButton;
    @BindView(R.id.field_phone_number)
    public EditText phoneNumberField;
    @BindView(R.id.field_verification_code)
    public EditText verificationField;
    @BindView(R.id.field_nickname)
    public EditText nicknameField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initViews();
        initFirebase();
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        startButton.setOnClickListener(this);
        nicknameButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        verifyButton.setVisibility(View.INVISIBLE);
        resendButton.setVisibility(View.INVISIBLE);
        nicknameButton.setVisibility(View.INVISIBLE);
        nicknameField.setVisibility(View.INVISIBLE);
        verificationField.setVisibility(View.INVISIBLE);
    }

    private void initFirebase() {
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                progressDialog.hide();
                if (!connected) {
                    showNoInternetAlertDialog();
                } else {
                    if (alert != null) {
                        alert.hide();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.hide();
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    userList.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                if (phoneNumberHaveNickname) {
                    signInWithPhoneAuthCredential(credential, true);
                } else {
                    nicknameField.setVisibility(View.VISIBLE);
                    nicknameButton.setVisibility(View.VISIBLE);
                    verificationField.setVisibility(View.INVISIBLE);
                    verifyButton.setVisibility(View.INVISIBLE);
                    resendButton.setVisibility(View.INVISIBLE);
                    authCredential = credential;
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    phoneNumberField.setError(getString(R.string.invalid_phone_number));
                    phoneNumberField.setVisibility(View.VISIBLE);
                    verificationField.setVisibility(View.INVISIBLE);
                    verifyButton.setVisibility(View.INVISIBLE);
                    startButton.setVisibility(View.VISIBLE);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    resendButton.setVisibility(View.VISIBLE);
                    Snackbar.make(findViewById(android.R.id.content), R.string.quota_exceeded,
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                for (User u : userList) {
                    if (u.getPhoneNumber().equals(phoneNumberField.getText().toString())) {
                        phoneNumberHaveNickname = true;
                        curentNickname = u.getNickname();
                    }
                }
                verifyButton.setVisibility(View.VISIBLE);
                verificationField.setVisibility(View.VISIBLE);
                nicknameField.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.INVISIBLE);
                phoneNumberField.setVisibility(View.INVISIBLE);

                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    AlertDialog alert;

    private void showNoInternetAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet")
                .setMessage("Please, connect to wi-fi.")
                .setCancelable(false)
                .setNegativeButton("Connect",
                        (dialog, id) -> {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        });
        alert = builder.create();
        alert.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, boolean auth) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                startActivity(new Intent(LoginActivity.this, MeetingsActivity.class));
                if (!auth) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(user.getPhoneNumber())
                            .setValue(new User(user.getPhoneNumber(), nicknameField.getText().toString()));
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(user.getPhoneNumber())
                            .setValue(new User(user.getPhoneNumber(), curentNickname));
                }
                finish();
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    verificationField.setError(getString(R.string.invalid_code));
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        if (phoneNumberHaveNickname) {
            signInWithPhoneAuthCredential(credential, true);
        } else {
            nicknameField.setVisibility(View.VISIBLE);
            nicknameButton.setVisibility(View.VISIBLE);
            verificationField.setVisibility(View.INVISIBLE);
            verifyButton.setVisibility(View.INVISIBLE);
            resendButton.setVisibility(View.INVISIBLE);
            authCredential = credential;
        }

    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60, TimeUnit.SECONDS,   // Unit of timeout and timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = phoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberField.setError(getString(R.string.invalid_phone_number));
            return false;
        }
        return true;
    }

    private boolean validateNickname() {
        String nickname = nicknameField.getText().toString();
        if (nickname.length() < 6) {
            nicknameField.setError("Short nickname.");
            return false;
        } else if (nickname.length() > 20) {
            nicknameField.setError("Long nickname.");
            return false;
        }
        for (User u : userList) {
            if (u.getNickname().equals(nicknameField.getText().toString())) {
                nicknameField.setError("Nickname is already register.");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MeetingsActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(phoneNumberField.getText().toString());
                break;
            case R.id.button_verify_phone:
                String code = verificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    verificationField.setError(getString(R.string.empty_field));
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(phoneNumberField.getText().toString(), mResendToken);
                break;
            case R.id.button_set_nickname:
                if (!validateNickname()) {
                    return;
                }
                signInWithPhoneAuthCredential(authCredential, false);
                break;
        }

    }

}
