package com.salam.naradh;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class PagerAdapter extends FragmentStatePagerAdapter {


    int noOfTabs;
    String[] data;


    public PagerAdapter(FragmentManager supportFragmentManager, int tabCount, String[] dataList) {
        super(supportFragmentManager);


        noOfTabs = tabCount;
        data =dataList;

    }

    @Override
    public Fragment getItem(int i) {

//        return CategoryFragment.newInstance(data.get(i).get("name").toString(),data.get(i).get("id").toString());
        return DataFragment.newInstance(data[i]);
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
