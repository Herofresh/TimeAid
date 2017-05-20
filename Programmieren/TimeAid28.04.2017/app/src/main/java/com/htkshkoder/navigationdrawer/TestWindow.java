package com.htkshkoder.navigationdrawer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by FeJo on 28.04.2017.
 */

public class TestWindow extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.testwindow, container, false);



        month +=1;
        Toast.makeText(getActivity(),dayOfMonth + "/" + month +"/" + year, Toast.LENGTH_LONG).show();

        return myView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
