package com.askmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.askmate.databinding.ActivitySettingProfileBinding;
import com.askmate.help.About_us;
import com.askmate.help.Contact_us;
import com.askmate.help.Feature_request;
import com.askmate.help.Send_feedback;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class setting_profile extends AppCompatActivity {

    ActivitySettingProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingProfileBinding.inflate(getLayoutInflater());
        View view  = binding.getRoot();
        setContentView(view);
//        setContentView(R.layout.activity_setting_profile);

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String imageProfile = snapshot.child("image").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);
                        String number = snapshot.child("number").getValue(String.class);
                        if (!isFinishing() && !isDestroyed()) {
                            Glide.with(setting_profile.this).load(imageProfile)
                                    .placeholder(R.drawable.user)
                                    .fitCenter().into(binding.profileImage);
                        }
                        binding.userName.setText(name);
                        String mynumber = "+91 " + number;
                        binding.numberMy.setText(mynumber);


                        Log.d("imageProfile", "onDataChange: " + imageProfile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.featureRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(setting_profile.this, Feature_request.class);
                startActivity(intent);

            }
        });

        binding.sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(setting_profile.this, Send_feedback.class);
                startActivity(intent);

            }
        });


        binding.contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(setting_profile.this, Contact_us.class);
                startActivity(intent);

            }
        });


        binding.aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(setting_profile.this, About_us.class);
                startActivity(intent);
            }
        });




        binding.tapToEditLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setting_profile.this , updateProfile.class);
                startActivity(intent);
            }
        });

    }
}