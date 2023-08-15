package com.example.easifyservicepartnerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easifyservicepartnerapp.OrderModel;
import com.example.easifyservicepartnerapp.R;
import com.example.easifyservicepartnerapp.RecyclerServiceRequestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    RecyclerServiceRequestAdapter adapter;

    FirebaseAuth mAuth;

    FirebaseDatabase database;

    Button accept,reject;

    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_fragement, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_service_req);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        DatabaseReference ordersRef = database.getReference("orders");

        Query query = ordersRef.orderByChild("service_partner_id")
                .equalTo(currentUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<OrderModel> orders = new ArrayList<>();
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    OrderModel order = orderSnapshot.getValue(OrderModel.class);
                    if (order != null && order.getStatus().equals("false")) {
                        orders.add(order);
                        Log.e("Location", "servicePartnerId: " + order.getService_partner_id() + ", customerId: " + order.getUser_id());
                    }
                }
                Log.e("success", orders.toString());

                // Initialize and set adapter here
                adapter = new RecyclerServiceRequestAdapter(getContext(), orders);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the database query
                Log.e("success", "order not fetched properly");
            }
        });





        return view;
    }
    }

