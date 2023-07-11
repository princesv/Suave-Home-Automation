package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference refUserId;
    String userId;
    TextView tvPincode;
    TextView tvAddressLine1;
    TextView tvCity;
    TextView tvCountry;
    TextView tvState;
    TextView tvAddressLine2;
    TextView emailTV;
    TextView phoneTV;
    ImageView imageHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tvPincode=findViewById(R.id.addPincodeTv);
        tvAddressLine1=findViewById(R.id.addL1Tv);
        tvCity=findViewById(R.id.addCityTv);
        tvCountry=findViewById(R.id.addCountryTv);
        tvState=findViewById(R.id.addStateTv);
        tvAddressLine2=findViewById(R.id.addL2Tv);
        emailTV=findViewById(R.id.emailTv);
        phoneTV=findViewById(R.id.phoneTv);
        imageHome=findViewById(R.id.image_home);
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        emailTV.setText(mAuth.getCurrentUser().getEmail());
        phoneTV.setText(mAuth.getCurrentUser().getPhoneNumber());
        refUserId= FirebaseDatabase.getInstance().getReference().child("usersId");
        Picasso.get().load(CommonUtil.profilePhotoUrl).centerCrop().fit().into(imageHome);
        refUserId.child(userId).child("profilePhotoUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    CommonUtil.profilePhotoUrl=snapshot.getValue().toString();
                }
                Picasso.get().load(CommonUtil.profilePhotoUrl).networkPolicy(NetworkPolicy.NO_CACHE).centerCrop().fit().into(imageHome);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refUserId.child(userId).child("personalData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserSignupInfo userSignupInfo=snapshot.getValue(UserSignupInfo.class);
                tvAddressLine1.setText(userSignupInfo.addressL1);
                tvCity.setText(userSignupInfo.city);
                tvCountry.setText(userSignupInfo.country);
                tvPincode.setText(userSignupInfo.pincode);
                tvState.setText(userSignupInfo.state);
                tvAddressLine2.setText(userSignupInfo.addressL2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void changeHomePhoto(View view){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        ImageView clickPhoto=dialog.findViewById(R.id.clickPhotoBtn);
        ImageView importPhoto=dialog.findViewById(R.id.importPhotoBtn);
        ImageView deletePhoto=dialog.findViewById(R.id.deletePhotoBtn);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
