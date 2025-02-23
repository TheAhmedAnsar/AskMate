package com.askmate.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.askmate.Modal.Home_modal;
import com.askmate.R;
import com.askmate.SliderAdapterExample;
import com.askmate.SliderItem;
import com.askmate.coachingDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Home_adapter extends RecyclerView.Adapter<Home_adapter.ViewHolder>{
     ArrayList<Home_modal> Details;
    boolean checkAdmissionLayout = false;
    private Context context;
    private String cityLocality;
    float latitude ;  // Default value is 0.0 if not found
    float longitude;

    public Home_adapter(ArrayList<Home_modal> details, Context context) {
        Details = details;
        this.context = context;
    }

    @NonNull
    @Override
    public Home_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.coaching_struture, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.name.setText(Details.get(position).getClassname());
        holder.address.setText(Details.get(position).getAddress());
        holder.distance.setText(Details.get(position).getDistance());
        ArrayList<String> imageUrls = Details.get(position).getUrls();
        holder.bind(imageUrls);
        String coachingKey = Details.get(position).getKey();
        SharedPreferences preferences = context.getSharedPreferences("Location", MODE_PRIVATE);
        latitude = preferences.getFloat("latitude", 0.0f);  // Default value is 0.0 if not found
         longitude = preferences.getFloat("longitude", 0.0f);

        if (latitude != 0f && longitude != 0f) {
            // Use Geocoder to get the locality from latitude and longitude
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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



        DatabaseReference offersRef = FirebaseDatabase.getInstance().getReference()
                .child("Coachings").child(cityLocality)
                .child(coachingKey).child("offers");

        offersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.offerLayout.setVisibility(View.VISIBLE);
                    String text = snapshot.child("detail").getValue(String.class);
                    holder.Offer.setText(text);
                    Log.d("text", "Offer: " + text);
                }
                else
                {
                    holder.offerLayout.setVisibility(View.GONE);
                    holder.Offer.setText("");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference announcementsRef = FirebaseDatabase.getInstance().getReference()
                .child("Coachings").child(cityLocality)
                .child(coachingKey).child("announcements");

        announcementsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    // "announcements" node exists, show the view
                    holder.admissionLayout.setVisibility(View.VISIBLE);
                } else {
                    // "announcements" node does not exist, hide the view
                    holder.admissionLayout.setVisibility(View.GONE);
                }

//                if (snapshot.exists()) {
//                    for (DataSnapshot announcementSnapshot : snapshot.getChildren()) {
//
//                        String announcementTitle = announcementSnapshot.getValue(String.class);
//
//                        // Check if the announcement is "Admission open"
//                        if ("Admission open".equals(announcementTitle)) {
//                            checkAdmissionLayout = true;
////                            AdmissionKey = announcementKey;
//                        }
//
//                        if (checkAdmissionLayout)
//                        {
////                            Log.d("newKey", "announcemetEnrollDetails: " +AdmissionKey);
////
////                            announcemetEnrollDetails2(AdmissionKey);
//                            holder.admissionLayout.setVisibility(View.VISIBLE);
//
//                            break;
//
//                        }
//                        else
//                        {
//                            holder.admissionLayout.setVisibility(View.GONE);
//
//                        }
//                    }
//
//                } else {
//                    holder.admissionLayout.setVisibility(View.GONE);
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(holder.itemView.getContext(), Details.get(position).getKey(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(holder.itemView.getContext(), coachingDetails.class);
                intent.putExtra("key",  Details.get(position).getKey());
                Log.d("letsKey", "onClick: " + Details.get(position).getKey());
                holder.itemView.getContext().startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return Details.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView name , address, distance, Offer;
        SliderView sliderView;
        LinearLayoutCompat admissionLayout, offerLayout;

        SliderAdapterExample adapter ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            offerLayout = itemView.findViewById(R.id.offerLayout);
            sliderView = itemView.findViewById(R.id.imageSlider);
            name = itemView.findViewById(R.id.class_name);
            address = itemView.findViewById(R.id.class_address);
            distance = itemView.findViewById(R.id.class_distance);
            admissionLayout = itemView.findViewById(R.id.admissionOpen);
            Offer = itemView.findViewById(R.id.textOffer);
        }


        // Bind data to views and update the slider adapter
        public void bind(ArrayList<String> imageUrls) {
            ArrayList<SliderItem> sliderDataArrayList = new ArrayList<>();

            for (String imageUrl : imageUrls) {
                sliderDataArrayList.add(new SliderItem(imageUrl));
            }

            // Urls of our images.
//            String url1 = "https://www.geeksforgeeks.org/wp-content/uploads/gfg_200X200-1.png";
//            String url2 = "https://qphs.fs.quoracdn.net/main-qimg-8e203d34a6a56345f86f1a92570557ba.webp";
//            String url3 = "https://bizzbucket.co/wp-content/uploads/2020/08/Life-in-The-Metro-Blog-Title-22.png";

            // Update the slider adapter with the provided URL
//            ArrayList<SliderItem> sliderDataArrayList = new ArrayList<>();
//
//            sliderDataArrayList.add(new SliderItem(url1));
//            sliderDataArrayList.add(new SliderItem(url2));
//            sliderDataArrayList.add(new SliderItem(url3));
             adapter = new SliderAdapterExample( sliderDataArrayList);
            sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
            sliderView.setSliderAdapter(adapter);
            sliderView.setScrollTimeInSec(3);
            sliderView.setAutoCycle(true);
            sliderView.startAutoCycle();
        }
    }

}
