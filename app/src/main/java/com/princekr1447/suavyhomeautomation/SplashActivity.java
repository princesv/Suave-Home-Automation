package com.princekr1447.suavyhomeautomation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    public static final String SHARED_PREF="sharedPrefs";
    public static final String SIGNEDIN="signedIn";
    SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private boolean isSignedIn= false;
    TextView tvSmartdom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        mAuth= FirebaseAuth.getInstance();
        tvSmartdom=findViewById(R.id.textViewSmartdom);
        //isSignedIn = sharedPreferences.getBoolean(SIGNEDIN,false);
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            isSignedIn=true;
        }
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                if(isSignedIn) {
                    Intent intent=new Intent(SplashActivity.this,Main2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(SplashActivity.this,SignupOrSigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this,tvSmartdom, ViewCompat.getTransitionName(tvSmartdom));
                    startActivity(intent,optionsCompat.toBundle());
                }
                finish();
            }
        }, 500);
    }
}
