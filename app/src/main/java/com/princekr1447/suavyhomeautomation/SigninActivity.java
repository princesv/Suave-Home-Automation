package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SigninActivity extends AppCompatActivity {

    EditText sign_in_username;
    EditText signin_password;
    TextView resetPasswordTextView;
    Button signin_button;
    private FirebaseAuth mAuth;
    public static final String SIGNEDIN="signedIn";
    public static final String USERID="userId";
    public static final String IS_GOOGLE_LOGIN="googleLogin";
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREF="sharedPrefs";
    SharedPreferences.Editor editor;
    ProgressBar loadingPB;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        sign_in_username=findViewById(R.id.signin_username);
        signin_password=findViewById(R.id.signin_password);
        signin_button=findViewById(R.id.signin_button);
        resetPasswordTextView=findViewById(R.id.resetPasswordTv);
        loadingPB=findViewById(R.id.loadingPB);
        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        mAuth=FirebaseAuth.getInstance();
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc= GoogleSignIn.getClient(this,gso);
        resetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this,PasswordResetActivity.class);
                startActivity(intent);

            }
        });
        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailSignin=sign_in_username.getText().toString().trim();
                final String passwordSignin=signin_password.getText().toString().trim();

                if(emailSignin.isEmpty()){
                    sign_in_username.setError("Email is required");
                    sign_in_username.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(emailSignin).matches()){
                    sign_in_username.setError("Please enter a valid email");
                    sign_in_username.requestFocus();
                    return;
                }
                if(passwordSignin.isEmpty()) {
                    signin_password.setError("Password is required");
                    signin_password.requestFocus();
                    return;
                }
                if(passwordSignin.length()<6){
                    signin_password.setError("Minimum length of password should be 6");
                    signin_password.requestFocus();
                    return;
                }
                progressVisible();
                mAuth.signInWithEmailAndPassword(emailSignin,passwordSignin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            HomeActivity();
                        }else{
                            Toast.makeText(SigninActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressInvisible();
                    }
                });
            }
        });




    }

    public void logInWithGoogle(View view) {
        Intent intent=gsc.getSignInIntent();
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressVisible();
        if(requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account= task.getResult(ApiException.class);
                mAuth.signInWithEmailAndPassword(account.getEmail(),account.getId()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            HomeActivity();
                        }else{
                            Toast.makeText(SigninActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            gsc.signOut();
                        }
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to sign in. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
        progressInvisible();
    }

    private void HomeActivity() {
        //String emailEdited=emailSignin.replace(".","DOTT");
        Intent intent=new Intent(SigninActivity.this,Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    void progressVisible(){
        loadingPB.setVisibility(View.VISIBLE);
        signin_button.setVisibility(View.INVISIBLE);
    }
    void progressInvisible(){
        loadingPB.setVisibility(View.INVISIBLE);
        signin_button.setVisibility(View.VISIBLE);
    }
}
