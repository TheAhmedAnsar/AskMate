package com.askmate.help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.askmate.R;
import com.askmate.databinding.ActivityAboutUsBinding;

public class About_us extends AppCompatActivity {

    ActivityAboutUsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().setStatusBarColor(getResources().getColor(R.color.appcolor));

        // Get references to the ImageViews
//        AppCompatImageView instagramImageView = findViewById(R.id.instagramImageView);
//        AppCompatImageView linkedinImageView = findViewById(R.id.linkedinImageView);

        // Set click listeners
        binding.instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Instagram profile
                openInstagramProfile();
            }
        });

        binding.linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open LinkedIn profile
                openLinkedInProfile();
            }
        });
    }

    private void openInstagramProfile() {
        // Replace "your_instagram_username" with your actual Instagram username
        String instagramUrl = "https://www.instagram.com/askmate.official/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl));
        startActivity(intent);
    }

    private void openLinkedInProfile() {
        // Replace "your_linkedin_username" with your actual LinkedIn username
        String linkedinUrl = "https://www.linkedin.com/in/ansari-ahmed-4322b8229/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
        startActivity(intent);
    }


}