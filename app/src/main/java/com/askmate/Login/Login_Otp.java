package com.askmate.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.askmate.Location_set;
import com.askmate.MainActivity;
import com.askmate.R;
import com.askmate.databinding.ActivityLoginOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class Login_Otp extends AppCompatActivity {
    ProgressDialog progressDialog;

    ActivityLoginOtpBinding binding;
    String enteredcode;
    String number;
    PhoneAuthProvider.ForceResendingToken resendToken;

    private CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);


        getWindow().setStatusBarColor(getResources().getColor(R.color.appcolor));
        binding = ActivityLoginOtpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        startCountDownTimer();
        Intent intent = getIntent();
        number = intent.getStringExtra("mobile");
        String phone = "+91-" + number;
        binding.phonenumber.setText(phone);

        binding.otpRead.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.otpRead.isFocusedByDefault();
        }

        Intent intent21 = getIntent();
        if (intent != null) {
            resendToken = intent21.getParcelableExtra("token");
        }

        enteredcode = getIntent().getStringExtra("otp");


        binding.resendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trigger OTP resend process
                // Disable the button
                binding.resendOtpButton.setEnabled(false);
                // Start the countdown timer
                startCountDownTimer();
                // Call the method to resend OTP
                resendOTP(number);
            }
        });


        binding.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
                String optnumber = binding.otpRead.getText().toString();

                if (!optnumber.isEmpty())
                {

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(enteredcode, optnumber);

                    signInWithPhoneAuthCredential(phoneAuthCredential);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Login_Otp.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
                        Toast.makeText(Login_Otp.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                binding.resendTime.setText(String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                binding.resendTime.setText("00");
                binding.resendOtpButton.setEnabled(true);
            }
        }.start();
    }

    private void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {

        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {


                    SharedPreferences sharedPreferences = getSharedPreferences("home", MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean("home", true);


                    progressDialog.dismiss();
                    boolean check = editor.commit();

                    if (check)
                    {
                        Intent intent = new Intent(Login_Otp.this, Location_set.class);
                        intent.putExtra("number", number);
                        startActivity(intent);
                        finish();
                    }

                    else
                    {
                        Toast.makeText(Login_Otp.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }


                } else {
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(Login_Otp.this, e.toString(), Toast.LENGTH_SHORT).show();
//
//                            constraintLayout.setAlpha(1);
//                            progressBar.setVisibility(View.GONE);
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                        }
                    });
                }

            }
        });


    }





}