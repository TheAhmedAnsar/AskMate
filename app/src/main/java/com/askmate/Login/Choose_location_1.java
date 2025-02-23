package com.askmate.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.askmate.R;

public class Choose_location_1 extends AppCompatActivity {

    TextView welCome;
    String name ;
    String uriString ;
    String uri = uriString; // Convert the URI string back to Uri object
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location1);

        getWindow().setStatusBarColor(Color.parseColor("#1B8BF2"));

welCome = findViewById(R.id.welcomeText);
//Intent intent = getIntent();
//String name = intent.getStringExtra("name");
//String welcomText = "Hii, Nice to meet you "+ name;
//nameText.setText(welcomText);

        Intent intent = getIntent();
         name = intent.getStringExtra("name");
         uriString = intent.getStringExtra("uri");
//        String uri = uriString; // Convert the URI string back to Uri object
         number = intent.getStringExtra("number");
String welComeText = "Hii, Nice to meet you " + name;
        welCome.setText(welComeText);
        LinearLayoutCompat linearLayout_jump_to_map = findViewById(R.id.map_jump);

        linearLayout_jump_to_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Choose_location_1.this, Choose_location_2.class);
                intent.putExtra("name", name);
                intent.putExtra("uri", uriString);
                intent.putExtra("number", number);
                startActivity(intent);

            }
        });
    }
}