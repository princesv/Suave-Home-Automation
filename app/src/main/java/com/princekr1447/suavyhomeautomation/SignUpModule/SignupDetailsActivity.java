package com.princekr1447.suavyhomeautomation.SignUpModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.princekr1447.suavyhomeautomation.CommonUtil;
import com.princekr1447.suavyhomeautomation.ProfileActivity;
import com.princekr1447.suavyhomeautomation.R;
import com.princekr1447.suavyhomeautomation.SignupActivity;
import com.princekr1447.suavyhomeautomation.SignupOrSigninActivity;
import com.princekr1447.suavyhomeautomation.UserSignupInfo;

public class SignupDetailsActivity extends AppCompatActivity {
    EditText editTextPincode;
    EditText editTextAddressLine1;
    EditText editTextCity;
    EditText editTextCountry;
    EditText editTextState;
    EditText getEditTextAddressLine2;
    Button btnNext;
    TextView textStep;
    TextView activityHeader;
    DatabaseReference databaseReferenceUsersId;
    final String usersId_key="usersId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_details);
        editTextPincode=findViewById(R.id.editPincode);
        editTextAddressLine1=findViewById(R.id.editAddressLine1);
        editTextCity=findViewById(R.id.editCity);
        editTextCountry=findViewById(R.id.editCountry);
        btnNext=findViewById(R.id.buttonGoToSignupPage);
        editTextState=findViewById(R.id.textState);
        getEditTextAddressLine2=findViewById(R.id.editAddressLine2);
        textStep=findViewById(R.id.stepNumberTextView);
        activityHeader=findViewById(R.id.createNewAccountTextView);
        textStep.setText(R.string.step3);
        Intent parentIntent=getIntent();
        boolean flag=parentIntent.getBooleanExtra(ProfileActivity.IS_CHANGE_ADDRESS,false);
        String phoneNumber="";
        if(flag){
            textStep.setVisibility(View.GONE);
            activityHeader.setText("Change your address");
            btnNext.setText("save");
            editTextState.setText(parentIntent.getStringExtra(CommonUtil.STATE));
            editTextAddressLine1.setText(parentIntent.getStringExtra(CommonUtil.ADDRESS_LINE_1));
            getEditTextAddressLine2.setText(parentIntent.getStringExtra(CommonUtil.ADDRESS_LINE_2));
            editTextCity.setText(parentIntent.getStringExtra(CommonUtil.CITY));
            editTextCountry.setText(parentIntent.getStringExtra(CommonUtil.COUNTRY));
            editTextPincode.setText(parentIntent.getStringExtra(CommonUtil.PINCODE));
            databaseReferenceUsersId= FirebaseDatabase.getInstance().getReference(usersId_key);
        }else {
            phoneNumber = parentIntent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }
        changeOrUpdateAddress(flag,phoneNumber);
    }
    public void changeOrUpdateAddress(final boolean flag, final String phoneNumber){
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addl1=editTextAddressLine1.getText().toString().trim();
                String addl2=getEditTextAddressLine2.getText().toString().trim();
                String pincode=editTextPincode.getText().toString().trim();
                String city=editTextCity.getText().toString().trim();
                String country=editTextCountry.getText().toString().trim();
                String state=editTextState.getText().toString().trim();
                if(addl1.isEmpty()){
                    editTextAddressLine1.setError("Required field");
                    editTextAddressLine1.requestFocus();
                    return;
                }
                if(addl2.isEmpty()){
                    editTextAddressLine1.setError("Required field");
                    editTextAddressLine1.requestFocus();
                    return;
                }
                if(pincode.isEmpty()){
                    editTextPincode.setError("Required field");
                    editTextPincode.requestFocus();
                    return;
                }
                if(city.isEmpty()){
                    editTextCity.setError("Required field");
                    editTextCity.requestFocus();
                    return;
                }
                if(country.isEmpty()){
                    editTextCountry.setError("Required field");
                    editTextCountry.requestFocus();
                    return;
                }
                if(state.isEmpty()){
                    editTextState.setError("Required field");
                    editTextState.requestFocus();
                    return;
                }
                if(flag){
                    UserSignupInfo userSignupInfo=new UserSignupInfo(addl1,addl2,city,state,pincode,country,phoneNumber);
                    databaseReferenceUsersId.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("personalData").setValue(userSignupInfo);
                    Toast.makeText(SignupDetailsActivity.this, "Adress changed successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Intent intent = new Intent(SignupDetailsActivity.this, SignupActivity.class);
                    intent.putExtra(CommonUtil.CITY, city);
                    intent.putExtra(CommonUtil.COUNTRY, country);
                    intent.putExtra(CommonUtil.ADDRESS_LINE_1, addl1);
                    intent.putExtra(CommonUtil.ADDRESS_LINE_2, addl2);
                    intent.putExtra(CommonUtil.PINCODE, pincode);
                    intent.putExtra(CommonUtil.STATE, state);
                    intent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
                    startActivity(intent);
                }
            }
        });
    }
}
