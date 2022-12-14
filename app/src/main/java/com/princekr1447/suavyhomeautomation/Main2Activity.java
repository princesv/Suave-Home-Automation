package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FloatingActionButton mFab;
    public static final int LAUNCH_QR_ACTIVITY_FOR_RESULT=11111;
    DatabaseReference refUserId;
    DatabaseReference refProductKey;
    String pk;
    String emailEncoded;
    public static final String SHARED_PREF="sharedPrefs";
    public static final String USERID="userId";
    SharedPreferences sharedPreferences;
    ArrayList<CentralModule> centralModules=new ArrayList<>();
    public static final String SIGNEDIN="signedIn";
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mFab=findViewById(R.id.add_fab);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        //getSupportActionBar().hide();
        toolbar=findViewById(R.id.toolBar);
        //toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);
        mAuth=FirebaseAuth.getInstance();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main2Activity.this, QrScannerActivity.class);
                startActivityForResult(i, LAUNCH_QR_ACTIVITY_FOR_RESULT);
            }
        });
        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        if(sharedPreferences!=null) {
            emailEncoded = sharedPreferences.getString(USERID, "F");
        }
        refUserId= FirebaseDatabase.getInstance().getReference().child("usersId");
        refProductKey=FirebaseDatabase.getInstance().getReference().child("productKeys");
        refUserId.child(emailEncoded).child("productKeys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                centralModules.clear();
                for(DataSnapshot artistSnapshot:snapshot.getChildren()){

                    CentralModule pk=artistSnapshot.getValue(CentralModule.class);
                    centralModules.add(pk);

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
                        CentralModule cm=new CentralModule("untitled",pk);
                        refUserId.child(emailEncoded).child("productKeys").child(pk).setValue(cm);
                        //refProductKey.child(pk).child("Name").setValue("Untitled");
                        SwitchBoard switchBoard=new SwitchBoard("Title","name1","name2","name3","name4","name5","name6","name7","name8");
                        for(int i=0;i<25;i++){
                            refProductKey.child(pk).child("switchBoards").child(""+i).setValue(switchBoard);
                        }
                        String kp= "";
                        String tmp="01010101";
                        for(int i=0;i<25;i++){
                            kp=kp.concat(tmp);
                        }
                        String kpid=refProductKey.child(pk).child("keyPos").push().getKey();
                        refProductKey.child(pk).child("keyPos").child(kpid).setValue(kp);
                        Toast.makeText(Main2Activity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Main2Activity.this, "Product key already used by some other user", Toast.LENGTH_SHORT).show();
                    }
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
        final SharedPreferences.Editor editor=sharedPreferences.edit();
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                editor.putBoolean(SIGNEDIN,false);
                editor.commit();
                Intent intent=new Intent(Main2Activity.this,SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return (true);
        }
        return true;
    }
}
