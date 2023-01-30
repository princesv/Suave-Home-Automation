package com.princekr1447.suavyhomeautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    public static final String SHARED_PREF="sharedPrefs";
    public static final String SIGNEDIN="signedIn";
    SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private boolean isSignedIn= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        mAuth= FirebaseAuth.getInstance();
        isSignedIn = sharedPreferences.getBoolean(SIGNEDIN,false);
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                if(isSignedIn) {
                    Intent intent=new Intent(SplashActivity.this,Main2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(SplashActivity.this,SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
            }
        }, 3000);
    }
}
