

package com.htkshkoder.navigationdrawer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

/**
 * Created by FeJo on 28.04.2017.
 */

public class Drawer_A_Calendar extends Fragment {

    public CalendarView myView;

    @Nullable
    @Override
    public CalendarView onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        myView = (CalendarView) inflater.inflate(R.layout.drawer_a_calendar_lo, container, false);

        myView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month +=1;
                Toast.makeText(getActivity(),dayOfMonth + "/" + month +"/" + year, Toast.LENGTH_LONG).show();
            }
        });
        return myView;
    }
}
