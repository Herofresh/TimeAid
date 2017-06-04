package com.htkshkoder.navigationdrawer;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by FeJo on 28.04.2017.
 */

public class Drawer_D_Settings extends Fragment {

    View myView;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.drawer_d_settings_lo, container, false);

        return myView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
