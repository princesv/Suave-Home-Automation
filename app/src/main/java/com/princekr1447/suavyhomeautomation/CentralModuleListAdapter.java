package com.princekr1447.suavyhomeautomation;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class CentralModuleListAdapter extends ArrayAdapter<CentralModule> {
    private Activity context;
    private ArrayList<CentralModule> centralModuleList;
    String PRODUCTKEY_KEY="ProductKeyId1236";
    DatabaseReference refUserId;
    String emailEncoded;
    public CentralModuleListAdapter(Activity context, ArrayList<CentralModule> cmList,DatabaseReference refUserId,String emailEncoded){
        super(context,R.layout.list_item_layout,cmList);
        this.context=context;
        this.centralModuleList=cmList;
        this.refUserId=refUserId;
        this.emailEncoded=emailEncoded;
    }
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       // return super.getView(position, convertView, parent);
        LayoutInflater inflater=context.getLayoutInflater();
        View listViewItem=inflater.inflate(R.layout.central_module_list_item,null,true);
        TextView tvCentralModuleName=listViewItem.findViewById(R.id.centralModuleName);
        tvCentralModuleName.setText(centralModuleList.get(position).getName());
        ImageButton btn=listViewItem.findViewById(R.id.buttonEditCentralModule);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CentralModule centralModule=centralModuleList.get(position);
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
                        CentralModule cmUpdated=new CentralModule(s1,centralModule.getProductKey());
                        if(TextUtils.isEmpty(s1)){
                            Toast.makeText(context, "Text field empty!", Toast.LENGTH_SHORT).show();
                        }else{
                            //   CentralModule cm=new CentralModule(centralModules.get(position).getProductKey(),s1);
                            refUserId.child(emailEncoded).child("productKeys").child(centralModule.getProductKey()).setValue(cmUpdated);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
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
