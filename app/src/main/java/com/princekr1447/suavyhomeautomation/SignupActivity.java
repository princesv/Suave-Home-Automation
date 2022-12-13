package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    Button button_signup;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextPincode;
    EditText editTextAddressLine1;
    EditText editTextCity;
    EditText editTextCountry;
    TextView goToSignIn;
    ArrayList<String> productKeys;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReferenceUsersId;
    DatabaseReference databaseReferenceProductKey;
    final String usersId_key="usersId";
    final String productKey_key="productKeys";
    final String urlString="https://suavy-home-automation-913b1-default-rtdb.firebaseio.com/productKeys/";
    boolean flag=false;
    List<String> datass = new ArrayList<>();
    public static final String SHARED_PREF="sharedPrefs";
    public static final String SIGNEDIN="signedIn";
    public static final String USERID="userId";
    String emailSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();

        productKeys=new ArrayList<>();
        button_signup=findViewById(R.id.buttonAccount);
        editTextEmail=findViewById(R.id.editEmail);
        editTextPassword=findViewById(R.id.editPass);
        editTextPincode=findViewById(R.id.editPincode);
        editTextAddressLine1=findViewById(R.id.editAddressLine1);
        editTextCity=findViewById(R.id.editCity);
        editTextCountry=findViewById(R.id.textState);
        goToSignIn=findViewById(R.id.goToLogIn);
        mAuth = FirebaseAuth.getInstance();
        databaseReferenceUsersId=FirebaseDatabase.getInstance().getReference(usersId_key);
        databaseReferenceProductKey=FirebaseDatabase.getInstance().getReference(productKey_key);

        button_signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datass.clear();
                emailSignup=editTextEmail.getText().toString().trim();
                String passwordSignup=editTextPassword.getText().toString().trim();
                final String pincodeSignup=editTextPincode.getText().toString().trim();
                final String addressLine1=editTextAddressLine1.getText().toString().trim();
                final String stringCity=editTextCity.getText().toString().trim();
                final String stringState=editTextCountry.getText().toString().trim();
                if(emailSignup.isEmpty()){
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(emailSignup).matches()){
                    editTextEmail.setError("Please enter a valid email");
                    editTextEmail.requestFocus();
                    return;
                }
                if(passwordSignup.isEmpty()) {
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    return;
                }
                if(passwordSignup.length()<6){
                    editTextPassword.setError("Minimum length of password should be 6");
                    editTextPassword.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(emailSignup,passwordSignup).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            String editedEmailForCall= emailSignup.replace(".","DOTT");
                            UserSignupInfo userSignupInfo=new UserSignupInfo(pincodeSignup,productKeys,addressLine1,stringCity,stringState);
                            databaseReferenceUsersId.child(editedEmailForCall).setValue(userSignupInfo);
                            Intent intent=new Intent(SignupActivity.this,Main2Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            editor.putBoolean(SIGNEDIN,true);
                            editor.putString(USERID,editedEmailForCall);
                            editor.apply();
                            startActivity(intent);
                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignupActivity.this, "you are already registered", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
            }
        });
    }
    public void openSignin(View view){
        Intent intent=new Intent(this,SigninActivity.class);
        startActivity(intent);
        finish();
    }
   /* void initialiseBoards(){
        SwitchBoard switchBoard=new SwitchBoard("Title","name1","name2","name3","name4","name5","name6","name7","name8");
        for(int i=0;i<25;i++){
            databaseReferenceProductKey.child(productKey).child("SwitchBoards").child(""+i).setValue(switchBoard);
        }
    }*/
}
