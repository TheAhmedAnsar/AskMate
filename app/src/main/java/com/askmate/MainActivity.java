package com.askmate;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.askmate.Fragments.Fragment_connect;
import com.askmate.Fragments.fragment_QnA;
import com.askmate.Fragments.fragment_home;
import com.askmate.Fragments.fragment_news_feed;
import com.askmate.Fragments.fragment_profile;
import com.askmate.Modal.Question;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity  implements OnItemActionListener{
    BottomNavigationView bottomNavigationView;
    CustomViewPager viewPagerrrrr;
//    ViewPager viewPagerrrrr;
    ViewPagerAdapter viewPagerAdapter;

    private static final String LOCAL_VERSION = "2.1.0";  // Local version of your app
    private DatabaseReference versionRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.parseColor("#1B8BF2"));

        viewPagerrrrr = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        setupViewPager(0);
        viewPagerrrrr.setPagingEnabled(false);
        viewPagerrrrr.setOffscreenPageLimit(0);

        int navigationTab = getIntent().getIntExtra("NAVIGATION_TAB", 0);

        bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(navigationTab).getItemId());

        viewPagerrrrr.setCurrentItem(navigationTab);
        viewPagerrrrr.setOffscreenPageLimit(viewPagerAdapter.getCount()-1);
        // Initialize the ExecutorService
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Execute the background task
        executorService.execute(new LocationAndTokenTask(this));


//         Subscribe to "news" topic
        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to news topic";
                        if (!task.isSuccessful()) {
                            msg = "Subscription failed";
                        }
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

// subscribe to questions topic
        FirebaseMessaging.getInstance().subscribeToTopic("questions")
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed to questions topic";
                    if (!task.isSuccessful()) {
                        msg = "Subscription to questions topic failed";
                    }
//                    Log.d(TAG, msg);
//                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        versionRef = database.getReference("askmate").child("version").child("Coaching");

        // Initialize your versionRef here

        // Run the version check in a background thread
        ExecutorService versionService = Executors.newSingleThreadExecutor();
      String uid = FirebaseAuth.getInstance().getUid().toString();

      if (!uid.equals("9vfOQCsLvNbzf9hnh2rsLSx2QhM2"))
        {
            versionService.execute(() -> checkVersion());

        }


        viewPagerrrrr.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Optionally handle scroll events here
            }

            @Override
            public void onPageSelected(int position) {
                // Update BottomNavigationView based on ViewPager position
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.Home);
//                                                              Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.QnA);
//                                                              Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();

                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.connect_menu);
                        break;
//                                                              Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();

                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.News);
//                                                              Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        bottomNavigationView.setSelectedItemId(R.id.Settings);
//                                                              Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();

                        break;
//                    case 4:
//                        bottomNavigationView.setSelectedItemId(R.id.person);
//                        break;
                    // Add other cases for more items
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Optionally handle page scroll state changes here
            }
        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.Home :
                        viewPagerrrrr.setCurrentItem(0);
//                        Toast.makeText(MainActivity.this, "Likes", Toast.LENGTH_SHORT).show();
                        break;

                    case  R.id.QnA:
                        viewPagerrrrr.setCurrentItem(1);
                        break;
//

                    case R.id.connect_menu:
                        viewPagerrrrr.setCurrentItem(2);
//                        Toast.makeText(MainActivity.this, "Likes", Toast.LENGTH_SHORT).show();
                        break;


//
                    case R.id.News:
                        viewPagerrrrr.setCurrentItem(3);
                        break;


                    case R.id.Settings:
                        viewPagerrrrr.setCurrentItem(4);
                        break;
//


//                    case R.id.person:
//                        viewPagerrrrr.setCurrentItem(4);
//                        break;


                }

                return true;
            }
        });


    }

    private void setupViewPager(int pageNumber) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new fragment_home(), "", "Home");
        viewPagerAdapter.add(new fragment_QnA(), "", "QnA");
        viewPagerAdapter.add(new Fragment_connect(), "", "Connect");
        viewPagerAdapter.add(new fragment_news_feed(), "", "News");
        viewPagerAdapter.add(new fragment_profile(), "", "Profile");
        viewPagerrrrr.setAdapter(viewPagerAdapter);
        viewPagerrrrr.setCurrentItem(pageNumber);
    }



    @Override
    public void onBackPressed() {
//        ViewPager viewPager = findViewById(R.id.viewPager);
        if (viewPagerrrrr.getCurrentItem() > 0) {
            // Go to the previous fragment
            viewPagerrrrr.setCurrentItem(viewPagerrrrr.getCurrentItem() - 1);
//            Toast.makeText(this, viewPagerrrrr.getCurrentItem(), Toast.LENGTH_SHORT).show();
            Log.d("back", "onBackPressed: " + viewPagerrrrr.getCurrentItem());
            bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(viewPagerrrrr.getCurrentItem()).getItemId());
        } else {
            // Optionally, finish the activity or handle back press differently if at the first fragment
            super.onBackPressed();
        }
    }
    private int getPageIndexForFragment(String fragment) {
        switch (fragment) {
            case "News":
                return 0; // Index for news fragment
            case "QnA":
                return 1; // Index for Q&A fragment
            default:
                return 0; // Default to news if unknown
        }
    }

    @Override
    public void onDeleteItem(Question questionModal) {

    }


    private void checkVersion() {
        versionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String remoteVersion = dataSnapshot.getValue(String.class);
                Log.d("version", "onDataChange: " + remoteVersion + " <------ version");

                if (!remoteVersion.equals(LOCAL_VERSION)) {
                    // Switch to the main thread to start the activity
                    runOnUiThread(() -> {
                        Intent intent = new Intent(MainActivity.this, Update.class);
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

}