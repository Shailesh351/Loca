package com.shell.loca;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_NAME = "LOCA";
    public static final String IS_LOGIN = "is_logged_in";
    public static final String KEY_MOBILE_NUMBER = "user_mobile_number";
    int PRIVATE_MODE = 0;

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    private RecyclerView mRecyclerView;

    private TextView mTextView;
    private ArrayList<Contact> mContactsList;
    private ContactsAdapter mAdapter;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mPref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();

        mTextView = (TextView) findViewById(R.id.text_view);

        mContactsList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        String isLoggedIn = mPref.getString(IS_LOGIN, null);

        if (isLoggedIn == null) {
            startActivity(new Intent(this, log_in_activity.class));
            finish();
        } else {
            mTextView.setText(mPref.getString(KEY_MOBILE_NUMBER, null));
        }

        mContactsList = getContacts();

        mAdapter = new ContactsAdapter(mContactsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private ArrayList<Contact> getContacts() {
        final ArrayList<Contact> mContacts = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor contacts = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        // use the cursor to access the contacts
        while (contacts.moveToNext()) {

            //get display name
            final String name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            //get mobile number
            String mobileNo = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            mobileNo = mobileNo.replace(" ", "");
            mobileNo = mobileNo.replace("-", "");
            mobileNo = mobileNo.replace("+91", "");

            if (mobileNo.length() > 10 && mobileNo.charAt(0) == '0') {
                mobileNo = mobileNo.substring(1);
            }

            final String finalPhoneNumber = mobileNo;
            mDatabaseReference.child("users").child(mobileNo)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                //user exists
                                if (!isContainsContact(mContacts,finalPhoneNumber)) {
                                    mContacts.add(new Contact(name, finalPhoneNumber));
                                    mAdapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
        return mContacts;
    }

    public boolean isContainsContact(List<Contact> list, String mobileNo) {
        for (Contact object : list) {
            if (object.getMobileNo().equals(mobileNo)) {
                return true;
            }
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_log_out:
                mEditor.clear();
                mEditor.commit();
                startActivity(new Intent(MainActivity.this, log_in_activity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
