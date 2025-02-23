package com.askmate.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.askmate.R;
import com.askmate.databinding.ActivityOtpCheckBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Otp_check extends AppCompatActivity {

    ActivityOtpCheckBinding activityOtpCheckBinding;
    String number, enteredcode;
    PhoneAuthProvider.ForceResendingToken resendToken;

    ProgressDialog progressDialog;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_otp_check);

        activityOtpCheckBinding = ActivityOtpCheckBinding.inflate(getLayoutInflater());
        View view = activityOtpCheckBinding.getRoot();
        setContentView(view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        getWindow().setStatusBarColor(Color.parseColor("#1B8BF2"));
        getWindow().setStatusBarColor(Color.parseColor("#1B8BF2"));

        startCountDownTimer();
        Intent intent = getIntent();
        number = intent.getStringExtra("mobile");

        String OwnerNumber = "+91" + number;
        activityOtpCheckBinding.phonenumber.setText(OwnerNumber);

        activityOtpCheckBinding.otpRead.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activityOtpCheckBinding.otpRead.isFocusedByDefault();
        }


        Intent intent21 = getIntent();
        if (intent != null) {
            resendToken = intent21.getParcelableExtra("token");
        }

        enteredcode = getIntent().getStringExtra("otp");

        activityOtpCheckBinding.resendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trigger OTP resend process
                // Disable the button
                activityOtpCheckBinding.resendOtpButton.setEnabled(false);
                // Start the countdown timer
                startCountDownTimer();
                // Call the method to resend OTP
                resendOTP(number);
            }
        });
        activityOtpCheckBinding.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String optnumber = activityOtpCheckBinding.otpRead.getText().toString();

                if (!optnumber.isEmpty()) {
                    progressDialog.show();

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(enteredcode, optnumber);

                    signInWithPhoneAuthCredential(phoneAuthCredential);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Otp_check.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                activityOtpCheckBinding.resendTime.setText(String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                activityOtpCheckBinding.resendTime.setText("00");
                activityOtpCheckBinding.resendOtpButton.setEnabled(true);
            }
        }.start();
    }
    private void resendOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,
                60,
                java.util.concurrent.TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(Otp_check.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }



                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // The SMS verification code has been sent to the provided phone number,
                        // we now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        enteredcode = verificationId;
                        resendToken = forceResendingToken;
                    }
                });

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {

        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    Intent intent = new Intent(Otp_check.this, Contact_details.class);
                    SharedPreferences sp = getSharedPreferences("number", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("number", number);
                    boolean success = editor.commit();
                    if (success) {
                        progressDialog.dismiss();
                        intent.putExtra("number", number);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Otp_check.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

//                                    }
//                                    else
//                                    {
//                                        Toast.makeText(Otp_check.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                                    }


//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            });


                } else {
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(Otp_check.this, "Please enter correct otp", Toast.LENGTH_SHORT).show();


                        }
                    });
                }

            }
        });


    }

}