package com.htkshkoder.navigationdrawer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by FeJo on 28.04.2017.
 */

public class SettingsFrag extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.settings_lo, container, false);
        return myView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
