

package com.htkshkoder.navigationdrawer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

/**
 * Created by FeJo on 28.04.2017.
 */

public class CalendarFragment extends Fragment {

    public CalendarView myView;

    @Nullable
    @Override
    public CalendarView onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


            myView = (CalendarView) inflater.inflate(R.layout.calendarlo, container, false);

        return myView;

         //myView = (CalendarView)inflater.inflate(R.layout.calendarlo, container, false);

        //myView =  (CalendarView)getView().findViewById(R.id.simpleCalendarView);


//        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                month +=1;
//                Toast.makeText(getActivity(),dayOfMonth + "/" + month +"/" + year, Toast.LENGTH_LONG).show();
//            }
//        });
        //return myView;
        //return super.onCreateView(inflater, container, savedInstanceState);


    }
}