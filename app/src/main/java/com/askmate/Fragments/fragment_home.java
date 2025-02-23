package com.askmate.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.askmate.Adapter.Home_adapter;
import com.askmate.Location_set;
import com.askmate.Modal.Home_modal;
import com.askmate.R;
import com.askmate.setting_profile;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_home extends Fragment {
    List<Map.Entry<String, Double>> filteredSortedCoachings;
    LinearLayoutCompat Location, secondLayout;
    TextView address1, address2;
    CircleImageView circleImageView;
    ArrayList<String> locationName = new ArrayList<>();
    String cityLocality;



    ArrayList<String> coachingLocalKeys = new ArrayList<>();
    private static final String PREFS_NAME = "CoachingsNearbyData";
    private static final String KEYS_PREF = "CoachingNearbyKeys";
    private DatabaseHelper dbHelper;

    private static final double EARTH_RADIUS_KM = 6371.0;
    List<Map.Entry<String, Double>> nearbyCoachingsList = new ArrayList<>();
    List<Map.Entry<String, Double>> sqlitenearbyCoachingsList = new ArrayList<>();
    double latitude ;
    double longitude;
    DatabaseReference geoFireRef;

    ShimmerFrameLayout shimmerFrameLayout;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_home.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_home newInstance(String param1, String param2) {
        fragment_home fragment = new fragment_home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Location = view.findViewById(R.id.location_set);
        address1 = view.findViewById(R.id.address1);
        address2 = view.findViewById(R.id.address2);
        circleImageView = view.findViewById(R.id.setting_profile);
        SharedPreferences preferences = getActivity().getSharedPreferences("Location", MODE_PRIVATE);
        latitude = preferences.getFloat("latitude", 0.0f);  // Default value is 0.0 if not found
        longitude = preferences.getFloat("longitude", 0.0f);
        shimmerFrameLayout = view.findViewById(R.id.shimmer);

        secondLayout = view.findViewById(R.id.secondLayout);



        // profile getting
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (view.getContext() == null) {
                            return;
                        }
                        String imageProfile = snapshot.child("image").getValue(String.class);
                        if (getActivity() != null) {
                            Glide.with(getActivity()).load(imageProfile)
                                    .placeholder(R.drawable.user)
                                    .into(circleImageView);

                        }


                        Log.d("imageProfile", "onDataChange: " + imageProfile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), setting_profile.class);
                startActivity(intent);
            }
        });

        // Check if latitude and longitude are not 0 (indicating default values)
        if (latitude != 0f && longitude != 0f) {
            // Use Geocoder to get the locality from latitude and longitude
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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

        geoFireRef = FirebaseDatabase.getInstance().getReference()
                .child("Coachings")
                .child("GeoHashIndex")
                .child(cityLocality);

//        dbHelper = new DatabaseHelper(getActivity());
//        List<CoachingLocation> allLocations = dbHelper.getAllCoachingLocations();
//        Log.d("Size", "onCreateView: " + allLocations.size());

        if (cityLocality.equals("Mumbai")) {
//            for (CoachingLocation location : allLocations) {
//                double distance = calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());
//                sqlitenearbyCoachingsList.add(new AbstractMap.SimpleEntry<>(location.getKey(), distance));
////                System.out.println("This is key of coaching" + location.getKey());
//            }
//
//            if (!sqlitenearbyCoachingsList.isEmpty()) {
//                // Sort the list by distance
//                Collections.sort(sqlitenearbyCoachingsList, new Comparator<Map.Entry<String, Double>>() {
//                    @Override
//                    public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
//                        return entry1.getValue().compareTo(entry2.getValue());
//                    }
//                });
//            }


//            if (!dbHelper.getAllCoachingLocations().isEmpty()) {
////                Toast.makeText(getActivity(), "Firebase is empty", Toast.LENGTH_SHORT).show();
////                Collections.sort(sqlitenearbyCoachingsList, new Comparator<Map.Entry<String, Double>>() {
////                    @Override
////                    public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
////                        return entry1.getValue().compareTo(entry2.getValue());
////                    }
////                });
//                DisplaySqlLite();
//            } else {
////                Toast.makeText(getActivity(), "Database is empty", Toast.LENGTH_SHORT).show();
//
                Distancecheck();
//            }
        }
        else
        {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            secondLayout.setVisibility(View.VISIBLE);
        }



//        Distancecheck();

        shimmerFrameLayout.startShimmer();







        getAddressFromLocation(latitude, longitude);
        if (!locationName.isEmpty()) {
            String address___1 = locationName.get(0);
//            String address___2 = locationName.get(1) + " " + locationName.get(2)+ " " + locationName.get(3);
            String addresspart =locationName.get(1) ;

            if (locationName.get(0) != null &&!locationName.get(0).isEmpty())
            {
                address1.setText(locationName.get(0));

            }
            else
            {
                address1.setText(locationName.get(1));

            }
//            address1.setText(locationName.get(0));
            address2.setText(addresspart);

        }

//        geoFireRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                String key = dataSnapshot.getKey();
//                Double latitude = dataSnapshot.child("l/0").getValue(Double.class);
//                Double longitude = dataSnapshot.child("l/1").getValue(Double.class);
//
//                if (latitude != null && longitude != null) {
//                    dbHelper.addCoachingLocation(key, longitude, latitude);
//                    Log.d("GeoFire", "Child added: " + key);
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
////                Toast.makeText(getActivity(), "Data changed", Toast.LENGTH_SHORT).show();
//                String key = dataSnapshot.getKey();
//                Double latitude = dataSnapshot.child("l/0").getValue(Double.class);
//                Double longitude = dataSnapshot.child("l/1").getValue(Double.class);
//
//                if (latitude != null && longitude != null) {
//                    dbHelper.updateCoachingLocation(key, longitude, latitude);
//                    Log.d("GeoFire", "Child changed: " + key);
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
////                Toast.makeText(getActivity(), "Data removed", Toast.LENGTH_SHORT).show();
//
//                String key = dataSnapshot.getKey();
//                dbHelper.removeCoachingLocation(key);
//                Log.d("GeoFire", "Child removed: " + key);
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                // Handle child moved event if needed
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("GeoFire", "Error: " + databaseError.getMessage());
//            }
//        });

        Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Location_set.class);
                startActivity(intent);
            }
        });

        return view ;
    }

    private void Distancecheck() {
//        Toast.makeText(getActivity(), "Loading from Firebase", Toast.LENGTH_SHORT).show();


//                .child("GeoHash");
        Log.d("GeoFire", "Reference: " + geoFireRef.toString());
        GeoFire geoFire = new GeoFire(geoFireRef);
        double radius = 8.0;
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), radius); // Adjust the radius as needed
        Log.d("GeoFire", "Querying at location: " + latitude + ", " + longitude + " with radius: " + radius);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                // This method will be called when a nearby coaching center key is found
