package com.example.easifyservicepartnerapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ManageAppointmentsFragment extends Fragment {

    private DatabaseReference databaseReference , userdbRef, servicedbRef;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    FirebaseDatabase database;

    AppCompatButton maps,cncl_appomnt,req_pay;

    LatLngWrapper location;

    String lndmrk;

    boolean hasActiveOrder = false;

    private Context appContext;

    TextView order_id,cust_name,mobile_no,address1,landmark,service_name,service_charge;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View nothing_view = inflater.inflate(R.layout.no_orders, container, false);
        View view = inflater.inflate(R.layout.fragment_manage_appointments, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // Get the current user ID (service_partner_id)
        String servicePartnerId = currentUser.getUid(); // Replace with your code to get the current user ID

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");
        userdbRef = FirebaseDatabase.getInstance().getReference("users");
        servicedbRef = FirebaseDatabase.getInstance().getReference("services");

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
                        hasActiveOrder = true;
                        // Process the order with "active" status
                        Log.e("From Manage Fragment","orders are here"+order.order_id);
                        order_id = view.findViewById(R.id.order_Id);
                        cust_name = view.findViewById(R.id.cust_name);
                        mobile_no = view.findViewById(R.id.mobile_no);
                        address1 = view.findViewById(R.id.addressView);
                        landmark = view.findViewById(R.id.landmarkId);
                        service_name = view.findViewById(R.id.serviceNameView);
                        service_charge = view.findViewById(R.id.servicChargeView);
                        location = order.location;
                        lndmrk = order.landmark;
                        order_id.setText(order.order_id);
                        cust_name.setText(currentUser.getDisplayName());
                        mobile_no.setText(currentUser.getPhoneNumber());
                        servicedbRef.child(order.service_id ).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String serviceName = dataSnapshot.child("serviceName").getValue(String.class);
                                    Long serviceCharge = dataSnapshot.child("serviceCharge").getValue(Long.class);


                                    if (serviceName != null && serviceCharge != null) {
                                        // Process the name and phone number
                                        service_name.setText(serviceName);
                                        service_charge.setText(serviceCharge.toString());
                                        Log.d("from manage fragment",serviceName.toString()+serviceCharge.toString());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors
                                Log.d("from manage fragment","Service Details Not fetched");
                            }
                        });
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
                    else{
                        hasActiveOrder = false;
                        Log.d("Manage Order","No orders accepted");
                        view.setVisibility(View.GONE);

                    }
                }
                if (!hasActiveOrder) {
                    // No active orders found, set visibility to GONE
                    view.setVisibility(View.GONE);
                    Log.d("Manage Order", "No orders accepted");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e("From Manage Fragment","Order not fetched");
                view.setVisibility(View.GONE);
            }
        });
        //cancel appoinmtnet
        cncl_appomnt = view.findViewById(R.id.cancel_service);
        cncl_appomnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the order details from Firebase Realtime Database
                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
                String userId = currentUser.getUid();
                Query query = ordersRef.orderByChild("service_partner_id").equalTo(userId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                            orderSnapshot.getRef().removeValue();
                            Toast.makeText(getContext(), "Appointment cancelled!", Toast.LENGTH_SHORT).show();
                            view.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Delete Order", "Failed to delete order: " + databaseError.getMessage());
                    }
                });
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
//        if (hasActiveOrder) {
//            return view;
//        }else {
//            return nothing_view;
//        }
    }
    private Address getAddressFromLatLng(LatLngWrapper latLng){
        Geocoder geocoder=new Geocoder(appContext);
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
    private void showBottomDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.req_pay_bottom_layout);

        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        TextInputLayout upi_id = dialog.findViewById(R.id.upi_id);

        Button submitUpi = dialog.findViewById(R.id.submit_upi);

        // Search clicked
        submitUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String upiId = upi_id.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(upiId)) {
                    Toast.makeText(getContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
                } else if (!isValidUpiId(upiId)) {
                    Toast.makeText(getContext(), "Invalid UPI ID format", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    currentUser = mAuth.getCurrentUser();
                    database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("orders");
                    String user_id = currentUser.getUid();

                    // Query the database to check if an order already exists for the current user
                    ref.orderByChild("user_id").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Handle the data snapshot
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Failed to read value
                            Toast.makeText(getContext(), "Failed to check existing orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private boolean isValidUpiId(String upiId) {
        // UPI ID format regex pattern
        String regex = "[\\w.-]+@[\\w]+";

        // Validate the upiId against the regex pattern
        return upiId.matches(regex);
    }



}