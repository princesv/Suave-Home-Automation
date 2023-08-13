package com.princekr1447.suavyhomeautomation.SettingsModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.princekr1447.suavyhomeautomation.CommonUtil;
import com.princekr1447.suavyhomeautomation.R;
import com.princekr1447.suavyhomeautomation.SignUpModule.PhoneActivity;

public class AuthsettingsActivity extends AppCompatActivity {
    LinearLayout changePhoneNo;
    LinearLayout changePassword;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authsettings);
        changePhoneNo=findViewById(R.id.changePhoneNumber);
        changePassword=findViewById(R.id.changePassword);
        progressBar=findViewById(R.id.progCircle);
        mAuth=FirebaseAuth.getInstance();
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.fetchSignInMethodsForEmail(mAuth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.getResult().getSignInMethods().contains(
                                EmailAuthProvider.PROVIDER_ID)){
                            mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(AuthsettingsActivity.this, "Check inbox and reset password using the shared link.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AuthsettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            }else{
                            Toast.makeText(AuthsettingsActivity.this, "You are logged in through Google authentication. Action can not be performed", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        changePhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AuthsettingsActivity.this, PhoneActivity.class);
                intent.putExtra(CommonUtil.CHANGE_PHONE_NUMBER,true);
                startActivity(intent);
            }
        });
    }

}
