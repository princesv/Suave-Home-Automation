package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.princekr1447.suavyhomeautomation.SignUpModule.SignupDetailsActivity;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.UUID;

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
    public static String IS_CHANGE_ADDRESS="change address";
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressBar progressBar;
    private Uri imageUri;
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
        progressBar=findViewById(R.id.progress_image_upload);
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        emailTV.setText(mAuth.getCurrentUser().getEmail());
        phoneTV.setText(mAuth.getCurrentUser().getPhoneNumber());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        refUserId= FirebaseDatabase.getInstance().getReference().child("usersId");
        Picasso.get().load(CommonUtil.profilePhotoUrl).centerCrop().fit().into(imageHome);
        refUserId.child(userId).child("profilePhotoUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.getValue().equals("NA")){
                    CommonUtil.profilePhotoUrl=snapshot.getValue().toString();
                }else{
                    CommonUtil.profilePhotoUrl=CommonUtil.defaultProfilePhoto;
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
        ImageView deletePhoto=dialog.findViewById(R.id.deletePhotoBtn);
        ImageView importPhotoBtn=dialog.findViewById(R.id.importPhotoBtn);
        importPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageGallery();
                dialog.dismiss();
            }
        });
        ImageView clickPhotoBtn=dialog.findViewById(R.id.clickPhotoBtn);
        clickPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageCamera();
                dialog.dismiss();
            }
        });
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                StorageReference ref
                        = storageReference
                        .child(
                                "home/"+userId);
                ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        refUserId.child(userId).child("profilePhotoUrl").setValue("NA").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Home picture removed successfully!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    public void changeAddress(View view){
        Intent intent=new Intent(ProfileActivity.this, SignupDetailsActivity.class);
        intent.putExtra(IS_CHANGE_ADDRESS,true);
        intent.putExtra(CommonUtil.CITY, tvCity.getText());
        intent.putExtra(CommonUtil.COUNTRY, tvCountry.getText());
        intent.putExtra(CommonUtil.ADDRESS_LINE_1, tvAddressLine1.getText());
        intent.putExtra(CommonUtil.ADDRESS_LINE_2, tvAddressLine2.getText());
        intent.putExtra(CommonUtil.PINCODE, tvPincode.getText());
        intent.putExtra(CommonUtil.STATE, tvState.getText());
        startActivity(intent);
    }

    private void pickImageCamera()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,110);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode==220||requestCode==110)&&resultCode==-1){
            if(data!=null) {
                imageUri = data.getData();
            }
            uploadImage();
        }
    }
    private void pickImageGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,220);
    }

    private void uploadImage()
    {
        if (imageUri != null) {

            // Code for showing progressDialog while uploading
            progressBar.setVisibility(View.VISIBLE);

            // Defining the child of storageReference
           // final String uuidPhoto=UUID.randomUUID().toString();
            StorageReference ref
                    = storageReference
                    .child(
                            "home/"+userId);

            // adding listeners on upload
            // or failure of image
            ref.putFile(imageUri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast
                                            .makeText(ProfileActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String downloadUrl=task.getResult().toString();
                                            refUserId.child(userId).child("profilePhotoUrl").setValue(downloadUrl);
                                        }
                                    });
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast
                                    .makeText(ProfileActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
    }

}
