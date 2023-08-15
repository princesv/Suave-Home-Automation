package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
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
    private FirebaseAuth mAuth;
    DatabaseReference databaseReferenceUsersId;
    DatabaseReference databaseReferenceProductKey;
    final String usersId_key="usersId";
    final String productKey_key="productKeys";
    final String urlString="https://suavy-home-automation-913b1-default-rtdb.firebaseio.com/productKeys/";
    String addressl1;
    String addressl2;
    String pincode;
    String city;
    String country;
    String state;
    String phoneNumber;
    CardView signupWithGoogle;
    TextView textStep;

    ProgressBar loadingPB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Intent intent=getIntent();
        addressl1=intent.getStringExtra(CommonUtil.ADDRESS_LINE_1);
        addressl2=intent.getStringExtra(CommonUtil.ADDRESS_LINE_2);
        pincode=intent.getStringExtra(CommonUtil.PINCODE);
        city=intent.getStringExtra(CommonUtil.CITY);
        country=intent.getStringExtra(CommonUtil.COUNTRY);
        state=intent.getStringExtra(CommonUtil.STATE);
        phoneNumber=intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        button_signup=findViewById(R.id.buttonAccount);
        editTextEmail=findViewById(R.id.emailSignup);
        editTextPassword=findViewById(R.id.passwordSignup);
        signupWithGoogle=findViewById(R.id.signUpWithGoogle);
        loadingPB=findViewById(R.id.loadingPB);
        textStep=findViewById(R.id.stepNumberTextView);
        textStep.setText(R.string.step4);
        mAuth = FirebaseAuth.getInstance();
        databaseReferenceUsersId=FirebaseDatabase.getInstance().getReference(usersId_key);
        databaseReferenceProductKey=FirebaseDatabase.getInstance().getReference(productKey_key);
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc= GoogleSignIn.getClient(this,gso);
        signupWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=gsc.getSignInIntent();
                startActivityForResult(intent,100);
            }
        });
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
                CheckEmailCollisionAndSignupWithEmailPasswordLogin(emailSignup, passwordSignup);
            }
        });
    }
    public void CheckEmailCollisionAndSignupWithEmailPasswordLogin(final String emailSignup,final String passwordSignup){
        mAuth.fetchSignInMethodsForEmail(emailSignup).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().contains(
                                GoogleAuthProvider.PROVIDER_ID)){
                    Toast.makeText(SignupActivity.this, "Email address already in use by Google authentication provider. Use another account.", Toast.LENGTH_SHORT).show();
                }else{
                    signupWithEmailAndPassword(emailSignup, passwordSignup);
                }
            }
        });
    }
   /* void initialiseBoards(){
        SwitchBoard switchBoard=new SwitchBoard("Title","name1","name2","name3","name4","name5","name6","name7","name8");
        for(int i=0;i<25;i++){
            databaseReferenceProductKey.child(productKey).child("SwitchBoards").child(""+i).setValue(switchBoard);
        }
    }*/
   void signupWithEmailAndPassword(String email,String password){
       progressVisible();
       AuthCredential credential = EmailAuthProvider.getCredential(email, password);
       mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   userSignupInfoUpdate(task.getResult().getUser().getUid());
                   Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                   FirebaseUser user=mAuth.getCurrentUser();
                   user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           showDialogOnUserRegistered();
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
                   mAuth.signOut();
               }
               else{
                   if(task.getException() instanceof FirebaseAuthUserCollisionException){
                       Toast.makeText(SignupActivity.this, "you are already registered", Toast.LENGTH_SHORT).show();
                   }else{
                       Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   }
               }
               progressInvisible();
           }
       });
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
           /* try {
                GoogleSignInAccount account= task.getResult(ApiException.class);
                signupWithEmailAndPassword(account.getEmail(),account.getId());
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to sign in. Please try again.", Toast.LENGTH_SHORT).show();
            }

            */
            try {
                progressVisible();
                final GoogleSignInAccount account= task.getResult(ApiException.class);
                mAuth.fetchSignInMethodsForEmail(account.getEmail()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.getResult().getSignInMethods().contains(
                                EmailAuthProvider.PROVIDER_ID)||
                                task.getResult().getSignInMethods().contains(
                                        GoogleAuthProvider.PROVIDER_ID)){
                            gsc.signOut();
                            Toast.makeText(SignupActivity.this, "Email address already in use. Use another account.", Toast.LENGTH_SHORT).show();
                        }else{
                            signInWithGoogleCredentials(account.getIdToken());
                        }
                        progressInvisible();
                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
    void progressVisible(){
       loadingPB.setVisibility(View.VISIBLE);
       button_signup.setVisibility(View.INVISIBLE);
    }
    void progressInvisible(){
        loadingPB.setVisibility(View.INVISIBLE);
        button_signup.setVisibility(View.VISIBLE);
    }
    public void signInWithGoogleCredentials(String idToken){
        progressVisible();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    userSignupInfoUpdate(task.getResult().getUser().getUid());
                    homeActivity();
                }
                else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(SignupActivity.this, "you are already registered", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressInvisible();
            }
        });
    }
    public void showDialogOnUserRegistered(){
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_verify_email);
        Button dialogButton=dialog.findViewById(R.id.dialog_btn_nav_to_signin);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this,SigninActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                dialog.hide();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    public void homeActivity() {
        //String emailEdited=emailSignin.replace(".","DOTT");
        Intent intent=new Intent(SignupActivity.this,Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void userSignupInfoUpdate(String userId){
       UserSignupInfo userSignupInfo=new UserSignupInfo(addressl1,addressl2,city,state,pincode,country,phoneNumber);
        databaseReferenceUsersId.child(userId).child("personalData").setValue(userSignupInfo);
        databaseReferenceUsersId.child(userId).child("profilePhotoUrl").setValue("NA");
    }
}
