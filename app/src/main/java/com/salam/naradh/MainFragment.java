package com.salam.naradh;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    TabLayout tabLayout;
    ViewPager viewPager;
    ProgressBar progressBar;
    String[] categories =new String[]{"Posts","Places","Aliens","Movies"};


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main,container,false);


        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        viewPager = (ViewPager)v.findViewById(R.id.view_pager);
//        progressBar = (ProgressBar)v.findViewById(R.id.pb_main);


        createTabs();



        return v;
    }

    private void createTabs() {
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        for (int i = 0;i<categories.length;i++){
            tabLayout.addTab(tabLayout.newTab().setText(categories[i]));
            switch (i){
                case 0:
                    tabLayout.getTabAt(0).setIcon(R.drawable.posts_icon);
                    break;
                case 1:
                    tabLayout.getTabAt(1).setIcon(R.drawable.place_icon);
                    break;
                case 2:
                    tabLayout.getTabAt(2).setIcon(R.drawable.alien_icon);
                    break;
                case 3:
                    tabLayout.getTabAt(3).setIcon(R.drawable.movie_icon);
                    break;
            }
        }






        if (isAdded()){
            PagerAdapter adapter = new PagerAdapter(getChildFragmentManager(),tabLayout.getTabCount(),categories);
            viewPager.setOffscreenPageLimit(0);
            viewPager.setAdapter(adapter);
            //tabLayout.setupWithViewPager(viewPager);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(final TabLayout.Tab tab) {

                    viewPager.setCurrentItem(tab.getPosition());
//                    tab.getIcon().setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_IN);

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
//                    tab.getIcon().setColorFilter(Color.parseColor("#bdbdbd"), PorterDuff.Mode.SRC_IN);

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

}
