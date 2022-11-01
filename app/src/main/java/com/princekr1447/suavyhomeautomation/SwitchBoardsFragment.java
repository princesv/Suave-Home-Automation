package com.princekr1447.suavyhomeautomation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class SwitchBoardsFragment extends Fragment {
    String productKey;
    DatabaseReference refKeyPos;
    DatabaseReference refSwitchInfo;
    DatabaseReference refUserId;
    DatabaseReference refProductKey;
    String keyPos;
    List<SwitchBoard> switchBoardList;
    ListView listViewSwitcheBoards;
    ImageButton buttonEditCentralModuleName;
    String emailEncoded;
    int firstVisible_position=0;
    int top=0;
    Activity context;
    String PRODUCTKEY_KEY="ProductKeyId1236";
    public SwitchBoardsFragment(String productKey,String emailEncoded,Activity context) {
        this.productKey = productKey;
        this.emailEncoded=emailEncoded;
        this.context=context;
    }

    public SwitchBoardsFragment(int contentLayoutId, String productKey) {
        super(contentLayoutId);
        this.productKey = productKey;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.switch_board_fragment_view,container,false);
        switchBoardList=new ArrayList<>();
        listViewSwitcheBoards=view.findViewById(R.id.listViewSwitchBoardsFragment);
        buttonEditCentralModuleName=view.findViewById(R.id.buttonEditCentralModuleName);
        refUserId= FirebaseDatabase.getInstance().getReference().child("usersId");
        refProductKey=FirebaseDatabase.getInstance().getReference().child("productKeys");
        refKeyPos=FirebaseDatabase.getInstance().getReference().child("productKeys").child(productKey).child("keyPos");
        refProductKey.child(productKey).child("keyPos").orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot cmSnapshot:snapshot.getChildren()) {
                    keyPos = cmSnapshot.getValue(String.class);
                }
                if(switchBoardList.size()!=0){
                    //updateUi(container);
                    SwitchBoardListAdapter adapter=new SwitchBoardListAdapter(getActivity(),switchBoardList,keyPos,refKeyPos,productKey);
                    listViewSwitcheBoards.setAdapter(adapter);
                    listViewSwitcheBoards.setSelectionFromTop(firstVisible_position, top);
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
                    //updateUi(container);
                    SwitchBoardListAdapter adapter=new SwitchBoardListAdapter(getActivity(),switchBoardList,keyPos,refKeyPos,productKey);
                    listViewSwitcheBoards.setAdapter(adapter);
                    listViewSwitcheBoards.setSelectionFromTop(firstVisible_position, top);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        buttonEditCentralModuleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(context);
                LayoutInflater inflater=context.getLayoutInflater();
                final View dialogView=inflater.inflate(R.layout.central_module_update_dialog,null);
                dialogBuilder.setView(dialogView);
                final EditText editText=dialogView.findViewById(R.id.cm_name);
                final Button cmUpdateButton=dialogView.findViewById(R.id.cm_update_button);
                final AlertDialog alertDialog=dialogBuilder.create();
                alertDialog.show();
                cmUpdateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s1=editText.getText().toString();
                        if(TextUtils.isEmpty(s1)){
                            Toast.makeText(getContext(), "Text field empty!", Toast.LENGTH_SHORT).show();
                        }else{
                            //   CentralModule cm=new CentralModule(centralModules.get(position).getProductKey(),s1);
                            CentralModule cmUpdated=new CentralModule(s1,productKey);
                            refUserId.child(emailEncoded).child("productKeys").child(productKey).setValue(cmUpdated);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        return view;
    }
}
