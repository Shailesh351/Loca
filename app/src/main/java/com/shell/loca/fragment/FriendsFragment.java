package com.shell.loca.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shell.loca.R;
import com.shell.loca.activity.LogInActivity;
import com.shell.loca.activity.MainActivity;
import com.shell.loca.other.Contact;
import com.shell.loca.other.ContactsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FriendsFragment extends Fragment {

    public static final String PREF_NAME = "LOCA";
    public static final String IS_LOGIN = "is_logged_in";
    public static final String KEY_MOBILE_NUMBER = "user_mobile_number";
    int PRIVATE_MODE = 0;

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    private RecyclerView mRecyclerView;

    private TextView mTextView;
    private ArrayList<Contact> mContactsList;
    private ContactsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mPref = getActivity().getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();

        mContactsList = new ArrayList<>();

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);

        mContactsList = getContacts();

        mAdapter = new ContactsAdapter(mContactsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<Contact> getContacts() {
        final ArrayList<Contact> mContacts = new ArrayList<>();

        ContentResolver cr = getActivity().getContentResolver();
        Cursor contacts = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        // use the cursor to access the contacts
        while (contacts.moveToNext()) {

            //get display name
            final String name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            //get mobile number
            String mobileNo = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            // trim mobile number
            mobileNo = mobileNo.replace(" ", "");
            mobileNo = mobileNo.replace("-", "");
            mobileNo = mobileNo.replace("+91", "");

            if (mobileNo.length() > 10 && mobileNo.charAt(0) == '0') {
                mobileNo = mobileNo.substring(1);
            }

            final String finalPhoneNumber = mobileNo;

            //check if user exist or not
            mDatabaseReference.child("users").child(mobileNo)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                //user exists
                                if (!isContainsContact(mContacts,finalPhoneNumber)) {
                                    mContacts.add(new Contact(name, finalPhoneNumber));
                                    mAdapter.notifyDataSetChanged();
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
}
