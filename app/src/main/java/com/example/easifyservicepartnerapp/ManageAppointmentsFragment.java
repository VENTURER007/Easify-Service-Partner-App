package com.example.easifyservicepartnerapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class ManageAppointmentsFragment extends Fragment {

    private DatabaseReference databaseReference , userdbRef;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    AppCompatButton maps;

    LatLngWrapper location;

    String lndmrk;

    TextView order_id,cust_name,mobile_no,address1,landmark;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_appointments, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // Get the current user ID (service_partner_id)
        String servicePartnerId = currentUser.getUid(); // Replace with your code to get the current user ID

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");
        userdbRef = FirebaseDatabase.getInstance().getReference("users");

        // Create a query to fetch the order details for the current user with the active status
        Query query = databaseReference.orderByChild("service_partner_id")
                .equalTo(servicePartnerId);


        // Attach a listener to the query
        Log.d("from manage fragment","working1");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("from manage fragment","working2");
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {

                    OrderModel order = orderSnapshot.getValue(OrderModel.class);
                    if (order != null && order.getStatus().equals("active")) {
                        // Process the order with "active" status
                        Log.e("From Manage Fragment",order.order_id);
                        order_id = view.findViewById(R.id.order_Id);
                        cust_name = view.findViewById(R.id.cust_name);
                        mobile_no = view.findViewById(R.id.mobile_no);
                        address1 = view.findViewById(R.id.addressView);
                        landmark = view.findViewById(R.id.landmarkId);
                        location = order.location;
                        lndmrk = order.landmark;
                        order_id.setText(order.order_id);
                        cust_name.setText(currentUser.getDisplayName());
                        mobile_no.setText(currentUser.getPhoneNumber());
                        userdbRef.child(order.user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                                    String phoneNo = dataSnapshot.child("phoneNo").getValue(String.class);


                                    if (fullName != null && phoneNo != null) {
                                        // Process the name and phone number
                                        cust_name.setText(fullName);
                                        mobile_no.setText(phoneNo);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors
                                Log.d("from manage fragment","user details Not fetched");
                            }
                        });


                        final String[] searchAddress = new String[1];
                        Address address = getAddressFromLatLng(order.location);
                        if(address!=null) {
                            searchAddress[0] = address.getAddressLine(0);
                            Log.d("Address : ", "" + address.toString());
                            Log.d("Address Line : ",""+address.getAddressLine(0));
                            Log.d("Phone : ",""+address.getPhone());
                            Log.d("Pin Code : ",""+address.getPostalCode());
                            Log.d("Feature : ",""+address.getFeatureName());
                            Log.d("More : ",""+address.getLocality());
                        }
                        else {
                            Log.d("Adddress","Address Not Found");
                        }

                        address1.setText(address.getAddressLine(0));
                        landmark.setText(order.landmark);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e("From Manage Fragment","Order not fetched");
            }
        });


        maps = view.findViewById(R.id.mapView);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

// Assuming you have the latitude and longitude values from the "orders" schema
                double latitude = location.latitude;
                double longitude = location.longitude;


// Create a Uri for the location

                // Additional information as a search query

// Create a Uri for the location with the search query
                String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + lndmrk + ")";

// Create an Intent with the Uri
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps"); // Specify Google Maps app package

// Check if there is a Google Maps app installed
                PackageManager packageManager = requireActivity().getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    // Start the activity to open Google Maps
                    startActivity(intent);
                } else {
                    // Google Maps app is not installed, handle this scenario
                    // For example, you can open a web browser with the Google Maps website
                    // or show a message to the user suggesting to install Google Maps
                    // based on your application's requirements.
                }


            }
        });
        return view;
    }
    private Address getAddressFromLatLng(LatLngWrapper latLng){
        Geocoder geocoder=new Geocoder(getActivity());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                Log.e("addresses",addresses.toString());
                Log.e("address",address.toString());
                return address;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}