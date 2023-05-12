package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {
    EditText passwordResetUsername;
    Button passwordResetButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        passwordResetButton=findViewById(R.id.passResetButton);
        passwordResetUsername=findViewById(R.id.passResetUsername);
        mAuth=FirebaseAuth.getInstance();
        passwordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=passwordResetUsername.getText().toString().trim();
                if(username.isEmpty()){
                    passwordResetUsername.setError("Please enter a valid email");
                    passwordResetUsername.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                    passwordResetUsername.setError("Please enter a valid email");
                    passwordResetUsername.requestFocus();
                    return;
                }else{
                    mAuth.sendPasswordResetEmail(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(PasswordResetActivity.this, "Check inbox and reset password using the shared link.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PasswordResetActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
