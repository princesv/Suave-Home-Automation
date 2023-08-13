package com.princekr1447.suavyhomeautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageSmartdomActivity extends AppCompatActivity implements ManageSmartdomListAdapter.buttonClickListener{
    DatabaseReference refUserId;
    DatabaseReference refProductKey;
    String emailEncoded;
    ArrayList<CentralModule> centralModules=new ArrayList<>();
    LinearLayout shimmerFrameLayout;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_smartdom);
        shimmerFrameLayout=findViewById(R.id.idShimmerLayoutView);
        recyclerView=findViewById(R.id.recyclerView);
        emailEncoded= FirebaseAuth.getInstance().getCurrentUser().getUid();
        refUserId= FirebaseDatabase.getInstance().getReference().child("usersId");
        refProductKey=FirebaseDatabase.getInstance().getReference().child("productKeys");
        refUserId.child(emailEncoded).child("productKeys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                centralModules.clear();
                shimmerFrameLayout.setVisibility(View.GONE);
                for(DataSnapshot artistSnapshot:snapshot.getChildren()){

                    CentralModule cm=artistSnapshot.getValue(CentralModule.class);
                    centralModules.add(cm);

                }
                updateCentralModulesList(centralModules);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void updateCentralModulesList(ArrayList<CentralModule> centralModules){
        ManageSmartdomListAdapter adapter
                = new ManageSmartdomListAdapter(getApplication(),centralModules,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(ManageSmartdomActivity.this));
    }

    @Override
    public void onItemClick( int position) {
        showConfirmDialog(position);
    }

    public void showConfirmDialog(final int position){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_confirm_dialog);
        ImageView confirm=dialog.findViewById(R.id.confirmBtn);
        ImageView cancel=dialog.findViewById(R.id.cancelBtn);
        TextView confirmText=dialog.findViewById(R.id.confirmText);
        confirmText.setText(R.string.confirm_delete_room);
        TextView confirmTextDetail=dialog.findViewById(R.id.confirmDetailText);
        confirmTextDetail.setText(R.string.confirm_delete_room_detail);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refUserId.child(emailEncoded).child("productKeys").child(centralModules.get(position).getId()).setValue(null);
                refProductKey.child(centralModules.get(position).getProductKey()).setValue(null);
                refProductKey.child(centralModules.get(position).getProductKey()).child("switchBoards").setValue("#");
                dialog.dismiss();
                Toast.makeText(ManageSmartdomActivity.this, "Freed central module "+centralModules.get(position).getName()+" successfully", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
