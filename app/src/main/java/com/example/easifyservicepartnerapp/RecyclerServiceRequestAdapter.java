package com.example.easifyservicepartnerapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RecyclerServiceRequestAdapter extends RecyclerView.Adapter<RecyclerServiceRequestAdapter.ViewHolder>{
    Context context;
    ArrayList<OrderModel> arrOrders;


    public RecyclerServiceRequestAdapter(Context context, ArrayList<OrderModel> arrOrders) {
        this.context = context;
        this.arrOrders = arrOrders;
    }

    @NonNull
    @Override
    public RecyclerServiceRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_req_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerServiceRequestAdapter.ViewHolder holder, int position) {
        holder.orderid.setText(arrOrders.get(position).order_id);
        final String[] searchAddress = new String[1];
        Address address = getAddressFromLatLng(arrOrders.get(position).location);
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
//        String location = LocationUtils.getLocationName(arrOrders.get(position).location);

        holder.location.setText(address.getAddressLine(0));
        holder.landmark.setText(arrOrders.get(position).landmark);

    }

    @Override
    public int getItemCount() {
        return arrOrders.size();
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
        TextView orderid,location,landmark;
        AppCompatButton accept;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderid = itemView.findViewById(R.id.order_id);
            location = itemView.findViewById(R.id.location_name);
            landmark = itemView.findViewById(R.id.landmark);
            accept = itemView.findViewById(R.id.accept_btn);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle accept button click
                    int position = getAdapterPosition(); // Get the selected item position from the adapter
                    if (position != RecyclerView.NO_POSITION) {
                        OrderModel selectedOrder = arrOrders.get(position);
                        Log.e("slected order",selectedOrder.getOrder_id());// Get the selected order from the adapter
                        updateOrderStatus(selectedOrder, "active"); // Update the status of the order to "active" in the Firebase Database
                    }
                }
            });
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


    }
}
