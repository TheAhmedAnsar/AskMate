package com.askmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.askmate.databinding.ActivityFulllScreenImageBinding;
import com.bumptech.glide.Glide;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;

public class FulllScreenImage extends AppCompatActivity {

    ActivityFulllScreenImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFulllScreenImageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().setStatusBarColor(getResources().getColor(R.color.appcolor));
        String url = getIntent().getStringExtra("url");

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Glide.with(this)
                .load(url)
                .into(binding.loadPost);






    }
}