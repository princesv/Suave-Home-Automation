package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    String emailEncoded;
    String productKey;
    DatabaseReference refKeyPos;
    DatabaseReference refSwitchInfo;
    DatabaseReference refUserId;
    DatabaseReference refProductKey;
    String keyPos;
    List<SwitchBoard> switchBoardList;
    ListView listViewSwitcheBoards;
    int firstVisible_position=0;
    int top=0;
    List<String> datass = new ArrayList<>();
    FloatingActionButton mFab;
    final String urlString="https://suavy-home-automation-913b1-default-rtdb.firebaseio.com/usersId/";
    public static final String USERID="userId";
    String PRODUCTKEY_KEY="ProductKeyId1236";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchBoardList=new ArrayList<>();
        listViewSwitcheBoards=findViewById(R.id.listViewSwitchBoards);
        productKey= "";
        String tmp="01010101";
        for(int i=0;i<25;i++){
            productKey=productKey.concat(tmp);
        }
        Intent intent=getIntent();
        productKey=intent.getStringExtra(PRODUCTKEY_KEY);
        refUserId=FirebaseDatabase.getInstance().getReference().child("usersId");
        refProductKey=FirebaseDatabase.getInstance().getReference().child("productKeys");
        refKeyPos=FirebaseDatabase.getInstance().getReference().child("productKeys").child(productKey).child("keyPos");
        listViewSwitcheBoards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SwitchBoard switchBoard=switchBoardList.get(position);
                showUpdateDialog(position,switchBoard.title);
                return true;
            }
        });
        refProductKey.child(productKey).child("keyPos").orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot cmSnapshot:snapshot.getChildren()) {
                    keyPos = cmSnapshot.getValue(String.class);
                }
                if(switchBoardList!=null){
                    updateUi();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        refProductKey.child(productKey).child("switchBoards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                switchBoardList.clear();
                for(DataSnapshot artistSnapshot:snapshot.getChildren()){

                    SwitchBoard switchBoard=artistSnapshot.getValue(SwitchBoard.class);
                    switchBoardList.add(switchBoard);

                }
                if(keyPos!=null) {
                    updateUi();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void updateUi(){
        SwitchBoardListAdapter adapter=new SwitchBoardListAdapter(MainActivity.this,switchBoardList,keyPos,refKeyPos,productKey);
      //  listViewSwitcheBoards.setAdapter(adapter);
       // listViewSwitcheBoards.setSelectionFromTop(firstVisible_position, top);
    }
    private void showUpdateDialog(final int pos, String switchBoardTitle){
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.update_dialog,null);

        dialogBuilder.setView(dialogView);
        final EditText ets1=dialogView.findViewById(R.id.dialogSwitch1);
        final EditText ets2=dialogView.findViewById(R.id.dialogSwitch2);
        final EditText ets3=dialogView.findViewById(R.id.dialogSwitch3);
        final EditText ets4=dialogView.findViewById(R.id.dialogSwitch4);
        final EditText ets5=dialogView.findViewById(R.id.dialogSwitch5);
        final EditText ets6=dialogView.findViewById(R.id.dialogSwitch6);
        final EditText ets7=dialogView.findViewById(R.id.dialogSwitch7);
        final EditText ets8=dialogView.findViewById(R.id.dialogSwitch8);
        final EditText etTitle=dialogView.findViewById(R.id.dialogTitle);
        final Button updateButton=dialogView.findViewById(R.id.dialogButton);
        etTitle.setText(switchBoardList.get(pos).title);
        ets1.setText(switchBoardList.get(pos).name1);
        ets2.setText(switchBoardList.get(pos).name2);
        ets3.setText(switchBoardList.get(pos).name3);
        ets4.setText(switchBoardList.get(pos).name4);
        ets5.setText(switchBoardList.get(pos).name5);
        ets6.setText(switchBoardList.get(pos).name6);
        ets7.setText(switchBoardList.get(pos).name7);
        ets8.setText(switchBoardList.get(pos).name8);
        dialogBuilder.setTitle("Update "+switchBoardTitle);
        final AlertDialog alertDialog=dialogBuilder.create();
        alertDialog.show();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=etTitle.getText().toString().trim();
                String s1=ets1.getText().toString().trim();
                String s2=ets2.getText().toString().trim();
                String s3=ets3.getText().toString().trim();
                String s4=ets4.getText().toString().trim();
                String s5=ets5.getText().toString().trim();
                String s6=ets6.getText().toString().trim();
                String s7=ets7.getText().toString().trim();
                String s8=ets8.getText().toString().trim();
                if(TextUtils.isEmpty(title)||TextUtils.isEmpty(s1) ||TextUtils.isEmpty(s2)||TextUtils.isEmpty(s3)||TextUtils.isEmpty(s4)
                ||TextUtils.isEmpty(s5)||TextUtils.isEmpty(s6)||TextUtils.isEmpty(s7)||TextUtils.isEmpty(s8)){
                    Toast.makeText(MainActivity.this, "One or more text fields are empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    SwitchBoard switchBoard = new SwitchBoard(title,s1,s2,s3,s4,s5,s6,s7,s8);
                    DatabaseReference dbrEdit = refSwitchInfo.child(pos+"");
                    dbrEdit.setValue(switchBoard);
                    Toast.makeText(MainActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }
}
