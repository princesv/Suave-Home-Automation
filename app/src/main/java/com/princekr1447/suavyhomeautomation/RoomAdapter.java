package com.princekr1447.suavyhomeautomation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    ArrayList<RoomPojo> rooms;
    Activity context;
    String keyPos;
    ArrayList<SwitchBoard> switchBoardList;
    String productKey;
    DatabaseReference refKeyPos;
    public RoomAdapter(ArrayList<RoomPojo> rooms, Activity context,String keyPos, ArrayList<SwitchBoard> switchBoardList,DatabaseReference refKeyPos){
        this.rooms=rooms;
        this.context=context;
        this.keyPos=keyPos;
        this.switchBoardList=switchBoardList;
        this.refKeyPos=refKeyPos;
    }
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.room_card_view,parent,false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.roomTitle.setText(rooms.get(position).getTitle());
        SwitchBoardListAdapter adapter=new SwitchBoardListAdapter(context,switchBoardList,keyPos,refKeyPos,productKey);
        holder.listSwitchBoards.setAdapter(adapter);
      //  boolean isExpandable=rooms.get(position).getExpandable();
       // holder.listSwitchBoards.setVisibility(isExpandable?View.VISIBLE:View.GONE);
    }

    @Override
    public int getItemCount() {

        return rooms.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder{
        TextView roomTitle;
        ListView listSwitchBoards;
        LinearLayout roomParentView;
        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomTitle=itemView.findViewById(R.id.roomTitle);
            listSwitchBoards=itemView.findViewById(R.id.listViewSwitchBoardsFragment);
            roomParentView=itemView.findViewById(R.id.roomParentView);
            roomParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // rooms.get(getAdapterPosition()).setExpandable(!(rooms.get(getAdapterPosition()).getExpandable()));
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
