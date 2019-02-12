package com.salam.naradh;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {
    String type = "";
    TextView tvData;
    RelativeLayout rlSlider;
    RecyclerView rvPosts;
    int deviceWidth;

    public DataFragment() {
        // Required empty public constructor
    }


    public static DataFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("type",data);

        DataFragment fragment = new DataFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_data,container,false);
        // Inflate the layout for this fragment


        type = getArguments().getString("type");

        rlSlider = (RelativeLayout)v.findViewById(R.id.rl_slider);
        rvPosts =(RecyclerView)v.findViewById(R.id.rv_posts);


        deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        rlSlider.getLayoutParams().width = deviceWidth;
        rlSlider.getLayoutParams().height = (int) (deviceWidth/1.6);


        return v;
    }

}
