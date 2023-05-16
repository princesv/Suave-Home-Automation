package com.princekr1447.suavyhomeautomation.SignUpModule;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.hbb20.CountryCodePicker;
import com.princekr1447.suavyhomeautomation.R;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhoneActivity extends AppCompatActivity {
    EditText phoneNumber;
    CountryCodePicker ccp;
    TextView textStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        phoneNumber=findViewById(R.id.userPhoneNumber);
        ccp=findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneNumber);
        textStep=findViewById(R.id.stepNumberTextView);
        textStep.setText(R.string.step1);

    }
   /* public static String httpRequestToSendOTP(String phoneno) {
        final String apiAccessToken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJhdXRoLWJhY2tlbmQ6YXBwIiwic3ViIjoiZTM5NTdjYTQtNTcwYy00MDAzLThmOTMtZWRiNWQxMWRhNDIxIn0.Dbam-CVSCLMy0cKbensM8TI6m6rOGPLlU9cnP23CPoU";
        final OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"originator\": \"SignOTP\",\n    \"recipient\": \"+91"+phoneno+"\",\n    \"content\": \"Greetings from D7 API, your mobile verification code is: {}\",\n    \"expiry\": \"600\",\n    \"data_coding\": \"text\"\n}");
        final Request request = new Request.Builder()
                .url("https://api.d7networks.com/verify/v1/otp/send-otp")
                .method("POST", body)
                .addHeader("Authorization", "Bearer "+apiAccessToken)
                .addHeader("Content-Type", "application/json")
                .build();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return "Run!";
    }
    private class AsyncTaskVerifyPhone extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n    \"originator\": \"SignOTP\",\n    \"recipient\": \"+91"+params+"\",\n    \"content\": \"Greetings from D7 API, your mobile verification code is: {}\",\n    \"expiry\": \"600\",\n    \"data_coding\": \"text\"\n}");
            Request request = new Request.Builder()
                    .url("https://api.d7networks.com/verify/v1/otp/send-otp")
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJhdXRoLWJhY2tlbmQ6YXBwIiwic3ViIjoiZTM5NTdjYTQtNTcwYy00MDAzLThmOTMtZWRiNWQxMWRhNDIxIn0.Dbam-CVSCLMy0cKbensM8TI6m6rOGPLlU9cnP23CPoU")
                    .addHeader("Content-Type", "application/json")
                    .build();
            String resp=null;
            try {
                Response response = client.newCall(request).execute();
                resp=response.message();
                String s=response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
        }


        @Override
        protected void onPreExecute() {
        }
    }*/
   public void openOTPVerificationActivity(View view){
       String phoneno=phoneNumber.getText().toString().trim();
       if(phoneno.isEmpty()){
           phoneNumber.setError("Field empty!");
           phoneNumber.requestFocus();
           return;
       }

       if(!Patterns.PHONE.matcher(phoneno).matches()){
           phoneNumber.setError("Please enter a valid phone number");
           phoneNumber.requestFocus();
           return;
       }
       //  httpRequestToSendOTP(phoneno);
       /*AsyncTaskVerifyPhone taskVerifyPhone=new AsyncTaskVerifyPhone();
       taskVerifyPhone.execute(phoneno);*/
       Intent intent = new Intent(PhoneActivity.this,EnterOtpActivity.class);
       intent.putExtra(Intent.EXTRA_PHONE_NUMBER,ccp.getFullNumberWithPlus().replace(" ",""));
       startActivity(intent);
   }
}
