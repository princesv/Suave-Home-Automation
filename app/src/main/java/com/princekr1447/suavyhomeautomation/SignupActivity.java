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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.princekr1447.suavyhomeautomation.SignUpModule.SignupDetailsActivity;

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

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
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
    public static final String SHARED_PREF="sharedPrefs";
    public static final String SIGNEDIN="signedIn";
    public static final String USERID="userId";
    String addressl1;
    String pincode;
    String city;
    String country;
    String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        Intent intent=getIntent();
        addressl1=intent.getStringExtra(SignupDetailsActivity.ADDRESS);
        pincode=intent.getStringExtra(SignupDetailsActivity.PINCODE);
        city=intent.getStringExtra(SignupDetailsActivity.CITY);
        country=intent.getStringExtra(SignupDetailsActivity.COUNTRY);
        state=intent.getStringExtra(SignupDetailsActivity.STATE);
        productKeys=new ArrayList<>();
        button_signup=findViewById(R.id.buttonAccount);
        editTextPincode=findViewById(R.id.editPincode);
        editTextAddressLine1=findViewById(R.id.editAddressLine1);
        editTextCity=findViewById(R.id.editCity);
        editTextCountry=findViewById(R.id.textState);
        mAuth = FirebaseAuth.getInstance();
        databaseReferenceUsersId=FirebaseDatabase.getInstance().getReference(usersId_key);
        databaseReferenceProductKey=FirebaseDatabase.getInstance().getReference(productKey_key);
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc= GoogleSignIn.getClient(this,gso);

        button_signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String emailSignup=editTextEmail.getText().toString().trim();
                String passwordSignup=editTextPassword.getText().toString().trim();
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
                signupWithEmailAndPassword(emailSignup,passwordSignup);
            }
        });
    }
   /* void initialiseBoards(){
        SwitchBoard switchBoard=new SwitchBoard("Title","name1","name2","name3","name4","name5","name6","name7","name8");
        for(int i=0;i<25;i++){
            databaseReferenceProductKey.child(productKey).child("SwitchBoards").child(""+i).setValue(switchBoard);
        }
    }*/
   public void signUpWithGoogle(View view){
       Intent intent=gsc.getSignInIntent();
       startActivityForResult(intent,100);
   }
   void signupWithEmailAndPassword(String email,String password){
       mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                   FirebaseUser user=mAuth.getCurrentUser();
                   String editedEmailForCall= user.getUid();
                   UserSignupInfo userSignupInfo=new UserSignupInfo(pincode,productKeys,addressl1,city,state);
                   databaseReferenceUsersId.child(editedEmailForCall).setValue(userSignupInfo);
                   Intent intent=new Intent(SignupActivity.this,Main2Activity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(intent);
               }
               else{
                   if(task.getException() instanceof FirebaseAuthUserCollisionException){
                       Toast.makeText(SignupActivity.this, "you are already registered", Toast.LENGTH_SHORT).show();
                   }else{
                       Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   }
                   gsc.signOut();
               }
           }
       });
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account= task.getResult(ApiException.class);
                signupWithEmailAndPassword(account.getEmail(),account.getId());
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to sign in. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
