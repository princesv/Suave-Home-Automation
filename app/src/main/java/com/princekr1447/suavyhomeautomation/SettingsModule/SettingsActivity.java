package com.princekr1447.suavyhomeautomation.SettingsModule;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.princekr1447.suavyhomeautomation.Main2Activity;
import com.princekr1447.suavyhomeautomation.ManageSmartdomActivity;
import com.princekr1447.suavyhomeautomation.ProfileActivity;
import com.princekr1447.suavyhomeautomation.R;
import com.princekr1447.suavyhomeautomation.SignupOrSigninActivity;

public class SettingsActivity extends AppCompatActivity {
    LinearLayout profile;
    LinearLayout authentication;
    LinearLayout subscription;
    LinearLayout manageSmartdom;
    TextView logoutTxtView;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        profile=findViewById(R.id.Profile);
        authentication=findViewById(R.id.authentication);
        subscription=findViewById(R.id.Subs);
        manageSmartdom=findViewById(R.id.manage);
        logoutTxtView=findViewById(R.id.logoutTxt);
        mAuth=FirebaseAuth.getInstance();
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc= GoogleSignIn.getClient(this,gso);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, AuthsettingsActivity.class);
                startActivity(intent);
            }
        });
        manageSmartdom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, ManageSmartdomActivity.class);
                startActivity(intent);
            }
        });
        logoutTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmLogoutDialog();
            }
        });
    }
    public void showConfirmLogoutDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_confirm_dialog);
        ImageView confirm=dialog.findViewById(R.id.confirmBtn);
        ImageView cancel=dialog.findViewById(R.id.cancelBtn);
        TextView confirmText=dialog.findViewById(R.id.confirmText);
        confirmText.setText(R.string.confirm_logout);
        TextView confirmTextDetail=dialog.findViewById(R.id.confirmDetailText);
        confirmTextDetail.setText(R.string.confirm_logout_detail);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                mAuth.signOut();
                gsc.signOut();
                Intent intent = new Intent(SettingsActivity.this, SignupOrSigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
