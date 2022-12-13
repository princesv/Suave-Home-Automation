package com.princekr1447.suavyhomeautomation;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableRoomListAdapter extends BaseExpandableListAdapter {
    ArrayList<RoomPojo> rooms;
    ArrayList<ArrayList<IndexPojo>> indicesArrayList;
    Activity context;
    String keyPos;
    ArrayList<SwitchBoard> switchBoardList;
    String productKey;
    DatabaseReference refKeyPos;
    DatabaseReference refProductKey;
    public ExpandableRoomListAdapter(String productKey,ArrayList<RoomPojo> rooms, Activity context,String keyPos, ArrayList<SwitchBoard> switchBoardList,DatabaseReference refKeyPos,DatabaseReference refProductKey,ArrayList<ArrayList<IndexPojo>> indicesArrayList){
        this.rooms=rooms;
        this.context=context;
        this.keyPos=keyPos;
        this.switchBoardList=switchBoardList;
        this.refKeyPos=refKeyPos;
        this.refProductKey=refProductKey;
        this.productKey=productKey;
        this.indicesArrayList=indicesArrayList;
    }

    @Override
    public int getGroupCount() {
        return rooms.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(indicesArrayList.get(groupPosition)==null){
            return 0;
        }
        return indicesArrayList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.room_card_view, parent, false);
        TextView roomTitle=convertView.findViewById(R.id.roomTitle);
        roomTitle.setText(rooms.get(groupPosition).getTitle());
        Button editButton=convertView.findViewById(R.id.editRoomButton);
        if(groupPosition==0){
            editButton.setVisibility(View.GONE);
            return convertView;
        }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_room_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.renameRoom:
                                //handle menu1 click
                                renameRoom(groupPosition);
                                return true;
                            case R.id.deleteRoom:
                                //handle menu2 click
                                deleteRoom(groupPosition);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
        return convertView;
    }
    private void renameRoom(final int groupPosition){
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(context);
        LayoutInflater inflater=context.getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.room_dialog,null);
        dialogBuilder.setView(dialogView);
        final EditText newRoomTitleEditText=dialogView.findViewById(R.id.newRoomTitle);
        Button btn=dialogView.findViewById(R.id.btnRoomDialog);
        btn.setText(R.string.rename_text);
        final AlertDialog alertDialog=dialogBuilder.create();
        alertDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newRoomTitleEditText.getText().equals("")){
                    Toast.makeText(context, "Text field empty!", Toast.LENGTH_SHORT).show();
                }else{
                    refProductKey.child(productKey).child("rooms").child(rooms.get(groupPosition).getId()).child("title").setValue(newRoomTitleEditText.getText().toString());
                    Toast.makeText(context, "Room renamed Successfully!", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }
    private  void deleteRoom(final int groupPosition){
        AsyncTaskDeleteRoom asyncTaskDeleteRoom=new AsyncTaskDeleteRoom();
        asyncTaskDeleteRoom.execute(groupPosition);
    }
    private class AsyncTaskDeleteRoom extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... groupPosition) {
            ArrayList<IndexPojo> indices=indicesArrayList.get(groupPosition[0]);
            if(indices==null){
                refProductKey.child(productKey).child("rooms").child(rooms.get(groupPosition[0]).getId()).child("indices").removeValue();
                Toast.makeText(context, "Room deleted successfully", Toast.LENGTH_SHORT).show();
            }
            for(IndexPojo index:indices){
                String key=refProductKey.child(productKey).child("rooms").child(rooms.get(0).getId()).child("indices").push().getKey();
                IndexPojo newIndex=new IndexPojo(key,index.getValue());
                refProductKey.child(productKey).child("rooms").child(rooms.get(0).getId()).child("indices").child(key).setValue(newIndex);
            }
            refProductKey.child(productKey).child("rooms").child(rooms.get(groupPosition[0]).getId()).removeValue();
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(context, "Room deleted successfully. All switchboards added to stand alone list", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View listViewItem, ViewGroup parent) {
        listViewItem = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        boolean f=true;
        TextView title=listViewItem.findViewById(R.id.board_title);
        TextView switch1=listViewItem.findViewById(R.id.switchName1);
        TextView switch2=listViewItem.findViewById(R.id.switchName2);
        TextView switch3=listViewItem.findViewById(R.id.switchName3);
        TextView switch4=listViewItem.findViewById(R.id.switchName4);
        TextView switch5=listViewItem.findViewById(R.id.switchName5);
        TextView switch6=listViewItem.findViewById(R.id.switchName6);
        TextView switch7=listViewItem.findViewById(R.id.switchName7);
        TextView switch8=listViewItem.findViewById(R.id.switchName8);
        SwitchCompat tb1=listViewItem.findViewById(R.id.toggleButton1);
        SwitchCompat tb2=listViewItem.findViewById(R.id.toggleButton2);
        SwitchCompat tb3=listViewItem.findViewById(R.id.toggleButton3);
        SwitchCompat tb4=listViewItem.findViewById(R.id.toggleButton4);
        SwitchCompat tb5=listViewItem.findViewById(R.id.toggleButton5);
        SwitchCompat tb6=listViewItem.findViewById(R.id.toggleButton6);
        SwitchCompat tb7=listViewItem.findViewById(R.id.toggleButton7);
        SwitchCompat tb8=listViewItem.findViewById(R.id.toggleButton8);
        ImageButton buttonEdit=listViewItem.findViewById(R.id.buttonEdit);
        final int position=indicesArrayList.get(groupPosition).get(childPosition).getValue();
        for(int i=8*position;i<8*position+8;i++){
            if(keyPos.charAt(i)!='2'){
                f=false;
                break;
            }
        }
        SwitchBoard switchBoard=switchBoardList.get(position);
        title.setVisibility(View.VISIBLE);
        buttonEdit.setVisibility(View.VISIBLE);
        title.setText(switchBoard.getTitle());
        switch1.setText(switchBoard.getName1());
        switch2.setText(switchBoard.getName2());
        switch3.setText(switchBoard.getName3());
        switch4.setText(switchBoard.getName4());
        switch5.setText(switchBoard.getName5());
        switch6.setText(switchBoard.getName6());
        switch7.setText(switchBoard.getName7());
        switch8.setText(switchBoard.getName8());
        if(keyPos.charAt(8*position)=='1'){
            tb1.setVisibility(View.VISIBLE);
            switch1.setVisibility(View.VISIBLE);
            tb1.setChecked(true);
        }else if(keyPos.charAt(8*position)=='0'){
            tb1.setVisibility(View.VISIBLE);
            switch1.setVisibility(View.VISIBLE);
            tb1.setChecked(false);
        }else{
            tb1.setVisibility(View.GONE);
            switch1.setVisibility(View.GONE);
        }
        if(keyPos.charAt(8*position+1)=='1'){
            tb2.setVisibility(View.VISIBLE);
            switch2.setVisibility(View.VISIBLE);
            tb2.setChecked(true);
        }else if(keyPos.charAt(8*position+1)=='0'){
            tb2.setVisibility(View.VISIBLE);
            switch2.setVisibility(View.VISIBLE);
            tb2.setChecked(false);
        }else{
            tb2.setVisibility(View.GONE);
            switch2.setVisibility(View.GONE);
        }
        if(keyPos.charAt(8*position+2)=='1'){
            tb3.setVisibility(View.VISIBLE);
            switch3.setVisibility(View.VISIBLE);
            tb3.setChecked(true);
        }else if(keyPos.charAt(8*position+2)=='0'){
            tb3.setVisibility(View.VISIBLE);
            switch3.setVisibility(View.VISIBLE);
            tb3.setChecked(false);
        }else{
            tb3.setVisibility(View.GONE);
            switch3.setVisibility(View.GONE);
        }
        if(keyPos.charAt(8*position+3)=='1'){
            tb4.setVisibility(View.VISIBLE);
            switch4.setVisibility(View.VISIBLE);
            tb4.setChecked(true);
        }else if(keyPos.charAt(8*position+3)=='0'){
            tb4.setVisibility(View.VISIBLE);
            switch4.setVisibility(View.VISIBLE);
            tb4.setChecked(false);
        }else{
            tb4.setVisibility(View.GONE);
            switch4.setVisibility(View.GONE);
        }
        if(keyPos.charAt(8*position+4)=='1'){
            tb5.setVisibility(View.VISIBLE);
            switch5.setVisibility(View.VISIBLE);
            tb5.setChecked(true);
        }else if(keyPos.charAt(8*position+4)=='0'){
            tb5.setVisibility(View.VISIBLE);
            switch5.setVisibility(View.VISIBLE);
            tb5.setChecked(false);
        }else{
            tb5.setVisibility(View.GONE);
            switch5.setVisibility(View.GONE);
        }
        if(keyPos.charAt(8*position+5)=='1'){
            tb6.setVisibility(View.VISIBLE);
            switch6.setVisibility(View.VISIBLE);
            tb6.setChecked(true);
        }else if(keyPos.charAt(8*position+5)=='0'){
            tb6.setVisibility(View.VISIBLE);
            switch6.setVisibility(View.VISIBLE);
            tb6.setChecked(false);
        }else{
            tb6.setVisibility(View.GONE);
            switch6.setVisibility(View.GONE);
        }
        if(keyPos.charAt(8*position+6)=='1'){
            tb7.setVisibility(View.VISIBLE);
            switch7.setVisibility(View.VISIBLE);
            tb7.setChecked(true);
        }else if(keyPos.charAt(8*position+6)=='0'){
            tb7.setVisibility(View.VISIBLE);
            switch7.setVisibility(View.VISIBLE);
            tb7.setChecked(false);
        }else{
            tb7.setVisibility(View.GONE);
            switch7.setVisibility(View.GONE);
        }
        if(keyPos.charAt(8*position+7)=='1'){
            tb8.setVisibility(View.VISIBLE);
            switch8.setVisibility(View.VISIBLE);
            tb8.setChecked(true);
        }else if(keyPos.charAt(8*position+7)=='0'){
            tb8.setVisibility(View.VISIBLE);
            switch8.setVisibility(View.VISIBLE);
            tb8.setChecked(false);
        }else{
            tb8.setVisibility(View.GONE);
            switch8.setVisibility(View.GONE);
        }
        if(f){
            title.setVisibility(View.GONE);
            buttonEdit.setVisibility(View.GONE);
            listViewItem.setVisibility(View.GONE);
            return listViewItem;
        }
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchBoard switchBoard=switchBoardList.get(position);
                showUpdateDialog(childPosition,switchBoard.title,groupPosition);
            }
        });
        tb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String newKeyPos;
                if(isChecked){
                    newKeyPos="";
                    if(position==0){
                        newKeyPos='1'+keyPos.substring(1);
                    }else{
                        newKeyPos=keyPos.substring(0,8*position)+'1'+keyPos.substring(8*position+1);
                    }

                }else{
                    newKeyPos="";
                    if(position==0){
                        newKeyPos='0'+keyPos.substring(1);
                    }else{
                        newKeyPos=keyPos.substring(0,8*position)+'0'+keyPos.substring(8*position+1);
                    }
                }
                refKeyPos.push().setValue(newKeyPos);
            }
        });
        tb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+1)+'1'+keyPos.substring(8*position+1+1);
                    refKeyPos.push().setValue(newKeyPos);
                }else{
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+1)+'0'+keyPos.substring(8*position+1+1);
                    refKeyPos.push().setValue(newKeyPos);
                }
            }
        });
        tb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+2)+'1'+keyPos.substring(8*position+2+1);
                    refKeyPos.push().setValue(newKeyPos);
                }else{
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+2)+'0'+keyPos.substring(8*position+2+1);
                    refKeyPos.push().setValue(newKeyPos);
                }
            }
        });
        tb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+3)+'1'+keyPos.substring(8*position+3+1);
                    refKeyPos.push().setValue(newKeyPos);
                }else{
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+3)+'0'+keyPos.substring(8*position+3+1);
                    refKeyPos.push().setValue(newKeyPos);
                }
            }
        });
        tb5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+4)+'1'+keyPos.substring(8*position+4+1);
                    refKeyPos.push().setValue(newKeyPos);
                }else{
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+4)+'0'+keyPos.substring(8*position+4+1);
                    refKeyPos.push().setValue(newKeyPos);
                }
            }
        });
        tb6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+5)+'1'+keyPos.substring(8*position+5+1);
                    refKeyPos.push().setValue(newKeyPos);
                }else{
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+5)+'0'+keyPos.substring(8*position+5+1);
                    refKeyPos.push().setValue(newKeyPos);
                }
            }
        });tb7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+6)+'1'+keyPos.substring(8*position+6+1);
                    refKeyPos.push().setValue(newKeyPos);
                }else{
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+6)+'0'+keyPos.substring(8*position+6+1);
                    refKeyPos.push().setValue(newKeyPos);
                }
            }
        });
        tb8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+7)+'1'+keyPos.substring(8*position+7+1);
                    refKeyPos.push().setValue(newKeyPos);
                }else{
                    String newKeyPos="";
                    newKeyPos=keyPos.substring(0,8*position+7)+'0'+keyPos.substring(8*position+7+1);
                    refKeyPos.push().setValue(newKeyPos);
                }
            }
        });
        return listViewItem;
    }
    private void showUpdateDialog(final int pos, final String switchBoardTitle, final int groupPosition){
        final int positionInitial=groupPosition;
        final int[] positionFinal = {positionInitial};
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(context);
        LayoutInflater inflater=LayoutInflater.from(context);
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
        Spinner dropdown = dialogView.findViewById(R.id.spinner1);
        /*ArrayList<String> items = new ArrayList<>();
        for(int i=0;i<rooms.size();i++){
            items.add(rooms.get(i).getTitle());
        }

         */
        ArrayAdapter adapter;
        ArrayList<String> roomNames=new ArrayList<>();
        for(RoomPojo room: rooms){
            roomNames.add(room.getTitle());
        }
        adapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, roomNames);
        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionFinal[0] =position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etTitle.setText(switchBoardList.get(indicesArrayList.get(groupPosition).get(pos).getValue()).title);
        ets1.setText(switchBoardList.get(indicesArrayList.get(groupPosition).get(pos).getValue()).name1);
        ets2.setText(switchBoardList.get(indicesArrayList.get(groupPosition).get(pos).getValue()).name2);
        ets3.setText(switchBoardList.get(indicesArrayList.get(groupPosition).get(pos).getValue()).name3);
        ets4.setText(switchBoardList.get(indicesArrayList.get(groupPosition).get(pos).getValue()).name4);
        ets5.setText(switchBoardList.get(indicesArrayList.get(groupPosition).get(pos).getValue()).name5);
        ets6.setText(switchBoardList.get(indicesArrayList.get(groupPosition).get(pos).getValue()).name6);
        ets7.setText(switchBoardList.get(indicesArrayList.get(groupPosition).get(pos).getValue()).name7);
        ets8.setText(switchBoardList.get(indicesArrayList.get(groupPosition).get(pos).getValue()).name8);
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
                    Toast.makeText(context, "One or more text fields are empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    SwitchBoard switchBoard = new SwitchBoard(title, s1, s2, s3, s4, s5, s6, s7, s8);
                    DatabaseReference refSwitchInfo  = FirebaseDatabase.getInstance().getReference().child("productKeys").child(productKey).child("switchBoards");
                    DatabaseReference dbrEdit = refSwitchInfo.child(indicesArrayList.get(groupPosition).get(pos).getValue() + "");
                    dbrEdit.setValue(switchBoard);
                    if(positionFinal[0]!=positionInitial) {
                        String key = refProductKey.child(productKey).child("rooms").child(rooms.get(positionFinal[0]).getId()).child("indices").push().getKey();
                        IndexPojo index = new IndexPojo(key, indicesArrayList.get(groupPosition).get(pos).getValue());
                        refProductKey.child(productKey).child("rooms").child(rooms.get(positionFinal[0]).getId()).child("indices").child(key).setValue(index);
                        refProductKey.child(productKey).child("rooms").child(rooms.get(groupPosition).getId()).child("indices").child(indicesArrayList.get(groupPosition).get(pos).getKey()).removeValue();
                    }
                    Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void dataSetChanged(ArrayList<RoomPojo> rooms, Activity context,String keyPos, ArrayList<SwitchBoard> switchBoardList,DatabaseReference refKeyPos,DatabaseReference refProductKey,ArrayList<ArrayList<IndexPojo>> indicesArrayList) {
        super.notifyDataSetChanged();
        this.rooms=rooms;
        this.context=context;
        this.keyPos=keyPos;
        this.switchBoardList=switchBoardList;
        this.refKeyPos=refKeyPos;
        this.refProductKey=refProductKey;
        this.indicesArrayList=indicesArrayList;
        notifyDataSetChanged();
    }
}
