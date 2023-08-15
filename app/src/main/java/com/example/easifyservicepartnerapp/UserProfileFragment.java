package com.example.easifyservicepartnerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserProfileFragment extends Fragment {

    FloatingActionButton logoutButton;

    TextInputEditText profileEmail;
    TextInputEditText profileName;

    TextInputEditText profilePhone;
    FloatingActionButton profileEditButton;
    FloatingActionButton profileConfirmButton;
    Boolean isEditable = false;
    // Reference to the Firebase Database
    DatabaseReference userDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileName = view.findViewById(R.id.profileName);
        profilePhone = view.findViewById(R.id.profilePhone);

        SharedPreferences pref = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String name = pref.getString("name","username");
        String phoneNo = pref.getString("phoneNo","phone number");
        String email = pref.getString("email","email");
        profileEmail.setText(email);
        profileName.setText(name);
        profilePhone.setText(phoneNo);
        logoutButton = view.findViewById((R.id.logoutButton));
        profileEditButton = view.findViewById(R.id.profileEditButton);
        profileConfirmButton = view.findViewById(R.id.profileConfirmButton);


        //logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(view.getContext(), "Logging Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        // Get the current Firebase user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId);
            fetchAndUpdateUserProfile();
        }

        // Profile Edit Button
        profileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle editability of the text fields
                isEditable = !isEditable;
                setEditable(isEditable);
            }
        });

        // Profile Confirm Button
        profileConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the edited details
                saveEditedDetails();
            }
        });
        return view;
    }
    private void fetchAndUpdateUserProfile() {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch user details from Firebase
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userEmail = dataSnapshot.child("userEmail").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phoneNo").getValue(String.class);

                    // Fetch user details from SharedPreferences
                    SharedPreferences pref = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                    String name = pref.getString("name", "username");
                    String email = pref.getString("email", "email");
                    String phoneNo = pref.getString("phoneNo", "phone number");

                    // Check if Firebase details differ from SharedPreferences
                    boolean isNameChanged = !TextUtils.isEmpty(userName) && !userName.equals(name);
                    boolean isEmailChanged = !TextUtils.isEmpty(userEmail) && !userEmail.equals(email);
                    boolean isPhoneChanged = !TextUtils.isEmpty(phoneNumber) && !phoneNumber.equals(phoneNo);

                    // If any detail is different, update SharedPreferences
                    if (isNameChanged || isEmailChanged || isPhoneChanged) {
                        SharedPreferences.Editor editor = pref.edit();
                        if (isNameChanged) {
                            editor.putString("name", userName);
                            profileName.setText(userName);
                        }
                        if (isEmailChanged) {
                            editor.putString("email", userEmail);
                            profileEmail.setText(userEmail);
                        }
                        if (isPhoneChanged) {
                            editor.putString("phoneNo", phoneNumber);
                            profilePhone.setText(phoneNumber);
                        }
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.d("UserProfileFragment", "Failed to fetch user details from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void setEditable(boolean isEditable) {
        // Enable or disable editability of the text fields based on the isEditable flag
        profileName.setEnabled(isEditable);
        profileEmail.setEnabled(isEditable);
        profilePhone.setEnabled(isEditable);
    }

    private void saveEditedDetails() {
        // Get the edited details from the text fields
        String editedName = profileName.getText().toString().trim();
        String editedEmail = profileEmail.getText().toString().trim();
        String editedPhone = profilePhone.getText().toString().trim();

        // Update the SharedPreferences with the edited details
        SharedPreferences pref = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", editedName);
        editor.putString("email", editedEmail);
        editor.putString("phoneNo", editedPhone);
        editor.apply();

        // Disable editability after saving
        isEditable = false;
        setEditable(isEditable);

        // You can also update the details in the Firebase Database if needed
        if (userDatabase != null) {
            userDatabase.child("userName").setValue(editedName);
            userDatabase.child("userEmail").setValue(editedEmail);
            userDatabase.child("phoneNumber").setValue(editedPhone);
        }

        // Show a toast to indicate successful save
        Toast.makeText(getActivity(), "Profile details saved", Toast.LENGTH_SHORT).show();
    }

}