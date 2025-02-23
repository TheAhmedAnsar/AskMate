package com.askmate.help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.askmate.R;
import com.askmate.databinding.ActivityFeatureRequestBinding;


public class Feature_request extends AppCompatActivity {
    String email;
    String subject;
    String message;
    ActivityFeatureRequestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityFeatureRequestBinding.inflate(getLayoutInflater());
       View view = binding.getRoot();
       setContentView(view);

        getWindow().setStatusBarColor(getResources().getColor(R.color.appcolor));


        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().trim().length()==0){
                    binding.submit.setEnabled(false);
                } else {
                    binding.submit.setEnabled(true);
                }


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });



    }

    private void sendEmail() {

        try {
            email = binding.etTo.getText().toString();
            subject = binding.etSubject.getText().toString();
            message = binding.editText.getText().toString();


            Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
            emailSelectorIntent.setData(Uri.parse("mailto:"));


            final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            emailIntent.setSelector(emailSelectorIntent);

            emailIntent.putExtra(Intent.EXTRA_TEXT, message);

            this.startActivity(Intent.createChooser(emailIntent, null));
            if( emailIntent.resolveActivity(getPackageManager()) != null )
                startActivity(emailIntent);



        } catch (Throwable t) {
            Toast.makeText(this, "Request failed try again: "+ t.toString(), Toast.LENGTH_LONG).show();

        }

    }

}