//                Log.d("Coaching", "Coaching Key: " + key + " entered the query area");
                double distance = calculateDistance(latitude, longitude, location.latitude, location.longitude);
                String formattedDistance = String.format("%.2f", distance);
//                Log.d("Coaching", "Distance: " + formattedDistance + " km");
                nearbyCoachingsList.add(new AbstractMap.SimpleEntry<>(key, distance));

            }

            @Override
            public void onKeyExited(String key) {
                // This method will be called when a nearby coaching center key exits the query area
//                Log.d("Coaching", "Coaching Key: " + key + " exited the query area");

                // Perform any cleanup or update operations if needed
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                // This method will be called when a nearby coaching center key moves within the query area
                // This might not be relevant if you're not interested in location updates
            }

            @Override
            public void onGeoQueryReady() {

                Log.d("newList", "Lets see----> : " + sqlitenearbyCoachingsList.toString());
//                double distance = calculateDistance(latitude, longitude, location.latitude, location.longitude);
//                String formattedDistance = String.format("%.2f", distance);
////                Log.d("Coaching", "Distance: " + formattedDistance + " km");
//                nearbyCoachingsList.add(new AbstractMap.SimpleEntry<>(key, distance));



//                List<Home_modal> coachingCentersList = new ArrayList<>();
                ArrayList<Home_modal> coachingCentersList = new ArrayList<>();
                // Read all values from the database

//                dbHelper.getAllCoachingLocations().toString();
//                 This method will be called when all initial data has been loaded and events have been fired for the initial data
                Collections.sort(nearbyCoachingsList, new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
                        return entry1.getValue().compareTo(entry2.getValue());
                    }
                });

                // Iterate through nearby coachings list
                for (final Map.Entry<String, Double> entry : nearbyCoachingsList) {
                    final String coachingKey = entry.getKey();
                    final double distance = entry.getValue();


                    // Fetch coaching name based on coaching key
                    FirebaseDatabase.getInstance().getReference().child("Coachings")
                            .child(cityLocality).child(coachingKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Fetch coaching center data
                                        String name = dataSnapshot.child("coaching_profiles").child("name").getValue(String.class);
//                                        String address = dataSnapshot.child("address").getValue(String.class);
                                        ArrayList<String> imageUrls = new ArrayList<>();




                                        DataSnapshot imagesSnapshot = dataSnapshot.child("coaching_profiles").child("images");
                                        for (DataSnapshot imageSnapshot : imagesSnapshot.getChildren()) {
                                            String imageUrl = imageSnapshot.getValue(String.class);
                                            imageUrls.add(imageUrl);
                                        }
//
//                                        // Create CoachingCenter object and add it to the list
                                        Home_modal coachingCenter = new Home_modal();
                                        coachingCenter.setClassname(name);
                                        coachingCenter.setUrls(imageUrls);
                                        coachingCenter.setKey(coachingKey);
//
                                        String distanceText;
//
                                        if (distance < 1) {
                                            // Convert distance to meters
                                            double distanceInMeters = distance * 1000;
                                            // Display distance in meters
                                            distanceText = String.format("%.0f meters away", distanceInMeters);
                                        } else {
                                            // Display distance in kilometers
                                            distanceText = String.format("%.2f kilometers away", distance);
                                        }
                                        Log.d("DistanceDebug", "Distance text: " + distanceText);
                                        coachingCenter.setDistance(distanceText);

                                        StringBuilder stringBuilder = new StringBuilder();

                                        // Retrieve data from the dataSnapshot and concatenate non-empty fields
//                                        String apartment = dataSnapshot.child("address").child("apartment").getValue(String.class);
                                        String block = dataSnapshot.child("address").child("block").getValue(String.class);
//                                        String area = dataSnapshot.child("address").child("area").getValue(String.class);
                                        String sublocal = dataSnapshot.child("address").child("sublocal").getValue(String.class);
//                                        String local = dataSnapshot.child("address").child("local").getValue(String.class);


                                        // Append non-empty fields in the desired sequence
//                                        if (apartment != null && !apartment.isEmpty()) {
//                                            stringBuilder.append(apartment).append(", ");
//                                        }
                                        if (block != null && !block.isEmpty()) {
                                            stringBuilder.append(block).append(", ");
                                        }
//                                        if (area != null && !area.isEmpty()) {
//                                            stringBuilder.append(area).append(", ");
//                                        }
                                        if (sublocal != null && !sublocal.isEmpty()) {
                                            stringBuilder.append(sublocal).append(", ");
                                        }
//                                        if (local != null && !local.isEmpty()) {
//                                            stringBuilder.append(local);
//                                        }

                                        // Remove the trailing comma and space if present
                                        String result = stringBuilder.toString().replaceAll(", $", "");
                                        coachingCenter.setAddress(result);

                                        coachingCentersList.add(coachingCenter);

//                                        if (coachingCentersList.size() == nearbyCoachingsList.size()) {
                                        if (!coachingCentersList.isEmpty()) {
                                            Home_adapter adapter = new Home_adapter(coachingCentersList, getActivity());
                                            shimmerFrameLayout.stopShimmer();
                                            shimmerFrameLayout.setVisibility(View.GONE);
                                            secondLayout.setVisibility(View.GONE);


                                            // Set the adapter to your RecyclerView
                                            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
                                            recyclerView.setVisibility(View.VISIBLE);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                            recyclerView.setAdapter(adapter);
                                        }
                                        else
                                        {
                                            shimmerFrameLayout.stopShimmer();
                                            shimmerFrameLayout.setVisibility(View.GONE);
                                            secondLayout.setVisibility(View.VISIBLE);
                                        }
                                        Log.d("string", "onDataChange: " + coachingCentersList.toString());

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("Coaching", "Error fetching coaching data for key " + coachingKey + ": " + databaseError.getMessage());
                                }
                            });

                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                // Handle any errors that occur during the geo query
                Log.e("Coaching", "GeoQuery error: " + error.getMessage());
            }
        });


        // Adding ChildEventListener to monitor changes in the child nodes


    }

    private void getAddressFromLocation(double latitude, double longitude) {
        LatLng savedLocation = new LatLng(latitude, longitude);
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String addressText = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(savedLocation.latitude, savedLocation.longitude, 1);


            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                Log.d("addressreal", "Location " + address.getAddressLine(1)+ " here we go  " + address.getAdminArea()+ " "
                        +address.getSubAdminArea()
                        +address.getLocality()+ " "+address.getThoroughfare()+ " "
                        + address.getSubLocality());
                locationName.add(address.getSubLocality());
                locationName.add(address.getLocality());
                locationName.add(address.getPostalCode());
                locationName.add(address.getCountryName());
//                locationName.add(address.getCountryName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private boolean isLocationInMumbai(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                return "Mumbai".equalsIgnoreCase(city);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to calculate distance between two locations using Haversine formula
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }


    // New Methods
    private void storeKeyLocally(String key, GeoLocation location) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> keysSet = sharedPreferences.getStringSet(KEYS_PREF, new HashSet<>());
        keysSet.add(key);
        editor.putStringSet(KEYS_PREF, keysSet);

        // Store the latitude and longitude as float values
        editor.putFloat(key + "_latitude", (float) location.latitude);
        editor.putFloat(key + "_longitude", (float) location.longitude);

        editor.apply();
    }


    public void DisplaySqlLite()
    {
//        Toast.makeText(getActivity(), "Loading from Database", Toast.LENGTH_SHORT).show();
//        Log.d("SQl", "DisplaySqlLite: " + sqlitenearbyCoachingsList.toString());
        ArrayList<Home_modal> coachingCentersList = new ArrayList<>();
        Map<String, Home_modal> coachingCentersMap = new LinkedHashMap<>();

//        Collections.sort(sqlitenearbyCoachingsList, new Comparator<Map.Entry<String, Double>>() {
//            @Override
//            public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
//                return entry1.getValue().compareTo(entry2.getValue());
//            }
//        });

//        Log.d("SQL", "DisplaySqlLite: " + sqlitenearbyCoachingsList.toArray().toString());

        // Filter and sort the list by distance
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//        List<Map.Entry<String, Double>> filteredSortedCoachings = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            filteredSortedCoachings = sqlitenearbyCoachingsList.stream()
//                    .filter(entry -> entry.getValue() <= 8.0)
//                    .sorted(Map.Entry.comparingByValue())
//                    .collect(Collectors.toList());
//        }
////        }

//        for (final Map.Entry<String, Double> entry : sqlitenearbyCoachingsList) {
//            final String coachingKey = entry.getKey();
//            final double distance = entry.getValue();
//
//
//            Log.d("cal", "Before: " + distance+ ": " + coachingKey);
//
//            FirebaseDatabase.getInstance().getReference().child("Coachings")
//                    .child(cityLocality).child(coachingKey).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            String name = snapshot.child("coaching_profiles").child("name").getValue(String.class);
//                            Log.d("cal", "after: " + distance+ ": " + coachingKey);
//
//                            Log.d("cal", "name---->  " + name);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//            // Filter out entries with a distance greater than 8 km
////            if (distance > 8.0) {
////                continue;
////            }
//
////
////            Log.d("Distance", "DisplaySqlLite: " + coachingKey + " " + distance);
////            // Fetch coaching name based on coaching key
////            Log.d("coachingKey", "This is key: " + coachingKey + " " + "ids");
////            Log.d("WOW", "before outside: " + distance);
//
//            // change here
////            FirebaseDatabase.getInstance().getReference().child("Coachings")
////                    .child(cityLocality).child(coachingKey).addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////
////                            Log.d("WOW", "Before inside: " + distance);
////                            if (dataSnapshot.exists()) {
////                                Log.d("WOW", "After inside: " + distance);
////                                // Fetch coaching center data
////                                String name = dataSnapshot.child("coaching_profiles").child("name").getValue(String.class);
////                                ArrayList<String> imageUrls = new ArrayList<>();
////                                DataSnapshot imagesSnapshot = dataSnapshot.child("coaching_profiles").child("images");
////
////                                for (DataSnapshot imageSnapshot : imagesSnapshot.getChildren()) {
////                                    String imageUrl = imageSnapshot.getValue(String.class);
////                                    imageUrls.add(imageUrl);
////                                }
//////
//////                                        // Create CoachingCenter object and add it to the list
////                                Home_modal coachingCenter = new Home_modal();
////                                coachingCenter.setClassname(name);
////                                coachingCenter.setUrls(imageUrls);
////                                coachingCenter.setKey(coachingKey);
//////
////                                String distanceText;
//////
////                                if (distance < 1) {
////                                    // Convert distance to meters
////                                    double distanceInMeters = distance * 1000;
////                                    // Display distance in meters
////                                    distanceText = String.format("%.0f meters away", distanceInMeters);
////                                } else {
////                                    // Display distance in kilometers
////                                    distanceText = String.format("%.2f kilometers away", distance);
////                                }
////                                Log.d("DistanceDebug", "Distance text: " + distanceText);
////                                coachingCenter.setDistance(distanceText);
////
////                                StringBuilder stringBuilder = new StringBuilder();
////
////                                // Retrieve data from the dataSnapshot and concatenate non-empty fields
//////                                        String apartment = dataSnapshot.child("address").child("apartment").getValue(String.class);
////                                String block = dataSnapshot.child("address").child("block").getValue(String.class);
//////                                        String area = dataSnapshot.child("address").child("area").getValue(String.class);
////                                String sublocal = dataSnapshot.child("address").child("sublocal").getValue(String.class);
//////                                        String local = dataSnapshot.child("address").child("local").getValue(String.class);
////
////
////                                // Append non-empty fields in the desired sequence
//////                                        if (apartment != null && !apartment.isEmpty()) {
//////                                            stringBuilder.append(apartment).append(", ");
//////                                        }
////                                if (block != null && !block.isEmpty()) {
////                                    stringBuilder.append(block).append(", ");
////                                }
//////                                        if (area != null && !area.isEmpty()) {
//////                                            stringBuilder.append(area).append(", ");
//////                                        }
////                                if (sublocal != null && !sublocal.isEmpty()) {
////                                    stringBuilder.append(sublocal).append(", ");
////                                }
//////                                        if (local != null && !local.isEmpty()) {
//////                                            stringBuilder.append(local);
//////                                        }
////
////                                // Remove the trailing comma and space if present
////                                String result = stringBuilder.toString().replaceAll(", $", "");
////                                coachingCenter.setAddress(result);
////                                coachingCentersMap.put(coachingKey, coachingCenter);
////                                coachingCentersList.add(coachingCenter);
////
////                                if (coachingCentersMap.size() == sqlitenearbyCoachingsList.size()) {
//////                                if (!coachingCentersList.isEmpty()) {
//////                                if (coachingCentersList.size() == sqlitenearbyCoachingsList.stream().filter(entry -> entry.getValue() <= 8.0).count())
////                                    {
////                                        ArrayList<Home_modal> coachingCentersList = new ArrayList<>(coachingCentersMap.values());
////                                        Home_adapter adapter = new Home_adapter(coachingCentersList);
////
////                                        shimmerFrameLayout.stopShimmer();
////                                        shimmerFrameLayout.setVisibility(View.GONE);
////                                        secondLayout.setVisibility(View.GONE);
////
////                                        // Set the adapter to your RecyclerView
////                                        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
////                                        recyclerView.setVisibility(View.VISIBLE);
////                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
////                                        recyclerView.setAdapter(adapter);
////                                        adapter.notifyDataSetChanged();
////
////                                    }
////
////                                }
////                                else
////                                {
////                                    shimmerFrameLayout.stopShimmer();
////                                    shimmerFrameLayout.setVisibility(View.GONE);
////secondLayout.setVisibility(View.VISIBLE);
////                                }
//////                                }
////
////                            }
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////                            Log.e("Coaching", "Error fetching coaching data for key " + coachingKey + ": " + databaseError.getMessage());
////                        }
////                    });
//
//        }

    }





}




