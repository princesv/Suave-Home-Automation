package com.princekr1447.suavyhomeautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.princekr1447.suavyhomeautomation.SignUpModule.EnterOtpActivity;
import com.princekr1447.suavyhomeautomation.SignUpModule.PhoneActivity;
import com.princekr1447.suavyhomeautomation.SignUpModule.SignupDetailsActivity;

public class SignupOrSigninActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_or_signin);
    }
    public void goToSigninPage(View view){
        Intent intent=new Intent(SignupOrSigninActivity.this,SigninActivity.class);
        startActivity(intent);
    }
    public void goToSignupPage(View view){
        Intent intent=new Intent(SignupOrSigninActivity.this, PhoneActivity.class);
        startActivity(intent);
    }
}
