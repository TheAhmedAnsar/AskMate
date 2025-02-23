package com.askmate.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.askmate.R;
import com.askmate.databinding.ActivityPhoneNumberEnterBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Phone_number_enter extends AppCompatActivity {
    ActivityPhoneNumberEnterBinding phonenoEnterBinding;
    String key;
    String codesent;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phonenoEnterBinding = ActivityPhoneNumberEnterBinding.inflate(getLayoutInflater());

        View view = phonenoEnterBinding.getRoot();
        setContentView(view);

        getWindow().setStatusBarColor(Color.parseColor("#1B8BF2"));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        AppCompatButton button = findViewById(R.id.Button);

        phonenoEnterBinding.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String number = phonenoEnterBinding.number.getText().toString();
                if (number.isEmpty() || number.length() < 10)
                {
                    progressDialog.dismiss();
                    Toast.makeText(Phone_number_enter.this, "Please enter correct details", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    checkNumber(number);


                }

            }
        });

    }
    private void checkNumber(String number) {

//        Toast.makeText(this,  number, Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference().child("askmate")
                .child("register")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean numberExists = false;

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            // Check if the child key (phone number) matches the desired number
                            if (childSnapshot.getKey().equals( number)) {
                                numberExists = true;
                                break; // Number found, exit the loop
                            }
//                            else
//                            {
////                                Snackbar.make(findViewById(android.R.id.content), "Account Not found, please sign up", Snackbar.LENGTH_SHORT).show();
//
////                                Toast.makeText(Login_Signup.this, "Not found", Toast.LENGTH_SHORT).show();
//                            }
                        }

                        // Check if the number exists in the database
                        if (numberExists) {
                            progressDialog.dismiss();

//                            phonenoEnterBinding.layoutAlpha.setAlpha(1);
//                            phonenoEnterBinding.progressBar.setVisibility(View.GONE);
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                            Login(number);
                            Snackbar.make(findViewById(android.R.id.content), "Account found, please log in",
                                    Snackbar.LENGTH_SHORT).show();


                        } else {
//                            phonenoEnterBinding.layoutAlpha.setAlpha(1);
//                            phonenoEnterBinding.progressBar.setVisibility(View.GONE);
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Login(number);
//                            Toast.makeText(Login_Signup.this, "Please sign up", Toast.LENGTH_SHORT).show();
//                            Snackbar.make(findViewById(android.R.id.content), "Account Not found, please sign up",
//                                    Snackbar.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    public void  Login(String phonenumber)
    {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phonenumber,
                60,
                TimeUnit.SECONDS,
                Phone_number_enter.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(Phone_number_enter.this, "Code Sent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Something went wrong, please try after sometime", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);


                        PhoneAuthProvider.ForceResendingToken resendToken  = forceResendingToken;
                        codesent = s;
//                                            Toast.makeText(phone_enter_login.this, phonenumber, Toast.LENGTH_SHORT).show();
//
//                                            Toast.makeText(phone_enter_login.this, codesent, Toast.LENGTH_SHORT).show();
////
                        progressDialog.dismiss();

                        Intent intent = new Intent(getApplicationContext(), Otp_check.class);
                        intent.putExtra("mobile", phonenumber);
                        intent.putExtra("otp", codesent);
                        intent.putExtra("token", resendToken);

//
//                                    progressBar.setVisibility(GONE);
//                                    constraintLayout.setAlpha(1);
                        startActivity(intent);
                        finish();

                    }
                }
        );


    }


}