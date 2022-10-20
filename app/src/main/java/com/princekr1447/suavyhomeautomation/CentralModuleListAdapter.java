package com.princekr1447.suavyhomeautomation;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class CentralModuleListAdapter extends ArrayAdapter<CentralModule> {
    private Activity context;
    private ArrayList<CentralModule> centralModuleList;
    String PRODUCTKEY_KEY="ProductKeyId1236";
    public CentralModuleListAdapter(Activity context, ArrayList<CentralModule> cmList){
        super(context,R.layout.list_item_layout,cmList);
        this.context=context;
        this.centralModuleList=cmList;
    }
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       // return super.getView(position, convertView, parent);
        LayoutInflater inflater=context.getLayoutInflater();
        View listViewItem=inflater.inflate(R.layout.central_module_list_item,null,true);
        TextView tvCentralModuleName=listViewItem.findViewById(R.id.centralModuleName);
        tvCentralModuleName.setText(centralModuleList.get(position).getName());
        listViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,MainActivity.class);
                intent.putExtra(PRODUCTKEY_KEY,centralModuleList.get(position).getProductKey());
                context.startActivity(intent);
            }
        });
        return listViewItem;
    }

    @Override
    public int getCount() {
        return centralModuleList.size();
    }
}
