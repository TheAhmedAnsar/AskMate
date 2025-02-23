package com.askmate.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.askmate.Location_set;
import com.askmate.Login.Choose_location_2;
import com.askmate.Login.Contact_details;
import com.askmate.Login.Login_Signup;
import com.askmate.MainActivity;
import com.askmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        SharedPreferences preferences = getSharedPreferences("home", Context.MODE_PRIVATE);
        SharedPreferences mapPreference = getSharedPreferences("Location", Context.MODE_PRIVATE);
        boolean isHomeSet = preferences.getBoolean("home", false);
        boolean isMapOk = mapPreference.getBoolean("mapOk", false);
        if(!isInternetConnected())
        {
            Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }
        else {

            Thread thread = new Thread() {
                public void run() {
                    try {
                        sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                        Intent intent;
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser == null) {
                            intent = new Intent(SplashScreen.this, Login_Signup.class);
                        } else if (isHomeSet) {

                            if (isMapOk) {
//                                intent = new Intent(SplashScreen.this, MainActivity.class);
                                intent = new Intent(SplashScreen.this, MainActivity.class);
                            } else {
                                intent = new Intent(SplashScreen.this, Location_set.class);

                            }
                        } else {
                            intent = new Intent(SplashScreen.this, Login_Signup.class);
                        }
//                                            intent = new Intent(SplashScreen.this, Location_set.class);

                        startActivity(intent);
                        finish();
//
//                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//
//
//                        Intent intent = new Intent(SplashScreen.this, Choose_location_2.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//
//                        Intent intent = new Intent(SplashScreen.this, Login_Signup.class);
//                        startActivity(intent);
//                        finish();
//                    }

                    }
                }

            };
            thread.start();
        }
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}
