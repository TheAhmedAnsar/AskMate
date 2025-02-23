package com.askmate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.askmate.databinding.ActivityUpdateProfileBinding;
import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class updateProfile extends AppCompatActivity {
    ActivityUpdateProfileBinding binding;
    String tag;
    Uri uri2;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_update_profile);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().setStatusBarColor(Color.parseColor("#1B8BF2"));

        progressDialog = new ProgressDialog(this);
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String name = snapshot.child("name").getValue(String.class);
                        String phone = snapshot.child("number").getValue(String.class);
                        binding.username.setText(name);
//                       updateprofileBinding.username.setTag("internet");
                        binding.myNumber.setText(phone);


                        String imageurl = snapshot.child("image").getValue(String.class);
                        if (!isDestroyed()) {
                            Glide.with(updateProfile.this).load(imageurl)
                                    .placeholder(R.drawable.user)
                                    .dontAnimate().fitCenter().into(binding.profileImage);
                            binding.profileImage.setTag("internet");
                        }
                        tag = (String) binding.profileImage.getTag();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher1.launch(ImagePicker.Companion.with(updateProfile.this)
                        .crop()
                        .cropFreeStyle()
                        .galleryOnly()

                        .setOutputFormat(Bitmap.CompressFormat.JPEG)
                        .createIntent()
                );
            }
        });


        binding.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ProgressDialog progressDialog = new ProgressDialog(updateprofile.this);
                progressDialog.setMessage("Updating...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String name = binding.username.getText().toString();
//                String nametag = (String) updateprofileBinding.username.getTag();
                if (!name.isEmpty())
                {
                    HashMap<String, Object> nameUpdate = new HashMap<>();
                    nameUpdate.put("name", name);
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).updateChildren(nameUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    String newtag = (String) binding.profileImage.getTag();

                                    if (!newtag.isEmpty())
                                    {
                                        Log.d("Ok", "Button: " + newtag);
                                        if (newtag.equals("gallery"))
                                        {
                                            if (uri2 != null)
                                            {
//                                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//                                                StorageReference imageRef = storageRef.child(FirebaseAuth.getInstance().getUid()).child("askmate")
//                                                        .child("profile_pic");
//                                                imageRef.putFile(fileUri)
                                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//                                                StorageReference imageRef = storageRef.child(FirebaseAuth.getInstance().getUid()).child("profile_pic");
                                                StorageReference imageRef = storageRef.child(FirebaseAuth.getInstance().getUid()).child("askmate")
                                                        .child("profile_pic");
                                                imageRef.putFile(uri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {

                                                                HashMap<String , Object> imageAdd = new HashMap<>();
                                                                imageAdd.put("image", uri.toString());
                                                                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                                                       .updateChildren(imageAdd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {

                                                                                Toast.makeText(updateProfile.this, "Data Uplaoded", Toast.LENGTH_SHORT).show();
                                                                                progressDialog.dismiss();
                                                                                onBackPressed();

                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(updateProfile.this, "Try again after sometime", Toast.LENGTH_SHORT).show();
                                                                                progressDialog.dismiss();
//                                                                                onBackPressed();

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

                                        }
                                        else
                                        {
                                            Log.d("Ok", "Button: " + "internet");
                                            Toast.makeText(updateProfile.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            onBackPressed();

                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(updateProfile.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        onBackPressed();
                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {


                                }
                            });

                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(updateProfile.this, "Please fill required details", Toast.LENGTH_SHORT).show();
                }



            }
        });




    }

    ActivityResultLauncher<Intent> launcher1 =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    uri2  = uri;

                    if (uri != null && !uri.toString().isEmpty()) {
//                        imageUriCache.add(uri);

                        binding.profileImage.setImageURI(uri);
                        binding.profileImage.setTag("gallery");

                    } else {
                        // Handle the case where URI is null or empty
                        Toast.makeText(updateProfile.this, "Failed to get image. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    Toast.makeText(updateProfile.this, "Error while picking image. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

}