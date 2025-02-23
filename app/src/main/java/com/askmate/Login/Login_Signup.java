package com.askmate.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import com.askmate.R;
import com.askmate.databinding.ActivityLoginSignupBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

;

public class Login_Signup extends AppCompatActivity {
//String number;
    ActivityLoginSignupBinding binding;

    String codesent;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginSignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.WHITE);

        binding.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();

                String number = binding.phone.getText().toString();

                // Used for changing font style of prefix text  i.e (+91)
                Typeface typeface = ResourcesCompat.getFont(Login_Signup.this, R.font.nunito_sans_semibold);
//         Use for changing alternate phone number prefix (+91) details
                AppCompatTextView prefixView = binding.layout.findViewById(com.google.android.material.R.id.textinput_prefix_text);
                prefixView.setTextSize(19);
                prefixView.setTypeface(typeface);
                prefixView.setTextColor(Color.BLACK);

                prefixView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                prefixView.setGravity(Gravity.CENTER);
//                Toast.makeText(Login_Signup.this, number, Toast.LENGTH_SHORT).show();
                if (!number.isEmpty())
                {

                    if (number.length() < 10)
                    {

                        Snackbar.make(findViewById(android.R.id.content), "Please enter correct mobile number",
                                Snackbar.LENGTH_SHORT).show();
//                        Toast.makeText(Login_Signup.this, "Please enter correct mobile number", Toast.LENGTH_SHORT).show();
//
                        progressDialog.dismiss();


                    }
                    else
                    {
                        checkNumber(number);

                    }

                }
                else
                {
                    Snackbar.make(findViewById(android.R.id.content), "Please enter registered phone number",
                            Snackbar.LENGTH_SHORT).show();
//                    Toast.makeText(Login_Signup.this, "Please enter registered phone number", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();


                }
            }
        });


        binding.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_Signup.this, Phone_number_enter.class);
                startActivity(intent);
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

                        }

                        // Check if the number exists in the database
                        if (numberExists) {
//                            binding.layoutAlpha.setAlpha(1);
//                            binding.progressBar.setVisibility(View.GONE);
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Login(number);



                        } else {
//                            binding.layoutAlpha.setAlpha(1);
//                            binding.progressBar.setVisibility(View.GONE);
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressDialog.dismiss();
//                            Toast.makeText(Login_Signup.this, "Please sign up", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(android.R.id.content), "Account Not found, please sign up",
                                    Snackbar.LENGTH_SHORT).show();

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
                Login_Signup.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(Login_Signup.this, "Code Sent", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(Login_Signup.this, Login_Otp.class);
//                        intent.putExtra("number", number);
//                        startActivity(intent);
//                        Intent intent = new Intent(getApplicationContext(), otp_check.class);
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