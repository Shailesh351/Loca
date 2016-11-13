package com.shell.loca.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private EditText mMobileNoEditText,mNameEditText;
    private Button mSignUpButton,mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

        mPref = getApplicationContext().getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        mEditor = mPref.edit();

        mMobileNoEditText = (EditText) findViewById(R.id.edit_text_mobile_number);
        mNameEditText = (EditText) findViewById(R.id.edit_text_name);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mNameEditText.getText().toString().trim();
                final String mobileNo = mMobileNoEditText.getText().toString().trim();

                if(mobileNo.length() > 0 & mobileNo.length()== 10 && name.length() > 0){

                    mDatabaseReference.child("users").child(mobileNo)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() != null){
                                        //user exists
                                        Toast.makeText(LogInActivity.this, "user already exists", Toast.LENGTH_SHORT).show();
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
                final String name = mNameEditText.getText().toString().trim();
                final String mobileNo = mMobileNoEditText.getText().toString().trim();

                if(mobileNo.length() > 0 & mobileNo.length()== 10 && name.length() > 0){

                    mDatabaseReference.child("users").child(mobileNo)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() == null){
                                        //user not exists
                                        Toast.makeText(LogInActivity.this, "Sign up first", Toast.LENGTH_SHORT).show();
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
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
            }
        });
    }


}
