package com.askmate;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.askmate.databinding.ActivityUpdateBinding;

public class Update extends AppCompatActivity {

    ActivityUpdateBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = ActivityUpdateBinding.inflate(R.layout.activity_update, this, false);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().setStatusBarColor(Color.parseColor("#1B8BF2"));

        // Handle Close App button
        binding.closeApp.setOnClickListener(v -> {
            this.finishAffinity();  // Close the app
        });


        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                intent.setPackage("com.android.vending"); // This ensures the Play Store app opens directly
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        super.onBackPressed();
        finishAffinity();
    }
}