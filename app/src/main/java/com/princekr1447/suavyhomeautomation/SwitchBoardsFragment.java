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
import android.widget.ExpandableListView;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class SwitchBoardsFragment extends Fragment {
    String productKey;
    DatabaseReference refKeyPos;
    DatabaseReference refSwitchInfo;
    DatabaseReference refUserId;
    DatabaseReference refProductKey;
    String keyPos;
    ArrayList<SwitchBoard> switchBoardList;
    ExpandableListView expandableListViewSwitcheBoards;
    ImageButton buttonEditCentralModuleName;
    String emailEncoded;
    Activity context;
    Button btnAddRoom;
    ArrayList<RoomPojo> rooms;
    ArrayList<ArrayList<IndexPojo>> indicesArrayList;
    ExpandableRoomListAdapter expandableRoomListAdapter;
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
        rooms=new ArrayList<>();
        indicesArrayList=new ArrayList<>();
        btnAddRoom=view.findViewById(R.id.btn_add_room);
        expandableListViewSwitcheBoards=view.findViewById(R.id.expandableListRooms);
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
                if(switchBoardList!=null&&rooms!=null){
                    //updateUi(container);
                  /*  int index = listViewSwitcheBoards.getFirstVisiblePosition();
                    View v = listViewSwitcheBoards.getChildAt(0);
                    int top = (v == null) ? 0 : (v.getTop() - listViewSwitcheBoards.getPaddingTop());
                    SwitchBoardListAdapter adapter=new SwitchBoardListAdapter(getActivity(),switchBoardList,keyPos,refKeyPos,productKey);
                    listViewSwitcheBoards.setAdapter(adapter);
                    listViewSwitcheBoards.setSelectionFromTop(index, top);

                   */
                    if(expandableRoomListAdapter==null) {
                        expandableRoomListAdapter = new ExpandableRoomListAdapter(productKey,rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,indicesArrayList);
                        expandableListViewSwitcheBoards.setAdapter(expandableRoomListAdapter);
                    }else{
                        expandableRoomListAdapter.dataSetChanged(rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,indicesArrayList);
                    }
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
                if(keyPos!=null&&rooms!=null) {
                    //updateUi(container);
                  /*  int index = listViewSwitcheBoards.getFirstVisiblePosition();
                    View v = listViewSwitcheBoards.getChildAt(0);
                    int top = (v == null) ? 0 : (v.getTop() - listViewSwitcheBoards.getPaddingTop());
                    SwitchBoardListAdapter adapter=new SwitchBoardListAdapter(getActivity(),switchBoardList,keyPos,refKeyPos,productKey);
                    listViewSwitcheBoards.setAdapter(adapter);
                    listViewSwitcheBoards.setSelectionFromTop(index, top);

                   */
                    if(expandableRoomListAdapter==null) {
                        expandableRoomListAdapter = new ExpandableRoomListAdapter(productKey,rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,indicesArrayList);
                        expandableListViewSwitcheBoards.setAdapter(expandableRoomListAdapter);
                    }else{
                        expandableRoomListAdapter.dataSetChanged(rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,indicesArrayList);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refProductKey.child(productKey).child("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms.clear();
                indicesArrayList.clear();
                for(DataSnapshot artistSnapshot:snapshot.getChildren()){
                    RoomPojo room=artistSnapshot.getValue(RoomPojo.class);
                    rooms.add(room);
                    if(room.getIndices()==null){
                        continue;
                    }
                    Collection<IndexPojo> indicesCollection=room.indices.values();
                    ArrayList<IndexPojo> indices=new ArrayList<>();
                    for (IndexPojo index : indicesCollection){
                        indices.add(index);
                    }
                    Collections.sort(indices, new Comparator<IndexPojo>(){
                        public int compare(IndexPojo obj1, IndexPojo obj2) {
                            // ## Ascending order
                            return obj1.getKey().compareToIgnoreCase(obj2.getKey()); // To compare string values
                            // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                            // ## Descending order
                            // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                            // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                        }
                    });
                    indicesArrayList.add(indices);
                }
                if(keyPos!=null&&switchBoardList!=null) {
                    if(expandableRoomListAdapter==null) {
                        expandableRoomListAdapter = new ExpandableRoomListAdapter(productKey,rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,indicesArrayList);
                        expandableListViewSwitcheBoards.setAdapter(expandableRoomListAdapter);
                    }else{
                        expandableRoomListAdapter.dataSetChanged(rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,indicesArrayList);
                    }
                }
                /*RoomAdapter roomAdapter = new RoomAdapter(rooms,context,keyPos,switchBoardList,refKeyPos);
                listViewSwitcheBoards.setAdapter(roomAdapter);
                listViewSwitcheBoards.setHasFixedSize(true);

                 */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(context);
                LayoutInflater inflater=context.getLayoutInflater();
                final View dialogView=inflater.inflate(R.layout.add_room_dialog,null);
                dialogBuilder.setView(dialogView);
                final EditText newRoomTitleEditText=dialogView.findViewById(R.id.newRoomTitle);
                Button createNewRoomButton=dialogView.findViewById(R.id.btnCreateNewRoom);
                final AlertDialog alertDialog=dialogBuilder.create();
                alertDialog.show();
                createNewRoomButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(newRoomTitleEditText.getText().equals("")){
                            Toast.makeText(getContext(), "Text field empty!", Toast.LENGTH_SHORT).show();
                        }else{
                            String key=refProductKey.child(productKey).child("rooms").push().getKey();
                            refProductKey.child(productKey).child("rooms").child(key).child("title").setValue(newRoomTitleEditText.getText().toString());
                            refProductKey.child(productKey).child("rooms").child(key).child("id").setValue(key);
                            Toast.makeText(getContext(), "Room created Successfully!", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
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
