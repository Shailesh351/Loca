package com.shell.loca.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shell.loca.R;
import com.shell.loca.activity.FriendLocationActivity;
import com.shell.loca.other.Contact;
import com.shell.loca.other.ContactViewHolder;
import com.shell.loca.other.ContactsAdapter;
import com.shell.loca.other.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

public class InviteFragment extends Fragment {

    public static final String PREF_NAME = "LOCA";
    public static final String IS_LOGIN = "is_logged_in";
    public static final String KEY_MOBILE_NUMBER = "user_mobile_number";
    public static final String KEY_NAME = "user_name";
    int PRIVATE_MODE = 0;

    private SharedPreferences mPref;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    private RecyclerView mRecyclerView;

    private ContactsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mPref = getActivity().getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }else {
            getContactsList();
        }

        mAdapter = new ContactsAdapter(getContext(),getLayoutInflater(null), null);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ContactViewHolder holder = (ContactViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:"));
                intent.putExtra("sms_body", "Hi, this is cool app to share location to friends ");
                intent.putExtra("address",holder.mTextViewMobileNo.getText().toString());
                getActivity().startActivity(intent);
            };
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Contacts permiission Granted", Toast.LENGTH_SHORT).show();
                getActivity().recreate();
            } else {
                Toast.makeText(getActivity(), "Contacts permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getContactsList() {
        final ArrayList<Contact> mContacts = new ArrayList<>();
        mContacts.add(new Contact(mPref.getString(KEY_NAME, null), mPref.getString(KEY_MOBILE_NUMBER, null)));

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
                            if (dataSnapshot.getValue() == null) {
                                //user not exists
                                if (!isContainsContact(mContacts, finalPhoneNumber)) {
                                    mAdapter.addItem(new Contact(name, finalPhoneNumber));
                                    mContacts.add(new Contact(name, finalPhoneNumber));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
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