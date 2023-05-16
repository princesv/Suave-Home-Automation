package com.princekr1447.suavyhomeautomation.SignUpModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.princekr1447.suavyhomeautomation.R;
import com.princekr1447.suavyhomeautomation.SignupActivity;
import com.princekr1447.suavyhomeautomation.SignupOrSigninActivity;

public class SignupDetailsActivity extends AppCompatActivity {
    EditText editTextPincode;
    EditText editTextAddressLine1;
    EditText editTextCity;
    EditText editTextCountry;
    EditText editTextState;
    EditText getEditTextAddressLine2;
    Button btnNext;
    TextView textStep;
    final public static String CITY="city";
    final public static String COUNTRY="country";
    final public static String PINCODE="pincode";
    final public static String ADDRESS_LINE_1="address line 1";
    final public static String ADDRESS_LINE_2="address line 2";
    final public static String STATE="state";

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
        textStep.setText(R.string.step3);
        Intent parentIntent=getIntent();
        final String phoneNumber=parentIntent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
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
                Intent intent=new Intent(SignupDetailsActivity.this, SignupActivity.class);
                intent.putExtra(CITY,city);
                intent.putExtra(COUNTRY,country);
                intent.putExtra(ADDRESS_LINE_1,addl1);
                intent.putExtra(ADDRESS_LINE_2,addl2);
                intent.putExtra(PINCODE,pincode);
                intent.putExtra(STATE,state);
                intent.putExtra(Intent.EXTRA_PHONE_NUMBER,phoneNumber);
                startActivity(intent);
            }
        });
    }
}
