package com.askmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LocationAndTokenTask implements Runnable {
    private Context context;
    String cityLocality = null;


    public LocationAndTokenTask(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("newTag", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get the device token
                        String token = task.getResult();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("deviceToken", token);
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(hashMap);
                        // Log.d("token", "FCM Token: " + token);
                    });

    }
}
