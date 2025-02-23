package com.askmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.askmate.databinding.ActivityCoachingDetailsBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class coachingDetails extends AppCompatActivity {
    ActivityCoachingDetailsBinding binding;
String key;

    String cityLocality;
    double latitude ;
    double longitude;
    boolean enrollCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_coaching_details);
        binding = ActivityCoachingDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
      key =   intent.getStringExtra("key");

        SharedPreferences preferences = getSharedPreferences("Location", MODE_PRIVATE);
        latitude = preferences.getFloat("latitude", 0.0f);  // Default value is 0.0 if not found
        longitude = preferences.getFloat("longitude", 0.0f);

//        / Check if latitude and longitude are not 0 (indicating default values)
        if (latitude != 0f && longitude != 0f) {
            // Use Geocoder to get the locality from latitude and longitude
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    cityLocality = address.getLocality();
                    // Now you have the locality from latitude and longitude
                    Log.d("Locality", "Locality: " +     cityLocality);
                } else {
                    Log.d("Locality", "No address found for the given coordinates.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Locality", "Error getting address from Geocoder: " + e.getMessage());
            }
        } else {
            Log.d("Locality", "Latitude and longitude not found in SharedPreferences.");
        }


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Coachings").child(cityLocality)
                .child(key)
                .child("coaching_profiles")
                .child("images");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SliderItem> sliderItems = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String imageUrl = snapshot.getValue(String.class);
                    SliderItem sliderItem = new SliderItem(imageUrl);
                    sliderItems.add(sliderItem);
                }


                // Pass sliderItems to your SliderAdapter
                SliderAdapterExample adapter = new SliderAdapterExample(sliderItems);
                binding.imageSlider.setSliderAdapter(adapter);
                binding.imageSlider.setScrollTimeInSec(3);
                binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                binding.imageSlider.setAutoCycle(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error retrieving image URLs: " + databaseError.getMessage());
            }
        });


