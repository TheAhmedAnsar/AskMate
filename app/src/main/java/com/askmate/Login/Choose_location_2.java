package com.askmate.Login;

import static com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.askmate.MainActivity;
import com.askmate.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Choose_location_2 extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {

    String name ;
    String uriString ;
    String uri = uriString; // Convert the URI string back to Uri object
    String number;
    LatLng center;

    private boolean isDragging = false;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private static final float DEFAULT_ZOOM_LEVEL = 18.0f;
    private static final LatLng DEFAULT_CAMERA_POSITION = new LatLng(20.5937, 78.9629); // Center of India
    private Handler handler = new Handler();
    LinearLayoutCompat current_locate;
    private Marker marker;
    private GoogleMap googleMap;
    FusedLocationProviderClient fusedLocationClient;
    AppCompatButton button;
    SupportMapFragment supportMapFragment;
    TextView sublocal_name, city_name, state_name, country_name;
    MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location2);


        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        uriString = intent.getStringExtra("uri");
//        String uri = uriString; // Convert the URI string back to Uri object
        number = intent.getStringExtra("number");

        sublocal_name = findViewById(R.id.sublocal);
        city_name = findViewById(R.id.address);
        country_name = findViewById(R.id.countryname);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        current_locate = findViewById(R.id.current_location);
        // Assuming you have a layout with a fragment container where the map will be placed
         supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);

        fetchLocation();

        current_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fetchLocation();

            }
        });

        button = findViewById(R.id.Button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((user_details) requireActivity()).InicatorProgress(10);//
//                uploadImage();
                LatLng currentLocation = googleMap.getCameraPosition().target;
                ArrayList<String> locationName = getLocationName(currentLocation);

                if (sublocal_name != null && !sublocal_name.getText().toString().trim().isEmpty() &&
                        !sublocal_name.getText().toString().trim().equalsIgnoreCase("null") &&
                        country_name != null && !country_name.getText().toString().trim().isEmpty() &&
                        !country_name.getText().toString().trim().equalsIgnoreCase("null") &&
                        city_name != null && !city_name.getText().toString().trim().isEmpty() &&
                        !city_name.getText().toString().trim().equalsIgnoreCase("null"))
                {
                    saveLocationInSharedPreferences(currentLocation.latitude, currentLocation.longitude);
                    Intent intent = new Intent(Choose_location_2.this , uploadAlldata.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("name", name);
                    intent.putExtra("uri", uriString);
                    intent.putExtra("number", number);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(Choose_location_2.this, "Please choose different location", Toast.LENGTH_SHORT).show();
                }
//




            }
        });

    }

    private void saveLocationInSharedPreferences(double latitude, double longitude) {
        SharedPreferences preferences = getSharedPreferences("Location", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("latitude", (float) latitude);
        editor.putFloat("longitude", (float) longitude);
        editor.apply();
    }


    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
             this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
//                    Toast.makeText(Choose_location_2.this, currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                            LatLng latLng = new LatLng(19.172251, 72.878154);

//                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here!");


                            if (marker != null) {
                                marker.setPosition(latLng);
                            } else {
                                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here!"));
                            }

//                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//                            googleMap.addMarker(markerOptions);


                            ArrayList<String> locationName = getLocationName(latLng);
//                            saveLocationInSharedPreferences(latLng.latitude, latLng.longitude);

                            System.out.println(locationName);
                            if (locationName.get(0).isEmpty())
                            {
                                sublocal_name.setText(locationName.get(1));

                            }
                            else
                            {
                                sublocal_name.setText(locationName.get(0));

                            }
                            String addresspart =locationName.get(1) + ", "+ locationName.get(2) ;
                            city_name.setText(addresspart);
                            country_name.setText(locationName.get(3));
//                            Toast.makeText(getActivity(), locationName, Toast.LENGTH_SHORT).show();



                        }
                    });
                }
            }
        });
    }


    private ArrayList<String> getLocationName(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        ArrayList<String> locationName = new ArrayList<>();

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                Log.d("address", "getLocationName: " + address.toString());
                Log.d("addressreal", "getLocationName: " + address.getAddressLine(0)+ " " + address.getLocality()+ " "
                        + address.getSubLocality());
//                locationName = address.getLocality(); // You can choose other address components as needed
                locationName.add(address.getSubLocality());
                locationName.add(address.getLocality());
                locationName.add(address.getAdminArea());
                locationName.add(address.getCountryName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locationName;
    }




    // just Changed
    @Override
    public void onCameraMove() {

        center = googleMap.getCameraPosition().target;
        Log.d("onCameraMove", "onCameraMove: " + center);

        if (marker != null) {
            marker.setPosition(center);

        } else {
            marker = googleMap.addMarker(new MarkerOptions().position(center).title("You are here!"));
        }

    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        moveCameraToLocation(latLng);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setOnCameraMoveListener(this); // Focus on India
        googleMap.setOnCameraIdleListener(this); //

        LatLngBounds indiaBounds = new LatLngBounds(
                new LatLng(8.4, 68.1), // Southwest corner of India
                new LatLng(37.6, 97.4)  // Northeast corner of India
        );

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_CAMERA_POSITION, DEFAULT_ZOOM_LEVEL));

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    private void moveCameraToLocation(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL));
        ArrayList<String> locationName = getLocationName(latLng);
        Log.d("locationName", "onCameraMove: " + locationName);
        System.out.println(locationName);
        sublocal_name.setText(locationName.get(0));
        String addresspart =locationName.get(1) + ", "+ locationName.get(2) ;
        city_name.setText(addresspart);
        country_name.setText(locationName.get(3));
    }


    @Override
    public void onCameraIdle() {
        ArrayList<String> locationName = getLocationName(center);
        if (locationName != null && !locationName.isEmpty()) {
            String subLocalName = locationName.size() > 0 ? locationName.get(0) : null;
            String cityNamePart1 = locationName.size() > 1 ? locationName.get(1) : null;
            String cityNamePart2 = locationName.size() > 2 ? locationName.get(2) : null;
            String countryName = locationName.size() > 3 ? locationName.get(3) : null;

            if (subLocalName == null || subLocalName.isEmpty()) {
                sublocal_name.setText(cityNamePart1 != null ? cityNamePart1 : "Unknown");
            } else {
                sublocal_name.setText(subLocalName);
            }

            if (cityNamePart1 != null && cityNamePart2 != null) {
                String addressPart = cityNamePart1 + ", " + cityNamePart2;
                city_name.setText(addressPart);
            } else {
                city_name.setText("Unknown");
            }

            if (countryName != null) {
                country_name.setText(countryName);
//                Toast.makeText(this, countryName, Toast.LENGTH_SHORT).show();
            } else {
                country_name.setText("Unknown");
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where locationName is null or empty
            sublocal_name.setText("Unknown");
            city_name.setText("Unknown");
            country_name.setText("Unknown");
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == REASON_GESTURE) {
            isDragging = true;
        }
    }


}