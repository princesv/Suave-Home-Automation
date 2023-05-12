package com.princekr1447.suavyhomeautomation.SignUpModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.princekr1447.suavyhomeautomation.R;
import com.princekr1447.suavyhomeautomation.SignupActivity;
import com.princekr1447.suavyhomeautomation.SignupOrSigninActivity;

public class SignupDetailsActivity extends AppCompatActivity {
    EditText editTextPincode;
    EditText editTextAddressLine1;
    EditText editTextCity;
    EditText editTextCountry;
    EditText editTextState;
    Button btnNext;
    final public static String CITY="city";
    final public static String COUNTRY="country";
    final public static String PINCODE="pincode";
    final public static String ADDRESS="address";
    final public static String STATE="state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_details);
        editTextPincode=findViewById(R.id.editPincode);
        editTextAddressLine1=findViewById(R.id.editAddressLine1);
        editTextCity=findViewById(R.id.editCity);
        editTextCountry=findViewById(R.id.textState);
        btnNext=findViewById(R.id.buttonGoToSignupPage);
        editTextState=findViewById(R.id.textState);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addl1=editTextAddressLine1.getText().toString().trim();
                String pincode=editTextPincode.getText().toString().trim();
                String city=editTextCity.getText().toString().trim();
                String country=editTextCountry.getText().toString().trim();
                String state=editTextState.getText().toString().trim();
                if(addl1.isEmpty()){
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
                intent.putExtra(ADDRESS,addl1);
                intent.putExtra(PINCODE,pincode);
                intent.putExtra(STATE,state);
                startActivity(intent);
            }
        });
    }
}
