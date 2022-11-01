package com.princekr1447.suavyhomeautomation;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SwitchBoardFragmentAdapter extends FragmentPagerAdapter {
    private Activity myContext;
    int totalTabs;
    String emailEncoded;
    ArrayList<CentralModule> centralModules;
    public SwitchBoardFragmentAdapter(Activity context,FragmentManager fragmentManager,int totalTabs,ArrayList<CentralModule> centralModules,String emailEncoded) {
        super(fragmentManager);
        this.myContext=context;
        this.totalTabs=totalTabs;
        this.centralModules=centralModules;
        this.emailEncoded=emailEncoded;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        SwitchBoardsFragment switchBoardsFragment=new SwitchBoardsFragment(centralModules.get(position).getProductKey(),emailEncoded,myContext);
        return switchBoardsFragment;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
