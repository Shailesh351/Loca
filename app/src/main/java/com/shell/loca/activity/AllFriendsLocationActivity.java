package com.shell.loca.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shell.loca.R;
import com.shell.loca.other.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllFriendsLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    String name, lat, lng, time;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    private SupportMapFragment fragment;
    private GoogleMap map;
    private float mapZoomLevel;

    private ArrayList<Contact> mContacts;
    private ArrayList<Marker> mMarkers;
    private Map<String, Marker> mMarkersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends_location);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mContacts = (ArrayList<Contact>) getIntent().getSerializableExtra("contacts_list");
        mMarkers = new ArrayList<>();
        mMarkersMap = new HashMap<>();

        Toast.makeText(this, mContacts.toString(), Toast.LENGTH_LONG);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        FragmentManager fm = getSupportFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
            fragment.getMapAsync(this);
        }

        mapZoomLevel = 13;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void requestLocationData() {
        for (Contact contact : mContacts) {
            final String name = contact.getName();
            Log.d("contacts", name);
            mDatabaseReference.child("users").child(contact.getMobileNo()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals("latitude")) {
                            lat = snapshot.getValue().toString();
                        } else if (snapshot.getKey().equals("longitude")) {
                            lng = snapshot.getValue().toString();
                        } else if (snapshot.getKey().equals("last_location_update_time")) {
                            time = snapshot.getValue().toString();
                        }
                    }
                    Log.d("sended", name + "  " + lat + "  " + lng + "  " + time);
                    showLocationOnMap(name, lat, lng, time);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void showLocationOnMap(String name, String lat, String lng, String time) {
        if (map != null && lat != null && lng != null && time != null) {

            LatLng latlng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, mapZoomLevel));

            if (mMarkersMap.get(name) != null) {
                Marker m = mMarkersMap.get(name);
                m.remove();
                mMarkersMap.remove(name);
            }

            Marker marker = map.addMarker(new MarkerOptions()
                    .title(name)
                    .snippet(lat + ", " + lng)
                    .position(latlng));
            mMarkers.add(marker);
            mMarkersMap.put(name, marker);

            Log.d("map", name);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mapZoomLevel = map.getCameraPosition().zoom;
            }
        });

        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);

        requestLocationData();
    }
}
