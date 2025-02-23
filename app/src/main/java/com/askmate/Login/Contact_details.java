package com.askmate.Login;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.askmate.R;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Contact_details extends AppCompatActivity {
    TextInputEditText owner_name, my_number, my_number_alternate;
    TextView profile_change;
    boolean change;
    Set<String> uriSet = new HashSet<>();
    List<Uri> imageUriCache = new ArrayList<>();
    String phoneNumber;
    Uri uri, uri2;
    String send_data, profile_url = "";
    de.hdodenhof.circleimageview.CircleImageView my_dp;

    String myNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        getWindow().setStatusBarColor(Color.parseColor("#1B8BF2"));
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        AppCompatButton Button = findViewById(R.id.Button);
        owner_name = findViewById(R.id.owner_name);
        my_number = findViewById(R.id.my_number);
//        my_number_alternate = view.findViewById(R.id.my_number_alternate);

        profile_change = findViewById(R.id.change_pic);
        my_dp = findViewById(R.id.profile_image);
        change = false;
//Intent intent = getIntent();
////        myNumber = intent.getStringExtra("phoneNumber");
        SharedPreferences sp = getSharedPreferences("number", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
    phoneNumber = sp.getString("number", null);

        if (!phoneNumber.isEmpty())
        {
            my_number.setText(phoneNumber);
        }
        else
        {
//            my_number.set
            Toast.makeText(Contact_details.this, "Failed to load data, please contact administrator", Toast.LENGTH_SHORT).show();
        }

//        AppCompatButton button = findViewById(R.id.Button);
        AppCompatImageView back = findViewById(R.id.backbutton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onBackPressed();
            }
        });


        profile_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                change = true;
                launcher.launch(ImagePicker.Companion.with(Contact_details.this)
                        .crop()
                        .cropFreeStyle()
                        .galleryOnly()
                        .setOutputFormat(Bitmap.CompressFormat.JPEG)
                        .createIntent()
                );


            }
        });

        // for storing data
        SharedPreferences saved_data = getSharedPreferences("owner", MODE_PRIVATE);
        SharedPreferences.Editor editor_data = saved_data.edit();

        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (profile_url.isEmpty()) {
                    Toast.makeText(Contact_details.this, "Please add profile image", Toast.LENGTH_SHORT).show();
                } else {

                    String Owner_name = owner_name.getText().toString();
                    String Owner_number = my_number.getText().toString();

                    if (Owner_name.isEmpty() || Owner_number.isEmpty()) {
                        Toast.makeText(Contact_details.this, "Please fill the details", Toast.LENGTH_SHORT).show();
                    } else {

                        // Check if the entered name is available in Firebase
                        DatabaseReference usernamesRef = FirebaseDatabase.getInstance().getReference("askmate").child("usernames");
                        usernamesRef.child(Owner_name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Username already exists
                                    Toast.makeText(Contact_details.this, "Username not available", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Username is available
                                    // Store the owner's information
                                    editor_data.putString("name", Owner_name);
                                    editor_data.putString("number", Owner_number);
                                    saveImageURIsToSharedPreferences();
                                    boolean success = editor_data.commit();

                                    if (success) {
                                        // Proceed with intent
                                        Intent intent1 = new Intent(Contact_details.this, Choose_location_1.class);
                                        intent1.putExtra("name", Owner_name);
                                        intent1.putExtra("uri", profile_url);
                                        intent1.putExtra("number", Owner_number);
                                        Log.d("Details", "Details: " + Owner_name + " " + Owner_number + " " + uri2.toString());

                                        startActivity(intent1);

                                    } else {
                                        Toast.makeText(Contact_details.this, "Oops something went wrong!", Toast.LENGTH_SHORT).show();
                                        owner_name.setText("");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle database error
                                Toast.makeText(Contact_details.this, "Database error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void saveImageURIsToSharedPreferences() {

        // Save the Set of URIs to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("profileimage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("image", profile_url);
        editor.apply();
    }


    ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    uri2 = uri;

                    if (uri != null && !uri.toString().isEmpty()) {
//                        saveImageToCache(uri);
                        profile_url = uri.toString();
//                        imageUriCache.add(uri);
                        my_dp.setImageURI(uri);
//                        my_dp.setTag(R.id.source, "gallery");

                    } else {
                        // Handle the case where URI is null or empty
                        Toast.makeText(Contact_details.this, "Failed to get image. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    Toast.makeText(Contact_details.this, "Error while picking image. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

}