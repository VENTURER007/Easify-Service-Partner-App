package com.example.easifyservicepartnerapp;

import android.Manifest;
import android.content.IntentSender;
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

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 2;

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
                    setupMap();
                    Toast.makeText(getContext(), "You are active now", Toast.LENGTH_SHORT).show();
                } else {
                    // Toggle button is OFF
//                    stopLocationUpdates();
                    isLocationUpdated = false;
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

    private void setupMap() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location lastLocation = locationResult.getLastLocation();
                    LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                }
            }
        };


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationRequest = createLocationRequest();

        Task<LocationSettingsResponse> locationSettingsResponseTask = checkLocationSettings();
        locationSettingsResponseTask.addOnSuccessListener(requireActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // Location settings are satisfied, initialize the map
                startLocationUpdates();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(requireActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        // Location settings are not satisfied, prompt the user to enable location services
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(requireActivity(), LOCATION_SETTINGS_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error
                    }
                }
            }
        });



        // Request location updates
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private Task<LocationSettingsResponse> checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(requireActivity());
        return client.checkLocationSettings(builder.build());
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            servicePartnersRef.child(currentUser.getUid()).child("location").setValue(currentLocation);
                            Toast.makeText(getContext(), "Current Location is updated", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000).setMinUpdateIntervalMillis(5000).build();

        return locationRequest;
    }








}
