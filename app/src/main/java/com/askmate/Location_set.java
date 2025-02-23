package com.askmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class Location_set extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnMapClickListener
        , GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {


    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    LatLng center;
    private boolean isDragging = false;
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


        sublocal_name = findViewById(R.id.sublocal);
        city_name = findViewById(R.id.address);
        country_name = findViewById(R.id.countryname);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        current_locate = findViewById(R.id.current_location);
        // Assuming you have a layout with a fragment container where the map will be placed
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);

//        fetchLocation();


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
//                LatLng currentLocation = googleMap.getCameraPosition().target;
                center = googleMap.getCameraPosition().target;

//                ArrayList<String> locationName = getLocationName(center);

                if (sublocal_name != null && !sublocal_name.getText().toString().trim().isEmpty() &&
                        !sublocal_name.getText().toString().trim().equalsIgnoreCase("null") &&
                        country_name != null && !country_name.getText().toString().trim().isEmpty() &&
                        !country_name.getText().toString().trim().equalsIgnoreCase("null") &&
                        city_name != null && !city_name.getText().toString().trim().isEmpty() &&
                        !city_name.getText().toString().trim().equalsIgnoreCase("null"))
                {
                    saveLocationInSharedPreferences(center.latitude, center.longitude);
                    SharedPreferences preferences = getSharedPreferences("Location", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("mapOk", true);
                    boolean check = editor.commit();
                    if (check) {
                        ProgressDialog progressDialog = new ProgressDialog(Location_set.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.setCancelable(false); // Prevent dismissing by tapping outside
                        progressDialog.show();
                        Intent intent = new Intent(Location_set.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(Location_set.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(Location_set.this, "Please choose different location", Toast.LENGTH_SHORT).show();
                }


            }
        });

        fetchLocation();
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
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        getLastLocation();
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            if (marker != null) {
                                marker.setPosition(latLng);
                            } else {
                                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here!"));
                            }

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

                            ArrayList<String> locationName = getLocationName(latLng);

                            System.out.println(locationName);
//                            sublocal_name.setText(locationName.get(0));
                            if (locationName.get(0).isEmpty())
                            {
                                sublocal_name.setText(locationName.get(1));

                            }
                            else
                            {
                                sublocal_name.setText(locationName.get(0));

                            }
                            String addresspart = locationName.get(1) + ", " + locationName.get(2);
                            city_name.setText(addresspart);
                            country_name.setText(locationName.get(3));
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
                Log.d("addressreal", "getLocationName: " + address.getAddressLine(0) + " " + address.getLocality() + " "
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




    @Override
    public void onCameraMove() {

         center = googleMap.getCameraPosition().target;
        Log.d("onCameraMove", "onCameraMove: " + center);

        if (marker != null) {
            marker.setPosition(center);

        } else {
            marker = googleMap.addMarker(new MarkerOptions().position(center).title("You are here!"));
        }

//        // Update address information...
//        ArrayList<String> locationName = getLocationName(center);
//        sublocal_name.setText(locationName.get(0));
//        String addresspart = locationName.get(1) + ", " + locationName.get(2);
//        city_name.setText(addresspart);
//        country_name.setText(locationName.get(3));
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
//        if (isDragging) {
//            isDragging = false;
//
//        }
//        ArrayList<String> locationName = getLocationName(center);
//
        ArrayList<String> locationName = getLocationName(center);

        if (locationName != null && !locationName.isEmpty()) {
            // Ensure locationName has at least one element before accessing index 0
            if (locationName.get(0) != null && !locationName.get(0).isEmpty())
            {
                sublocal_name.setText(locationName.get(0));

            }
            else
            {
                sublocal_name.setText(locationName.get(1));

            }

            // Ensure locationName has at least three elements before accessing indices 1 and 2
            if (locationName.size() > 2) {
                String addresspart = locationName.get(1) + ", " + locationName.get(2);
                city_name.setText(addresspart);
            } else {
                // Handle the case where there are not enough elements
                city_name.setText("Address not found");
            }

            // Ensure locationName has at least four elements before accessing index 3
            if (locationName.size() > 3) {
                country_name.setText(locationName.get(3));
            } else {
                // Handle the case where there are not enough elements
                country_name.setText("Country not found");
            }
        } else {
            // Handle the case where locationName is empty or null
            sublocal_name.setText("We don't support at this location!");
            city_name.setText("");
            country_name.setText("");
        }

//        if (locationName != null && !locationName.isEmpty()) {
//            sublocal_name.setText(locationName.get(0));
//        } else {
//            // Handle the case where locationName is empty or null
//            sublocal_name.setText("Location not found");
//        }
//
//        String addresspart = locationName.get(1) + ", " + locationName.get(2);
//        city_name.setText(addresspart);
//        country_name.setText(locationName.get(3));
//        Toast.makeText(this, locationName.get(3), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == REASON_GESTURE) {
            isDragging = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app requires location access to show your current location. Kindly manually give permission to AskMate")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                ActivityCompat.requestPermissions(Location_set.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE) {
//            // If request is cancelled, the result arrays are empty
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission was granted
//                fetchLocation();
//            } else {
//                // Permission was denied
//                Toast.makeText(this, "Permission denied. Unable to get location.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    @Override
//    public void onCameraMove() {
//        LatLng currentCenter = googleMap.getCameraPosition().target;
//        if (!currentCenter.equals(previousCenter)) {
//            isDragging = true;
//        }
//        previousCenter = currentCenter;
//    }
}