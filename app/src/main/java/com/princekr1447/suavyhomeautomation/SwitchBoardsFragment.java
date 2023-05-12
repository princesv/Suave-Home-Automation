package com.princekr1447.suavyhomeautomation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    FloatingActionButton buttonEditCentralModuleName;
    String emailEncoded;
    Activity context;
    FloatingActionButton btnAddRoom;
    ArrayList<RoomPojo> rooms;
    ArrayList<ArrayList<IndexPojo>> indicesArrayList;
    ArrayList<ArrayList<IndexPojo>> liveIndicesArrayList;
    ExpandableRoomListAdapter expandableRoomListAdapter;
    CentralModule centralModule;
    FloatingActionButton mainFab;
    boolean fabClicked;
    TextView tv1,tv2;
    public SwitchBoardsFragment(String productKey,CentralModule centralModule,String emailEncoded,Activity context) {
        this.productKey = productKey;
        this.emailEncoded=emailEncoded;
        this.context=context;
        this.centralModule=centralModule;
    }

    public SwitchBoardsFragment(int contentLayoutId, String productKey) {
        super(contentLayoutId);
        this.productKey = productKey;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.switch_board_fragment_view,container,false);
        fabClicked=false;
        switchBoardList=new ArrayList<>();
        rooms=new ArrayList<>();
        indicesArrayList=new ArrayList<>();
        liveIndicesArrayList=new ArrayList<>();
        btnAddRoom=view.findViewById(R.id.btn_add_room);
        tv1=view.findViewById(R.id.tv1);
        tv2=view.findViewById(R.id.tv2);
        mainFab=view.findViewById(R.id.main_fab);
        expandableListViewSwitcheBoards=view.findViewById(R.id.expandableListRooms);
        buttonEditCentralModuleName=view.findViewById(R.id.buttonEditCentralModuleName);
        refUserId= FirebaseDatabase.getInstance().getReference().child("usersId");
        refProductKey=FirebaseDatabase.getInstance().getReference().child("productKeys");
        refKeyPos=FirebaseDatabase.getInstance().getReference().child("productKeys").child(productKey).child("keyPos");
        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility();
                setAnimation();
                fabClicked=!fabClicked;
            }
        });
        refProductKey.child(productKey).child("keyPos").orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot cmSnapshot:snapshot.getChildren()) {
                    keyPos = cmSnapshot.getValue(String.class);
                }
                if(switchBoardList.size()!=0&&rooms.size()!=0&&indicesArrayList.size()!=0){
                    AsyncTaskKeyPosUpdated asyncTaskKeyPosUpdated=new AsyncTaskKeyPosUpdated();
                    asyncTaskKeyPosUpdated.execute("0");
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
                if(keyPos!=null&&rooms.size()!=0&&indicesArrayList.size()!=0) {
                    //updateUi(container);
                  /*  int index = listViewSwitcheBoards.getFirstVisiblePosition();
                    View v = listViewSwitcheBoards.getChildAt(0);
                    int top = (v == null) ? 0 : (v.getTop() - listViewSwitcheBoards.getPaddingTop());
                    SwitchBoardListAdapter adapter=new SwitchBoardListAdapter(getActivity(),switchBoardList,keyPos,refKeyPos,productKey);
                    listViewSwitcheBoards.setAdapter(adapter);
                    listViewSwitcheBoards.setSelectionFromTop(index, top);

                   */
                    if(expandableRoomListAdapter==null) {
                        expandableRoomListAdapter = new ExpandableRoomListAdapter(productKey, rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey, liveIndicesArrayList);
                        expandableListViewSwitcheBoards.setAdapter(expandableRoomListAdapter);
                    }else{
                        expandableRoomListAdapter.dataSetChanged(rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,liveIndicesArrayList);
                    }


                   // expandableRoomListAdapter = new ExpandableRoomListAdapter(productKey,rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,indicesArrayList);
                   // expandableListViewSwitcheBoards.setAdapter(expandableRoomListAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refProductKey.child(productKey).child("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AsyncTaskGetRoomsCollection asyncTaskGetRoomsCollection= new AsyncTaskGetRoomsCollection();
                asyncTaskGetRoomsCollection.execute(snapshot);
                if(keyPos!=null&&rooms.size()!=0&&switchBoardList.size()!=0) {
                   /* if(expandableRoomListAdapter==null) {
                        expandableRoomListAdapter = new ExpandableRoomListAdapter(productKey,rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,indicesArrayList);
                        expandableListViewSwitcheBoards.setAdapter(expandableRoomListAdapter);
                    }else{
                          expandableRoomListAdapter.dataSetChanged(rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,indicesArrayList);
                    }

                    */
                   // AsyncTaskKeyPosUpdated asyncTaskKeyPosUpdated=new AsyncTaskKeyPosUpdated();
                   // asyncTaskKeyPosUpdated.execute("1");
                   // expandableRoomListAdapter = new ExpandableRoomListAdapter(productKey,rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey,liveIndicesArrayList);
                   // expandableListViewSwitcheBoards.setAdapter(expandableRoomListAdapter);
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
                final View dialogView=inflater.inflate(R.layout.room_dialog,null);
                dialogBuilder.setView(dialogView);
                final EditText newRoomTitleEditText=dialogView.findViewById(R.id.newRoomTitle);
                Button btn=dialogView.findViewById(R.id.btnRoomDialog);
                btn.setText(R.string.create_room);
                final AlertDialog alertDialog=dialogBuilder.create();
                alertDialog.show();
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(newRoomTitleEditText.getText().equals("")){
                            Toast.makeText(getContext(), "Text field empty!", Toast.LENGTH_SHORT).show();
                        }else{
                            String key=refProductKey.child(productKey).child("rooms").push().getKey();
                            if(key!=null) {
                                RoomPojo roomPojo=new RoomPojo(newRoomTitleEditText.getText().toString(),null ,key);
                              //  refProductKey.child(productKey).child("rooms").child(key).child("title").setValue(newRoomTitleEditText.getText().toString());
                               // refProductKey.child(productKey).child("rooms").child(key).child("id").setValue(key);
                                refProductKey.child(productKey).child("rooms").child(key).setValue(roomPojo);
                                Toast.makeText(getContext(), "Room created Successfully!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "Failed to create room. try again!", Toast.LENGTH_SHORT).show();
                            }
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
                            CentralModule cmUpdated=new CentralModule(s1,productKey,centralModule.id);
                            refUserId.child(emailEncoded).child("productKeys").child(centralModule.getId()).setValue(cmUpdated);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        return view;
    }
    void setVisibility(){
        if(!fabClicked){
            btnAddRoom.setVisibility(View.VISIBLE);
            buttonEditCentralModuleName.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
        }else{
              btnAddRoom.setVisibility(View.INVISIBLE);
            buttonEditCentralModuleName.setVisibility(View.INVISIBLE);
            tv1.setVisibility(View.INVISIBLE);
            tv2.setVisibility(View.INVISIBLE);
        }
    }
    void setAnimation(){
        if(!fabClicked){
            Animation animation1= AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim);
            btnAddRoom.startAnimation(animation1);
            buttonEditCentralModuleName.startAnimation(animation1);
            tv1.setAnimation(animation1);
            tv2.setAnimation(animation1);
            Animation animation2= AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim);
            mainFab.startAnimation(animation2);
        }else{
            Animation animation1= AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim);
            btnAddRoom.startAnimation(animation1);
            buttonEditCentralModuleName.startAnimation(animation1);
            tv1.setAnimation(animation1);
            tv2.setAnimation(animation1);
            Animation animation2= AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim);
            mainFab.startAnimation(animation2);
        }
    }
    private class AsyncTaskKeyPosUpdated extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            liveIndicesArrayList.clear();
            for(int i=0;i<indicesArrayList.size();i++){
                ArrayList<IndexPojo> tmp=new ArrayList<>();
                if(indicesArrayList.get(i)==null){
                    liveIndicesArrayList.add(null);
                    continue;
                }
                for(int j=0;j<indicesArrayList.get(i).size();j++){
                    int position=indicesArrayList.get(i).get(j).getValue();
                    boolean ff=true;
                    for(int k=8*position;k<8*position+8;k++){
                        if(keyPos.charAt(k)!='2'){
                            ff=false;
                            break;
                        }
                    }
                    if(ff){
                        continue;
                    }
                    tmp.add(indicesArrayList.get(i).get(j));
                }
                if(tmp.size()==0){
                    liveIndicesArrayList.add(null);
                }else {
                    liveIndicesArrayList.add(tmp);
                }
            }
            return strings[0];
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("0")) {
                if (expandableRoomListAdapter == null) {
                    expandableRoomListAdapter = new ExpandableRoomListAdapter(productKey, rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey, liveIndicesArrayList);
                    expandableListViewSwitcheBoards.setAdapter(expandableRoomListAdapter);
                } else {
                    expandableRoomListAdapter.dataSetChanged(rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey, liveIndicesArrayList);
                }
            }else{
                expandableRoomListAdapter = new ExpandableRoomListAdapter(productKey, rooms, context, keyPos, switchBoardList, refKeyPos, refProductKey, liveIndicesArrayList);
                expandableListViewSwitcheBoards.setAdapter(expandableRoomListAdapter);
            }
        }
    }
    private class AsyncTaskGetRoomsCollection extends AsyncTask<DataSnapshot, String, String>{

        @Override
        protected String doInBackground(DataSnapshot... snapshots) {
            DataSnapshot snapshot=snapshots[0];
            rooms.clear();
            indicesArrayList.clear();
            for(DataSnapshot artistSnapshot:snapshot.getChildren()){
                RoomPojo room=artistSnapshot.getValue(RoomPojo.class);
                rooms.add(room);
                if(room.getIndices()==null){
                    indicesArrayList.add(null);
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
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AsyncTaskKeyPosUpdated asyncTaskKeyPosUpdated=new AsyncTaskKeyPosUpdated();
            asyncTaskKeyPosUpdated.execute("1");
        }
    }
}