//        CheckEnroll();
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String number = snapshot.child("number").getValue(String.class);
                        FirebaseDatabase.getInstance().getReference().child("Coachings").child(cityLocality)
                                .child(key).child("admission").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren())
                                        {
                                            if(dataSnapshot.getValue().equals(name))
                                            {
                                                binding.enrollText.setText("Enrolled");
                                                enrollCheck = true;
                                                Log.d("enroll", "onDataChange: " + dataSnapshot.getKey());
                                                break;

                                            }
                                            else
                                            {
                                                binding.enrollText.setText("Enroll now");
                                                enrollCheck = false;

                                            }
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.enrollnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (enrollCheck == true) {
                    Toast.makeText(coachingDetails.this, "You already enrolled", Toast.LENGTH_SHORT).show();
                } else {
                    showEnroll();
                }

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Coachings").child(cityLocality)
                .child(key)
                .child("address").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Initialize an empty StringBuilder to concatenate non-empty fields
                        StringBuilder stringBuilder = new StringBuilder();

                        // Retrieve data from the dataSnapshot and concatenate non-empty fields
                        String apartment = dataSnapshot.child("apartment").getValue(String.class);
                        String block = dataSnapshot.child("block").getValue(String.class);
                        String area = dataSnapshot.child("area").getValue(String.class);
                        String sublocal = dataSnapshot.child("sublocal").getValue(String.class);
                        String local = dataSnapshot.child("local").getValue(String.class);

                        // Append non-empty fields in the desired sequence
                        if (apartment != null && !apartment.isEmpty()) {
                            stringBuilder.append(apartment).append(", ");
                        }
                        if (block != null && !block.isEmpty()) {
                            stringBuilder.append(block).append(", ");
                        }
                        if (area != null && !area.isEmpty()) {
                            stringBuilder.append(area).append(", ");
                        }
                        if (sublocal != null && !sublocal.isEmpty()) {
                            stringBuilder.append(sublocal).append(", ");
                        }
                        if (local != null && !local.isEmpty()) {
                            stringBuilder.append(local);
                        }

                        // Remove the trailing comma and space if present
                        String result = stringBuilder.toString().replaceAll(", $", "");

                        // Set the concatenated string to the TextView
                        binding.address.setText(result);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle potential errors here
                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Coachings")
                .child(cityLocality)
                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String coachingName = snapshot.child("coaching_profiles").child("name").getValue(String.class);
                        String mission = snapshot.child("coaching_profiles").child("vision").getValue(String.class);
                        StringBuilder announcement = new StringBuilder();

                        DataSnapshot announcementNode = snapshot.child("announcements");
                        if(announcementNode.exists())
                        {
                            for (DataSnapshot announcementDetails : snapshot.child("announcements").getChildren()) {
                            String description = announcementDetails.child("description").getValue(String.class);
//                                String announce = announcementDetails.getValue(String.class);
                                announcement.append("\u2022   ").append(description).append("\n");


                            }
                            binding.announceLinear.setVisibility(View.VISIBLE);

                            binding.announcement.setText(announcement.toString());
                        }
                        else
                        {
                            binding.announceLinear.setVisibility(View.GONE);

                        }

                        DataSnapshot ameneties = snapshot.child("amenities");
                        StringBuilder Amenities = new StringBuilder();


                        // Amenities
                        if (ameneties.exists()) {

                            for (DataSnapshot gradeSnapshot : ameneties.getChildren()) {
//                                String grade = gradeSnapshot.getKey();
                                String data = gradeSnapshot.getValue(String.class);
                                Amenities.append("\u2022   ").append(data).append("\n");
//                                             commerceFees.append("\u2022 ").append(grade).append("th grade: ₹").append(fees).append("\n");
                                binding.amenitiesData.setText(Amenities.toString());

                            }

                            // Display the fees details in your TextView
                            // textView.setText(feesEnglishPrimaryDetails.toString());
                        } else {
                            // The fees data for English primary level is not available
                            binding.amenities.setVisibility(View.GONE);
                        }


//                        Log.d("WOW", "onDataChange: " + snapshot.child("Fees_visibility").getValue(Boolean.class));



                        binding.name.setText(coachingName);
                        if (mission != null  && !mission.isEmpty()) {
                            binding.vision.setText(mission);
                            binding.missionLayout.setVisibility(View.VISIBLE);

                        }
                        else
                        {
                            binding.missionLayout.setVisibility(View.GONE);
                        }
                        Boolean fees_visibility = snapshot.child("Fees_visibility").getValue(Boolean.class);

                        if (fees_visibility != null && fees_visibility)
                        {

//                        if (snapshot.hasChild("enroll")) {
//                            // Enroll node exists, iterate through its children
//                            StringBuilder enrollDetails = new StringBuilder();
//                            for (DataSnapshot enrollSnapshot : dataSnapshot.child("enroll").getChildren()) {
//                                String name = enrollSnapshot.child("name").getValue(String.class);
//                                String phone = enrollSnapshot.child("phone").getValue(String.class);
//                                enrollDetails.append(name).append(": ").append(phone).append("\n");
//                            }
//                            binding.noEnroll.setText(enrollDetails.toString());
//                        } else {
                            // Enroll node doesn't exist
//                            binding.noEnroll.setText("No students enroll till now");
//                        }
//

                        // Fetch fees data
//                           StringBuilder feesEnglishPrimaryDetails = new StringBuilder();
                        StringBuilder feesEnglishPrimaryDetails = new StringBuilder();
                        StringBuilder feesHindiPrimaryDetails = new StringBuilder();
                        StringBuilder feesmarathiPrimaryDetails = new StringBuilder();

                        StringBuilder scienceFees = new StringBuilder();
                        StringBuilder commerceFees = new StringBuilder();
                        StringBuilder artsFees = new StringBuilder();

                        StringBuilder Specializationfees = new StringBuilder();


                        DataSnapshot primarySnapshot = snapshot.child("fees").child("primary").child("english");
                        DataSnapshot primarySnapshotHindi = snapshot.child("fees").child("primary").child("hindi");
                        DataSnapshot primarySnapshotMarathi = snapshot.child("fees").child("primary").child("marathi");


                        DataSnapshot secondaryScience = snapshot.child("fees").child("secondary").child("science");
                        DataSnapshot secondaryCommerce = snapshot.child("fees").child("secondary").child("commerce");
                        DataSnapshot secondaryArts = snapshot.child("fees").child("secondary").child("arts");

                        DataSnapshot specialization = snapshot.child("fees").child("specialization").child("specialization");





//                        if (primarySnapshot.exists()) {
//
//                            for (DataSnapshot gradeSnapshot : primarySnapshot.getChildren()) {
//                                String grade = gradeSnapshot.getKey();
//                                String fees = gradeSnapshot.getValue(String.class);
//                                feesEnglishPrimaryDetails.append("\u2022 ").append(grade).append("th grade: ₹").append(fees).append("\n");
////                                             commerceFees.append("\u2022 ").append(grade).append("th grade: ₹").append(fees).append("\n");
//
//                            }
//
//                            // Display the fees details in your TextView
//                            // textView.setText(feesEnglishPrimaryDetails.toString());
//                        } else {
//                            // The fees data for English primary level is not available
//                            binding.primaryFees.setVisibility(View.GONE);
//                        }

                            if (primarySnapshot.exists()) {
                                for (DataSnapshot gradeSnapshot : primarySnapshot.getChildren()) {
                                    String grade = gradeSnapshot.getKey();
                                    String fees = gradeSnapshot.getValue(String.class);

                                    // Remove leading zeros from the grade number
                                    int gradeNumber = Integer.parseInt(grade);

                                    // Determine the correct suffix for the grade number
                                    String suffix;
                                    if (gradeNumber == 1) {
                                        suffix = "st";
                                    } else if (gradeNumber == 2) {
                                        suffix = "nd";
                                    } else if (gradeNumber == 3) {
                                        suffix = "rd";
                                    } else {
                                        suffix = "th";
                                    }

                                    // Append the formatted grade and fees to the StringBuilder
                                    feesEnglishPrimaryDetails.append("\u2022 ")
                                            .append(gradeNumber)
                                            .append(suffix)
                                            .append(" grade: ₹")
                                            .append(fees)
                                            .append("\n");
                                }

                                // Display the fees details in your TextView
                                // textView.setText(feesEnglishPrimaryDetails.toString());
                            } else {
                                // The fees data for English primary level is not available
                                binding.primaryFees.setVisibility(View.GONE);
                            }


                            // for hindi
                        if (primarySnapshotHindi.exists()) {

                            for (DataSnapshot gradeSnapshot : primarySnapshotHindi.getChildren()) {
                                String grade = gradeSnapshot.getKey();
                                String fees = gradeSnapshot.getValue(String.class);
                                int gradeNumber = Integer.parseInt(grade);
                                String suffix;
                                if (gradeNumber == 1) {
                                    suffix = "st";
                                } else if (gradeNumber == 2) {
                                    suffix = "nd";
                                } else if (gradeNumber == 3) {
                                    suffix = "rd";
                                } else {
                                    suffix = "th";
                                }

                                // Append the formatted grade and fees to the StringBuilder
                                feesHindiPrimaryDetails.append("\u2022 ")
                                        .append(gradeNumber)
                                        .append(suffix)
                                        .append(" grade: ₹")
                                        .append(fees)
                                        .append("\n");

//                                feesHindiPrimaryDetails.append("\u2022 ").append(grade).append("th grade: ₹").append(fees).append("\n");
                            }

                            // Display the fees details in your TextView
                            // textView.setText(feesEnglishPrimaryDetails.toString());
                        } else {
                            // The fees data for English primary level is not available
                            binding.primaryFeesHindi.setVisibility(View.GONE);
                        }

                        // Marathi
                        if (primarySnapshotMarathi.exists()) {

                            for (DataSnapshot gradeSnapshot : primarySnapshotMarathi.getChildren()) {
                                String grade = gradeSnapshot.getKey();
                                String fees = gradeSnapshot.getValue(String.class);

                                int gradeNumber = Integer.parseInt(grade);
                                String suffix;
                                if (gradeNumber == 1) {
                                    suffix = "st";
                                } else if (gradeNumber == 2) {
                                    suffix = "nd";
                                } else if (gradeNumber == 3) {
                                    suffix = "rd";
                                } else {
                                    suffix = "th";
                                }

                                // Append the formatted grade and fees to the StringBuilder
                                feesmarathiPrimaryDetails.append("\u2022 ")
                                        .append(gradeNumber)
                                        .append(suffix)
                                        .append(" grade: ₹")
                                        .append(fees)
                                        .append("\n");


//                                feesmarathiPrimaryDetails.append("\u2022 ").append(grade).append("th grade: ₹").append(fees).append("\n");
                            }

                            // Display the fees details in your TextView
                            // textView.setText(feesEnglishPrimaryDetails.toString());
                        } else {
                            // The fees data for English primary level is not available
                            binding.primaryFeesMarathi.setVisibility(View.GONE);
                        }


                        // Secondary section

                        if (secondaryScience.exists()) {

                            for (DataSnapshot gradeSnapshot : secondaryScience.getChildren()) {
                                String grade = gradeSnapshot.getKey();
                                String fees = gradeSnapshot.getValue(String.class);

                                int gradeNumber = Integer.parseInt(grade);
                                String suffix;
                                if (gradeNumber == 1) {
                                    suffix = "st";
                                } else if (gradeNumber == 2) {
                                    suffix = "nd";
                                } else if (gradeNumber == 3) {
                                    suffix = "rd";
                                } else {
                                    suffix = "th";
                                }

                                // Append the formatted grade and fees to the StringBuilder
                                scienceFees.append("\u2022 ")
                                        .append(gradeNumber)
                                        .append(suffix)
                                        .append(" grade: ₹")
                                        .append(fees)
                                        .append("\n");


//                                scienceFees.append("\u2022 ").append(grade).append("th grade: ₹").append(fees).append("\n");
                            }

                            // Display the fees details in your TextView
                            // textView.setText(feesEnglishPrimaryDetails.toString());
                        } else {
                            // The fees data for English primary level is not available
                            binding.secondaryFees.setVisibility(View.GONE);
                        }



                        if (secondaryCommerce.exists()) {

                            for (DataSnapshot gradeSnapshot : secondaryCommerce.getChildren()) {
                                String grade = gradeSnapshot.getKey();
                                String fees = gradeSnapshot.getValue(String.class);

                                int gradeNumber = Integer.parseInt(grade);
                                String suffix;
                                if (gradeNumber == 1) {
                                    suffix = "st";
                                } else if (gradeNumber == 2) {
                                    suffix = "nd";
                                } else if (gradeNumber == 3) {
                                    suffix = "rd";
                                } else {
                                    suffix = "th";
                                }

                                // Append the formatted grade and fees to the StringBuilder
                                commerceFees.append("\u2022 ")
                                        .append(gradeNumber)
                                        .append(suffix)
                                        .append(" grade: ₹")
                                        .append(fees)
                                        .append("\n");


                                commerceFees.append("\u2022 ").append(grade).append("th grade: ₹").append(fees).append("\n");
                            }

                            // Display the fees details in your TextView
                            // textView.setText(feesEnglishPrimaryDetails.toString());
                        } else {
                            // The fees data for English primary level is not available
                            binding.secondaryFeesCommerce.setVisibility(View.GONE);
                        }


                        if (secondaryArts.exists()) {

                            for (DataSnapshot gradeSnapshot : secondaryArts.getChildren()) {
                                String grade = gradeSnapshot.getKey();
                                String fees = gradeSnapshot.getValue(String.class);
                                int gradeNumber = Integer.parseInt(grade);
                                String suffix;
                                if (gradeNumber == 1) {
                                    suffix = "st";
                                } else if (gradeNumber == 2) {
                                    suffix = "nd";
                                } else if (gradeNumber == 3) {
                                    suffix = "rd";
                                } else {
                                    suffix = "th";
                                }

                                // Append the formatted grade and fees to the StringBuilder
                                artsFees.append("\u2022 ")
                                        .append(gradeNumber)
                                        .append(suffix)
                                        .append(" grade: ₹")
                                        .append(fees)
                                        .append("\n");

//                                artsFees.append("\u2022 ").append(grade).append("th grade: ₹").append(fees).append("\n");
                            }

                            // Display the fees details in your TextView
                            // textView.setText(feesEnglishPrimaryDetails.toString());
                        } else {
                            // The fees data for English primary level is not available
                            binding.secondaryFeesArts.setVisibility(View.GONE);
                        }


                        if (specialization.exists()) {

                            for (DataSnapshot gradeSnapshot : specialization.getChildren()) {
                                String grade = gradeSnapshot.getKey();
                                String fees = gradeSnapshot.getValue(String.class);

//                                int gradeNumber = Integer.parseInt(grade);
//                                String suffix;
//                                if (gradeNumber == 1) {
//                                    suffix = "st";
//                                } else if (gradeNumber == 2) {
//                                    suffix = "nd";
//                                } else if (gradeNumber == 3) {
//                                    suffix = "rd";
//                                } else {
//                                    suffix = "th";
//                                }

//                                // Append the formatted grade and fees to the StringBuilder
//                                Specializationfees.append("\u2022 ")
//                                        .append(gradeNumber)
//                                        .append(suffix)
//                                        .append(" grade: ₹")
//                                        .append(fees)
//                                        .append("\n");


                                Specializationfees.append("\u2022 ").append(grade).append(":  ₹").append(fees).append("\n");
                            }

                            // Display the fees details in your TextView
                            // textView.setText(feesEnglishPrimaryDetails.toString());
                        } else {
                            // The fees data for English primary level is not available
                            binding.specializationFees.setVisibility(View.GONE);
                        }



                        binding.primaryEnglish.setText(feesEnglishPrimaryDetails.toString());
                        binding.primaryHindi.setText(feesHindiPrimaryDetails.toString());
                        binding.primaryMarathi.setText(feesmarathiPrimaryDetails.toString());

                        // Secondary

                        binding.sciencefees.setText(scienceFees.toString());
                        binding.commercefees.setText(commerceFees.toString());
                        binding.artsfees.setText(artsFees.toString());

                        binding.specialization.setText(Specializationfees.toString());


//                        binding.name.setText(coachingName);
//                        if (mission != null  && !mission.isEmpty()) {
//                            binding.vision.setText(mission);
//                            binding.missionLayout.setVisibility(View.VISIBLE);
//
//                        }
//                        else
//                        {
//                            binding.missionLayout.setVisibility(View.GONE);
//                        }
                        }
                        else {
                            binding.specializationFees.setVisibility(View.GONE);
                            binding.secondaryFeesArts.setVisibility(View.GONE);
                            binding.secondaryFeesCommerce.setVisibility(View.GONE);
                            binding.secondaryFees.setVisibility(View.GONE);
                            binding.primaryFeesMarathi.setVisibility(View.GONE);
                            binding.primaryFeesHindi.setVisibility(View.GONE);
                            binding.primaryFees.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

    private void CheckEnroll() {
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String number = snapshot.child("number").getValue(String.class);
                        FirebaseDatabase.getInstance().getReference().child("Coachings").child(cityLocality)
                                .child(key).child("admission").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren())
                                        {
                                            if(dataSnapshot.getValue().equals(name))
                                            {
                                                binding.enrollText.setText("Enrolled");
                                                enrollCheck = true;
                                                Log.d("enroll", "onDataChange: " + dataSnapshot.getKey());
                                                break;

                                            }
                                            else
                                            {
                                                binding.enrollText.setText("Enroll now");
                                                enrollCheck = false;

                                            }
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void showEnroll() {

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);
        // Set background drawable to make dialog transparent

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(dialogView, 0, 0, 0, 0);

        AlertDialog dialog = builder.create();
        dialog.setView(dialogView, 15, 0 , 15, 0);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayoutCompat agreeButton = dialogView.findViewById(R.id.Yes);
        LinearLayoutCompat cancelButton = dialogView.findViewById(R.id.No);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog when cancel button is clicked
            }
        });

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameUser= "";
                String numberUser= "";
                FirebaseDatabase.getInstance().getReference()
                        .child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = snapshot.child("name").getValue(String.class);
                                String number = snapshot.child("number").getValue(String.class);

                                HashMap<String, Object> userDetails = new HashMap<>();
                                userDetails.put(number, name);
//                                userDetails.put("number", number);

                                FirebaseDatabase.getInstance().getReference().child("Coachings").child(cityLocality)
                                        .child(key).child("admission").updateChildren(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Toast.makeText(coachingDetails.this, "Data shared", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(coachingDetails.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



            }
        });

        dialog.show();

    }
}