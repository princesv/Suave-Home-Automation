package com.princekr1447.suavyhomeautomation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class ManageSmartdomListAdapter extends RecyclerView.Adapter<ManageSmartdomListAdapter.ViewHolder> {
    buttonClickListener mListener;
    private final Context context;
    private final List<CentralModule> centralModules;
    public ManageSmartdomListAdapter(Context context,List<CentralModule> centralModules,buttonClickListener listener){
        this.context=context;
        mListener=listener;
        this.centralModules=centralModules;
        this.mListener=listener;
    }
    @NonNull
    @Override
    public ManageSmartdomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.smartdom_item_layout,parent,false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageSmartdomListAdapter.ViewHolder holder, int position) {
        holder.title.setText(centralModules.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return centralModules.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView freeSmartdom;
        TextView title;
        buttonClickListener clickListener;

        public ViewHolder(@NonNull View itemView,buttonClickListener clickListener) {
            super(itemView);
            freeSmartdom=itemView.findViewById(R.id.freeUpSmartdom);
            title=itemView.findViewById(R.id.centralModuleTitle);
            this.clickListener=clickListener;
            freeSmartdom.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition());
        }
    }
    public interface buttonClickListener{
        void onItemClick(int position);
    }
}
