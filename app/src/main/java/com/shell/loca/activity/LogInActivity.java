package com.shell.loca.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shell.loca.R;

public class LogInActivity extends AppCompatActivity {

    int PRIVATE_MODE = 0;
    public static final String PREF_NAME = "LOCA";
    public static final String IS_LOGIN = "is_logged_in";
    public static final String KEY_MOBILE_NUMBER = "user_mobile_number";
    public static final String KEY_NAME = "user_name";

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    private TextInputEditText mMobileNoEditText,mNameEditText;
    private Button mSignUpButton,mSignInButton;
    private View focusView = null;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

        mPref = getApplicationContext().getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        mEditor = mPref.edit();

        mMobileNoEditText = (TextInputEditText) findViewById(R.id.edit_text_mobile_number);
        mNameEditText = (TextInputEditText) findViewById(R.id.edit_text_name);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mProgressDialog = new ProgressDialog(LogInActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setTitle("Signing Up...");
                mProgressDialog.show();

                final String name = mNameEditText.getText().toString().trim();
                final String mobileNo = mMobileNoEditText.getText().toString().trim();

                if(validateDetails(name,mobileNo)){

                    mDatabaseReference.child("users").child(mobileNo)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() != null){
                                        //user exists
                                        mMobileNoEditText.setError("User with this Mobile No already exists");
                                        focusView = mMobileNoEditText;
                                        focusView.requestFocus();
                                    }else{
                                        //user not exists
                                        mDatabaseReference.child("users").child(mobileNo).child("name").setValue(name);
                                        mEditor.clear();
                                        mEditor.putString(KEY_MOBILE_NUMBER, mobileNo);
                                        mEditor.putString(KEY_NAME,name);
                                        mEditor.putString(IS_LOGIN, "true");
                                        mEditor.commit();
                                        Toast.makeText(LogInActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LogInActivity.this,MainActivity.class));
                                        finish();
                                    }
                                    if(mProgressDialog != null)
                                        mProgressDialog.dismiss();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setTitle("Signing In...");
                mProgressDialog.show();

                final String name = mNameEditText.getText().toString().trim();
                final String mobileNo = mMobileNoEditText.getText().toString().trim();

                if(validateDetails(name,mobileNo)){

                    mDatabaseReference.child("users").child(mobileNo)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() == null){
                                        //user not exists
                                        mMobileNoEditText.setError("You are not user, Sign up first");
                                        focusView = mMobileNoEditText;
                                        focusView.requestFocus();
                                    }else{
                                        //user exists
                                        mDatabaseReference.child("users").child(mobileNo).child("name").setValue(name);
                                        mEditor.clear();
                                        mEditor.putString(KEY_MOBILE_NUMBER, mobileNo);
                                        mEditor.putString(KEY_NAME,name);
                                        mEditor.putString(IS_LOGIN, "true");
                                        mEditor.commit();
                                        Toast.makeText(LogInActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LogInActivity.this,MainActivity.class));
                                        finish();
                                    }
                                    if(mProgressDialog != null)
                                        mProgressDialog.dismiss();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
            }
        });
    }

    private boolean validateDetails(String name, String mobileNo) {

        boolean validateData = true;
        // Reset errors.
        mNameEditText.setError(null);
        mMobileNoEditText.setError(null);

        // Check for a valid mMobileNoEditText, if the user entered one.
        if (TextUtils.isEmpty(name)) {
            mNameEditText.setError("This field is required");
            validateData = false;
            focusView = mNameEditText;
        }
        // Check for a valid Admission no, if the user entered one.
        if (TextUtils.isEmpty(mobileNo)) {
            mMobileNoEditText.setError("This field is required");
            validateData = false;
            focusView = mMobileNoEditText;
        }else if (!isMobileNoValid(mobileNo)) {
            mMobileNoEditText.setError("Mobile number not valid");
            validateData = false;
            focusView = mMobileNoEditText;
        }
        if (validateData==false) {
            focusView.requestFocus();
        } else {
        }

        if(mProgressDialog.isShowing() && validateData != true){
            if(mProgressDialog != null)
                mProgressDialog.dismiss();
        }

        return validateData;
    }

    private boolean isMobileNoValid(String mobileNo) {
        return (mobileNo.length() == 10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}
