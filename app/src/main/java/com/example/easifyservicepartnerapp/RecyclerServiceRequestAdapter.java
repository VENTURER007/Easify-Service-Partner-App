package com.example.easifyservicepartnerapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class RecyclerServiceRequestAdapter extends RecyclerView.Adapter<RecyclerServiceRequestAdapter.ViewHolder>{
    Context context;
    ArrayList<OrderModel> arrOrders;

    boolean showEmptyView;

    private DatabaseReference servicedbRef;


    public RecyclerServiceRequestAdapter(Context context, ArrayList<OrderModel> arrOrders) {
        this.context = context;
        this.arrOrders = arrOrders;
        this.showEmptyView = arrOrders.isEmpty();
    }

    @NonNull
    @Override
    public RecyclerServiceRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (viewType == R.layout.no_orders) {
            // Inflate the empty view layout
            view = inflater.inflate(R.layout.no_orders, parent, false);
        } else {
            // Inflate the item view layout
            view = inflater.inflate(R.layout.service_req_row, parent, false);
        }

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerServiceRequestAdapter.ViewHolder holder, int position) {
        if (showEmptyView) {
            // Handle empty view
            Log.e("from service request adpater","no orders here!");
            // You can customize this section to display an appropriate message or layout for the empty view
        } else {
            // Handle item view
            // Your existing code for populating item views goes here
            if (!arrOrders.isEmpty()) {
                OrderModel order = arrOrders.get(position);
                holder.orderid.setText(arrOrders.get(position).order_id);
                final String[] searchAddress = new String[1];

                Address address = getAddressFromLatLng(arrOrders.get(position).location);
                if (address != null) {
                    searchAddress[0] = address.getAddressLine(0);
                    Log.d("Address : ", "" + address.toString());
                } else {
                    Log.d("Adddress", "Address Not Found");
                }
//        String location = LocationUtils.getLocationName(arrOrders.get(position).location);

                holder.location.setText(address.getAddressLine(0));
                holder.landmark.setText(arrOrders.get(position).landmark);

                //fetching service details
                servicedbRef = FirebaseDatabase.getInstance().getReference("services");
                servicedbRef.child(arrOrders.get(position).service_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String serviceName = dataSnapshot.child("serviceName").getValue(String.class);
                            Long serviceCharge = dataSnapshot.child("serviceCharge").getValue(Long.class);


                            if (serviceName != null && serviceCharge != null) {
                                // Process the name and phone number
                                holder.service_name.setText(serviceName);
                                holder.service_charge.setText(serviceCharge.toString());
                                Log.d("from manage fragment", serviceName.toString() + serviceCharge.toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors
                        Log.d("from manage fragment", "Service Details Not fetched");
                    }
                });
            }


            holder.orderid.setText(arrOrders.get(position).order_id);
            final String[] searchAddress = new String[1];
            Address address = getAddressFromLatLng(arrOrders.get(position).location);
            if (address != null) {
                searchAddress[0] = address.getAddressLine(0);
                Log.d("Address : ", "" + address.toString());
                Log.d("Address Line : ", "" + address.getAddressLine(0));
                Log.d("Phone : ", "" + address.getPhone());
                Log.d("Pin Code : ", "" + address.getPostalCode());
                Log.d("Feature : ", "" + address.getFeatureName());
                Log.d("More : ", "" + address.getLocality());
            } else {
                Log.d("Adddress", "Address Not Found");
            }
//        String location = LocationUtils.getLocationName(arrOrders.get(position).location);

            holder.location.setText(address.getAddressLine(0));
            holder.landmark.setText(arrOrders.get(position).landmark);

            //fetching service details
            servicedbRef = FirebaseDatabase.getInstance().getReference("services");
            servicedbRef.child(arrOrders.get(position).service_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String serviceName = dataSnapshot.child("serviceName").getValue(String.class);
                        Long serviceCharge = dataSnapshot.child("serviceCharge").getValue(Long.class);


                        if (serviceName != null && serviceCharge != null) {
                            // Process the name and phone number
                            holder.service_name.setText(serviceName);
                            holder.service_charge.setText(serviceCharge.toString());
                            Log.d("from manage fragment", serviceName.toString() + serviceCharge.toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                    Log.d("from manage fragment", "Service Details Not fetched");
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (showEmptyView) {
            return 1; // Displaying empty view
        } else {
            return arrOrders.size(); // Displaying items in the RecyclerView
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (showEmptyView) {
            return R.layout.no_orders; // Replace with the layout file for your empty view
        } else {
            return R.layout.service_req_row; // Replace with the layout file for your item view
        }
    }


    private Address getAddressFromLatLng(LatLngWrapper latLng){
        Geocoder geocoder=new Geocoder(context);
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderid, location, landmark, service_name, service_charge;
        AppCompatButton accept,reject;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderid = itemView.findViewById(R.id.order_id);
            location = itemView.findViewById(R.id.location_name);
            landmark = itemView.findViewById(R.id.upi_id_input);
            accept = itemView.findViewById(R.id.accept_btn);
            reject = itemView.findViewById(R.id.reject_btn);
            service_name = itemView.findViewById(R.id.servicenameViewrow);
            service_charge = itemView.findViewById(R.id.serviceChargeViewrow);


            if (accept != null) {
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            OrderModel selectedOrder = arrOrders.get(position);
                            Log.e("selected order", selectedOrder.getOrder_id());
                            updateOrderStatus(selectedOrder, "active");
                        }
                    }
                });
            }
            if (reject != null) {
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            OrderModel selectedOrder = arrOrders.get(position);

                            rejectOrder(selectedOrder, "false");
                        }
                    }
                });
            }
        }

        private void updateOrderStatus(OrderModel order, String status) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference orderRef = database.getReference("orders").child(order.getOrder_id());
            orderRef.child("status").setValue(status)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Order status updated successfully
                            Toast.makeText(context.getApplicationContext(), "Order accepted...Check Manage Order", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update order status
                            Toast.makeText(context.getApplicationContext(), "Order not accepted!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        private void rejectOrder(OrderModel order, String status) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference orderRef = database.getReference("orders").child(order.getOrder_id());

            // Delete the order from Firebase Realtime Database
            orderRef.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("Rejected order", order.getOrder_id());
                            Toast.makeText(context, "Appointment cancelled!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Delete Order", "Failed to delete order: " + e.getMessage());
                        }
                    });
        }





    }
}