//    No need to change this
//
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                // This method will be called when a nearby coaching center key is found
////                Log.d("Coaching", "Coaching Key: " + key + " entered the query area");
//                double distance = calculateDistance(latitude, longitude, location.latitude, location.longitude);
//                String formattedDistance = String.format("%.2f", distance);
////                Log.d("Coaching", "Distance: " + formattedDistance + " km");
//                nearbyCoachingsList.add(new AbstractMap.SimpleEntry<>(key, distance));
//
//                // Store the key locally or perform any desired operations
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//                // This method will be called when a nearby coaching center key exits the query area
//                Log.d("Coaching", "Coaching Key: " + key + " exited the query area");
//
//                // Perform any cleanup or update operations if needed
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//                // This method will be called when a nearby coaching center key moves within the query area
//                // This might not be relevant if you're not interested in location updates
//            }
//
//            @Override
//            public void onGeoQueryReady() {
////                List<Home_modal> coachingCentersList = new ArrayList<>();
//                ArrayList<Home_modal> coachingCentersList = new ArrayList<>();
//                Log.d("List", "onGeoQueryReady: " + nearbyCoachingsList.toString());
//                // This method will be called when all initial data has been loaded and events have been fired for the initial data
//                Collections.sort(nearbyCoachingsList, new Comparator<Map.Entry<String, Double>>() {
//                    @Override
//                    public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
//                        return entry1.getValue().compareTo(entry2.getValue());
//                    }
//                });
//
//                // Iterate through nearby coachings list
//                for (final Map.Entry<String, Double> entry : nearbyCoachingsList) {
//                    final String coachingKey = entry.getKey();
//                    final double distance = entry.getValue();
//
//                    final String formattedDistance = String.format("%.2f", distance);
//
//                    // Fetch coaching name based on coaching key
//                    FirebaseDatabase.getInstance().getReference().child("Coachings")
//                            .child(cityLocality).child(coachingKey).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//                                        // Fetch coaching center data
//                                        String name = dataSnapshot.child("coaching_profiles").child("name").getValue(String.class);
////                                        String address = dataSnapshot.child("address").getValue(String.class);
//                                        ArrayList<String> imageUrls = new ArrayList<>();
//
//                                        DataSnapshot admissionCheck = dataSnapshot.child("announcements");
//
//
//                                        DataSnapshot imagesSnapshot = dataSnapshot.child("coaching_profiles").child("images");
//                                        for (DataSnapshot imageSnapshot : imagesSnapshot.getChildren()) {
//                                            String imageUrl = imageSnapshot.getValue(String.class);
//                                            imageUrls.add(imageUrl);
//                                        }
////
////                                        // Create CoachingCenter object and add it to the list
//                                        Home_modal coachingCenter = new Home_modal();
//                                        coachingCenter.setClassname(name);
//                                        coachingCenter.setUrls(imageUrls);
//                                        coachingCenter.setKey(coachingKey);
////
//                                        String distanceText;
////
//                                        if (distance < 1) {
//                                            // Convert distance to meters
//                                            double distanceInMeters = distance * 1000;
//                                            // Display distance in meters
//                                            distanceText = String.format("%.0f meters away", distanceInMeters);
//                                        } else {
//                                            // Display distance in kilometers
//                                            distanceText = String.format("%.2f kilometers away", distance);
//                                        }
//                                        Log.d("DistanceDebug", "Distance text: " + distanceText);
//                                        coachingCenter.setDistance(distanceText);
//
//                                        StringBuilder stringBuilder = new StringBuilder();
//
//                                        // Retrieve data from the dataSnapshot and concatenate non-empty fields
////                                        String apartment = dataSnapshot.child("address").child("apartment").getValue(String.class);
//                                        String block = dataSnapshot.child("address").child("block").getValue(String.class);
////                                        String area = dataSnapshot.child("address").child("area").getValue(String.class);
//                                        String sublocal = dataSnapshot.child("address").child("sublocal").getValue(String.class);
////                                        String local = dataSnapshot.child("address").child("local").getValue(String.class);
//
//
//                                        // Append non-empty fields in the desired sequence
////                                        if (apartment != null && !apartment.isEmpty()) {
////                                            stringBuilder.append(apartment).append(", ");
////                                        }
//                                        if (block != null && !block.isEmpty()) {
//                                            stringBuilder.append(block).append(", ");
//                                        }
////                                        if (area != null && !area.isEmpty()) {
////                                            stringBuilder.append(area).append(", ");
////                                        }
//                                        if (sublocal != null && !sublocal.isEmpty()) {
//                                            stringBuilder.append(sublocal).append(", ");
//                                        }
////                                        if (local != null && !local.isEmpty()) {
////                                            stringBuilder.append(local);
////                                        }
//
//                                        // Remove the trailing comma and space if present
//                                        String result = stringBuilder.toString().replaceAll(", $", "");
//                                        coachingCenter.setAddress(result);
//
//                                        coachingCentersList.add(coachingCenter);
//
//                                        if (coachingCentersList.size() == nearbyCoachingsList.size()) {
//                                            Home_adapter adapter = new Home_adapter(coachingCentersList);
//                                            shimmerFrameLayout.stopShimmer();
//                                            shimmerFrameLayout.setVisibility(View.GONE);
//                                            // Set the adapter to your RecyclerView
//                                            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
//                                            recyclerView.setVisibility(View.VISIBLE);
//                                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//                                            recyclerView.setAdapter(adapter);
//                                        }
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    Log.e("Coaching", "Error fetching coaching data for key " + coachingKey + ": " + databaseError.getMessage());
//                                }
//                            });
//
//                }
//
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//                // Handle any errors that occur during the geo query
//                Log.e("Coaching", "GeoQuery error: " + error.getMessage());
//            }
//        });