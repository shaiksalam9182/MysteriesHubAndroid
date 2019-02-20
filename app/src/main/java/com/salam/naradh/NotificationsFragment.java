package com.salam.naradh;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {

    ProgressBar pbNot;
    RecyclerView rvNot;


    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_notifications,container,false);



        pbNot = (ProgressBar)v.findViewById(R.id.pb_not);
        rvNot = (RecyclerView)v.findViewById(R.id.rv_notifications);





        return v;
    }

}
