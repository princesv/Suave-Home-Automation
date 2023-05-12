package com.princekr1447.suavyhomeautomation.SignUpModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.princekr1447.suavyhomeautomation.R;

import java.util.concurrent.TimeUnit;

public class EnterOtpActivity extends AppCompatActivity {
    EditText et1;
    EditText et2;
    EditText et3;
    EditText et4;
    EditText et5;
    EditText et6;
    String phoneNumber;
    String otpId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        et3=findViewById(R.id.et3);
        et4=findViewById(R.id.et4);
        et5=findViewById(R.id.et5);
        et6=findViewById(R.id.et6);
        setupOtpInputs();
        phoneNumber=getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        sendOtp();
    }
    private void sendOtp(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(EnterOtpActivity.this, "Phone verification successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EnterOtpActivity.this, SignupDetailsActivity.class);
                        intent.putExtra(Intent.EXTRA_PHONE_NUMBER,phoneNumber);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpId=s;
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(EnterOtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    private void setupOtpInputs(){
        et1.setFocusedByDefault(true);
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    et2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    et3.requestFocus();
                }else{
                    et1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    et4.requestFocus();
                }else{
                    et2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    et5.requestFocus();
                }else{
                    et3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    et6.requestFocus();
                }else{
                    et4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().isEmpty()){
                    et5.requestFocus();
                }else{
                    et6.clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void verifyOTP(View view){
        if(et1.toString().trim().isEmpty()||et2.toString().trim().isEmpty()||et3.toString().trim().isEmpty()||et4.toString().trim().isEmpty()){
            Toast.makeText(this, "Enter valid OTP!", Toast.LENGTH_SHORT).show();
        }else{
            String otp=et1.getText().toString().trim();
            otp+=et2.getText().toString().trim();
            otp+=et3.getText().toString().trim();
            otp+=et4.getText().toString().trim();
            otp+=et5.getText().toString().trim();
            otp+=et6.getText().toString().trim();

            /*otp.concat(et2.getText().toString().trim());
            otp.concat(et3.getText().toString().trim());
            otp.concat(et4.getText().toString().trim());
            otp.concat(et5.getText().toString().trim());
            otp.concat(et6.getText().toString().trim());

             */
            PhoneAuthCredential credential=PhoneAuthProvider.getCredential(otpId,otp);
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(EnterOtpActivity.this, "Phone verification successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EnterOtpActivity.this, SignupDetailsActivity.class);
                        intent.putExtra(Intent.EXTRA_PHONE_NUMBER,phoneNumber);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(EnterOtpActivity.this, "Incorrect OTP entered", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void resendOtp(View view){
        sendOtp();
    }
}
