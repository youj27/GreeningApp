package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainAdapter01 extends FragmentStateAdapter{
    public int mCount;
    public MainAdapter01(FragmentActivity fa, int count) {
        super(fa);
        mCount = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        if(index==0) return new mainslide02_Fg1();
        else if(index==1) return new mainslide02_Fg2();
        else if(index==2) return new mainslide02_Fg3();
        else return null;
//        else return new mainslide02_Fg4();
    }

    @Override
    public int getItemCount() {
        return 2000;
    }

    public int getRealPosition(int position) { return position % mCount; }
}