package com.askmate.help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.askmate.R;


public class Contact_us extends AppCompatActivity {

    AppCompatEditText editText;
    LinearLayoutCompat linearLayoutCompat;

    ConstraintLayout layout;


    TextView etEmail;
    TextView etSubject;

    String email;
    String subject;
    String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        getWindow().setStatusBarColor(getResources().getColor(R.color.appcolor));


        editText = findViewById(R.id.edit_text);
        linearLayoutCompat = findViewById(R.id.submit);
//        layout = findViewById(R.id.constrain);

        etEmail = findViewById(R.id.etTo);
        etSubject = findViewById(R.id.etSubject);


        linearLayoutCompat.setEnabled(false);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().trim().length()==0){
                    linearLayoutCompat.setEnabled(false);
                } else {
                    linearLayoutCompat.setEnabled(true);
                }


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        linearLayoutCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });


    }

    private void sendEmail() {

        try {
            email = etEmail.getText().toString();
            subject = etSubject.getText().toString();
            message = editText.getText().toString();


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