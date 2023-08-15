package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.princekr1447.suavyhomeautomation.SettingsModule.SettingsActivity;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    FirebaseAuth mAuth;
    public static final int LAUNCH_QR_ACTIVITY_FOR_RESULT=11111;
    DatabaseReference refUserId;
    DatabaseReference refProductKey;
    String pk;
    String emailEncoded;
    public static final String SHARED_PREF="sharedPrefs";
    public static final String USERID="userId";
    public static final String IS_GOOGLE_LOGIN="googleLogin";
    SharedPreferences sharedPreferences;
    ArrayList<CentralModule> centralModules=new ArrayList<>();
    public static final String SIGNEDIN="signedIn";
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    LinearLayout shimmerFrameLayout;
    ImageView imageHome;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mAuth=FirebaseAuth.getInstance();
        handleIncompleteRegistrationFailure();

        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        shimmerFrameLayout=findViewById(R.id.idShimmerLayoutView);
        //getSupportActionBar().hide();
        toolbar=findViewById(R.id.toolBar);
        imageHome=findViewById(R.id.image_home);
        //toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.colorWhite));
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        if(sharedPreferences!=null) {
           // emailEncoded = sharedPreferences.getString(USERID, "F");
           // isGoogleLogin=sharedPreferences.getBoolean(IS_GOOGLE_LOGIN,false);
        }
        emailEncoded=user.getUid();
        refUserId= FirebaseDatabase.getInstance().getReference().child("usersId");
        refProductKey=FirebaseDatabase.getInstance().getReference().child("productKeys");

        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc= GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
        Picasso.get().load(CommonUtil.profilePhotoUrl).centerCrop().fit().into(imageHome);
        refUserId.child(emailEncoded).child("profilePhotoUrl").addValueEventListener(new ValueEventListener() {
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
        refUserId.child(emailEncoded).child("productKeys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                centralModules.clear();
                shimmerFrameLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                for(DataSnapshot artistSnapshot:snapshot.getChildren()){

                    CentralModule cm=artistSnapshot.getValue(CentralModule.class);
                    centralModules.add(cm);

                }
                updateTabLayout();
                final SwitchBoardFragmentAdapter switchBoardFragmentAdapter = new SwitchBoardFragmentAdapter(Main2Activity.this,getSupportFragmentManager(), tabLayout.getTabCount(),centralModules,emailEncoded);
                viewPager.setAdapter(switchBoardFragmentAdapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void handleIncompleteRegistrationFailure(){
        FirebaseUser user=mAuth.getCurrentUser();
        List<? extends UserInfo> info= mAuth.getCurrentUser().getProviderData();
        int provCount=info.size();
        String a=info.get(1).getProviderId();
        if(provCount==2&&info.get(1).getProviderId().equals(PhoneAuthProvider.PROVIDER_ID)){
            mAuth.signOut();
            Intent intent = new Intent(Main2Activity.this, SignupOrSigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Toast.makeText(this, "Error handled", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LAUNCH_QR_ACTIVITY_FOR_RESULT){
            if(resultCode== Activity.RESULT_OK){
                pk=data.getStringExtra("result");
                Main2Activity.AsyncTaskAddNewCentralModule asyncTaskAddNewCentralModule=new Main2Activity.AsyncTaskAddNewCentralModule();
                asyncTaskAddNewCentralModule.execute();
            }else if(resultCode== Activity.RESULT_CANCELED){
                Toast.makeText(this, "Failed to scan product key. Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    void updateTabLayout(){
        tabLayout.removeAllTabs();
        for(int i=0;i<centralModules.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(centralModules.get(i).getName()));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }
    private class AsyncTaskAddNewCentralModule extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            refProductKey.child(pk).child("switchBoards").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String res=snapshot.toString();
                    if(res.equals("DataSnapshot { key = switchBoards, value = null }")){
                        Toast.makeText(Main2Activity.this, "Invalid product key", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("DataSnapshot { key = switchBoards, value = # }")){
                       // String id=refUserId.child(emailEncoded).child("productKeys").push().getKey();

                        //refProductKey.child(pk).child("Name").setValue("Untitled");
                        initializeCentralModule();
                        initializeSwitchBoards();
                        initializeRoom();
                        initializeKeypos();
                        Toast.makeText(Main2Activity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Main2Activity.this, "Product key already used by some other user", Toast.LENGTH_SHORT).show();
                    }
                }
                void initializeSwitchBoards(){
                    SwitchBoard switchBoard=new SwitchBoard("Title","name1","name2","name3","name4","name5","name6","name7","name8");
                    for(int i=0;i<25;i++){
                        refProductKey.child(pk).child("switchBoards").child(""+i).setValue(switchBoard);
                    }
                }
                void initializeRoom(){
                    HashMap<String,IndexPojo> indices=new HashMap<>();
                    String key2=refProductKey.child(pk).child("rooms").push().getKey();
                    for(int i=0;i<25;i++){
                        String key3 = refProductKey.child(pk).child("rooms").child(key2).push().getKey();
                        IndexPojo indexPojo=new IndexPojo(key3,i);
                        indices.put(key3,indexPojo);
                    }
                    RoomPojo roomPojo=new RoomPojo("Stand Alone",indices,key2);
                    refProductKey.child(pk).child("rooms").child(key2).setValue(roomPojo);
                }
                void initializeCentralModule(){
                    String key=refUserId.child(emailEncoded).child("productKeys").push().getKey();
                    CentralModule cm=new CentralModule("untitled",pk,key);
                    refUserId.child(emailEncoded).child("productKeys").child(key).setValue(cm);
                }
                void initializeKeypos(){
                    String kp= "";
                    String tmp="22222222";
                    for(int i=0;i<25;i++){
                        kp=kp.concat(tmp);
                    }
                    String kpid=refProductKey.child(pk).child("keyPos").push().getKey();
                    refProductKey.child(pk).child("keyPos").child(kpid).setValue(kp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addCentralModule:
                 Intent i = new Intent(Main2Activity.this, QrScannerActivity.class);
                 startActivityForResult(i, LAUNCH_QR_ACTIVITY_FOR_RESULT);
                 return (true);
            case R.id.settings:
                Intent i2 = new Intent(Main2Activity.this, SettingsActivity.class);
                startActivity(i2);
                return (true);
        }
        return true;
    }
}
