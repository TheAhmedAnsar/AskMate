package com.askmate.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.askmate.MainActivity;
import com.askmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Locale;

public class uploadAlldata extends AppCompatActivity {
    String name;
    String uriString;
    String uri = uriString; // Convert the URI string back to Uri object
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_alldata);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        uriString = intent.getStringExtra("uri");
//        String uri = uriString; // Convert the URI string back to Uri object
        number = intent.getStringExtra("number");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("newTag", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get the device token
                        String token = task.getResult();
                        uploadOwnerData(name, uriString, number, token);

                        Log.d("token", "FCM Token: " + token);

                        // Store the token in your database (e.g., Firebase Realtime Database)
//                        updateDeviceTokenInDatabase(token);
                    }
                });


    }

    private void uploadOwnerData(String name, String uriString, String number, String token) {
        Uri fileUri = Uri.parse(uriString);
        HashMap<String, Object> mapKey = new HashMap<>();
        mapKey.put("name", name.toLowerCase(Locale.ROOT));
        mapKey.put("number", number);
        mapKey.put("uid", FirebaseAuth.getInstance().getUid());
        mapKey.put("deviceToken", token);
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .setValue(mapKey).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference imageRef = storageRef.child(FirebaseAuth.getInstance().getUid()).child("askmate")
                                .child("profile_pic");
                        imageRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String, Object> imageKey = new HashMap<>();
                                        imageKey.put("image", uri.toString());
                                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                                .updateChildren(imageKey).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        HashMap<String, Object> namepload = new HashMap<>();
                                                        namepload.put(name, true);
                                                        FirebaseDatabase.getInstance().getReference("askmate").child("usernames")
                                                                .updateChildren(namepload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        HashMap<String, Object> users = new HashMap<>();
                                                                        users.put(number, FirebaseAuth.getInstance().getUid());
//                    SharedPreferences prefs = getSharedPreferences("first_details", Context.MODE_PRIVATE);
//                    prefs.edit().putBoolean("profile", true).apply();

                                                                        FirebaseDatabase.getInstance().getReference().child("askmate").child("register")
                                                                                .updateChildren(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                        SharedPreferences sp = getSharedPreferences("home", Context.MODE_PRIVATE);
                                                                                        SharedPreferences.Editor editor = sp.edit();
                                                                                        editor.putBoolean("home", true);

                                                                                        boolean check = editor.commit();
                                                                                        if (check) {
                                                                                            Intent intent = new Intent(uploadAlldata.this, MainActivity.class);
                                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                            startActivity(intent);
                                                                                            finish();

                                                                                        } else {
                                                                                            Toast.makeText(uploadAlldata.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                                                        }


                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {

                                                                                    }
                                                                                });


                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });


                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}