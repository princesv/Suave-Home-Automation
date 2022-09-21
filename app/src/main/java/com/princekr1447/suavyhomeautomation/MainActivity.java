package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
    SharedPreferences sharedPreferences;
    public static final String USERID="userId";
    DatabaseReference refKeyPos;
    DatabaseReference refSwitchInfo;
    String keyPos;
    List<SwitchBoard> switchBoardList;
    ListView listViewSwitcheBoards;
    int firstVisible_position=0;
    int top=0;
    List<String> datass = new ArrayList<>();
    final String urlString="https://suavy-home-automation-913b1-default-rtdb.firebaseio.com/usersId/";
    public static final String SHARED_PREF="sharedPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchBoardList=new ArrayList<>();
        listViewSwitcheBoards=findViewById(R.id.listViewSwitchBoards);
        sharedPreferences=getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        if(sharedPreferences!=null) {
            emailEncoded = sharedPreferences.getString(USERID, "F");
        }
        String urlStringForCall=urlString+emailEncoded+".json";
        final URL urlForNetworkCall=getUrlFromString(urlStringForCall);
        final CountDownLatch mCountDownLatch=new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String isAlreadyPresentcallResult= null;
                try {
                    isAlreadyPresentcallResult = getResponseFromHttpUrl(urlForNetworkCall);
                    datass.add(isAlreadyPresentcallResult);
                    productKey=datass.get(0).substring(1,datass.get(0).length()-1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCountDownLatch.countDown();
            }
        }).start();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        refKeyPos=FirebaseDatabase.getInstance().getReference().child("productKeys").child(productKey).child("keyPos");
        refSwitchInfo=FirebaseDatabase.getInstance().getReference().child("productKeys").child(productKey).child("SwitchBoards");
        refKeyPos.orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String res=snapshot.getValue().toString();
                int i=0;
                for(i=0;i<res.length();i++) {
                    if(res.charAt(i)=='='){
                        break;
                    }
                }
                keyPos=res.substring(i+1,res.length()-1);
                refSwitchInfo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        View v = listViewSwitcheBoards.getChildAt(0);
                        top = (v == null) ? 0 : (v.getTop() - listViewSwitcheBoards.getPaddingTop());
                        getData(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        listViewSwitcheBoards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SwitchBoard switchBoard=switchBoardList.get(position);
                showUpdateDialog(position,switchBoard.title);
                return true;
            }
        });
    }

    void getData(DataSnapshot snapshot){
        switchBoardList.clear();
        for(DataSnapshot artistSnapshot:snapshot.getChildren()){

            SwitchBoard switchBoard=artistSnapshot.getValue(SwitchBoard.class);
            switchBoardList.add(switchBoard);

        }
        SwitchBoardListAdapter adapter=new SwitchBoardListAdapter(MainActivity.this,switchBoardList,keyPos,refKeyPos,productKey);
        listViewSwitcheBoards.setAdapter(adapter);
        listViewSwitcheBoards.setSelectionFromTop(firstVisible_position, top);
    }
    URL getUrlFromString(String stringUrl){
        try {
            URL urlToFetchData = new URL(stringUrl);
            return urlToFetchData;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
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
