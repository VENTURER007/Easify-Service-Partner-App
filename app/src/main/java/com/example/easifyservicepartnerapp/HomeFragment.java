package com.example.easifyservicepartnerapp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    ToggleButton toggleButton;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private DatabaseReference servicePartnersRef;
    private FirebaseUser currentUser;
    private boolean isLocationUpdated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        servicePartnersRef = FirebaseDatabase.getInstance().getReference("service_partners");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Inflate the layout for this fragment
        servicePartnersRef.child(currentUser.getUid()).child("status").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Boolean status = task.getResult().getValue(Boolean.class);
                if (status != null) {
                    // Set the status of the toggle button
                    toggleButton.setChecked(status);
                }
            } else {
                // Handle the error case
                Toast.makeText(getContext(), "Failed to fetch status", Toast.LENGTH_SHORT).show();
            }
        });

        toggleButton = view.findViewById(R.id.toggleButton);
        // Create the location request for obtaining location updates
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).setMinUpdateIntervalMillis(5000).build(); // 3 seconds
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the status of the service partner in the Firebase Realtime Database
                servicePartnersRef.child(currentUser.getUid()).child("status").setValue(isChecked);

                if (isChecked) {
                    // Toggle button is ON
//                    startLocationUpdates();
                    Toast.makeText(getContext(), "You are active now", Toast.LENGTH_SHORT).show();
                } else {
                    // Toggle button is OFF
//                    stopLocationUpdates();
//                    isLocationUpdated = false;
                    Toast.makeText(getContext(), "You are inactive now", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



}
