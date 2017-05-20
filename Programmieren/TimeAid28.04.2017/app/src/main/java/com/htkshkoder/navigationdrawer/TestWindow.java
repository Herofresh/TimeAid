package com.htkshkoder.navigationdrawer;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.util.logging.*;

/**
 * Created by FeJo on 28.04.2017.
 */

public class TestWindow extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        //mContainer = inflater.inflate(R.layout.connections_layout, null);
        //LinearLayout linearLayout = mContainer.findViewById(R.id.connections);
        //EditText editText = new EditText(getActivity());

        //final int i = 5;
        /*editText.setId(i); //Set id to remove in the future.
        editText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        editText.setText("Hello");
        Log.d("View","Start");
        try{
            linearLayout.addView(editText);
        }catch(Exception e){
            e.printStackTrace();
        }*/



        Calendar_Logic cl = new Calendar_Logic();
        List<Calendar_A_Event> test = new LinkedList<Calendar_A_Event>();
/*
        for (int i = 0; i < 10; i++)
        {


            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.GERMAN);

            int day = 20+i;

            String sDate = "2017-05-" + day + "T08:00:00+01:00";
            String eDate = "2017-05-" + day + "T20:00:00+01:00";



            DateTime s = new DateTime(sDate);
            DateTime e = new DateTime(eDate);

            EventDateTime edtS = new EventDateTime();
            EventDateTime edtE = new EventDateTime();

            edtE.setDateTime(e);
            edtS.setDateTime(s);

            test.add(new Calendar_A_Event(""+i, edtS, edtE));
            cl.addCalendar_A_Event(new Calendar_A_Event(""+i, edtS, edtE));
        }*/
        String sDate = "2017-05-25T08:00:00+01:00";

        DateTime s = new DateTime(sDate);
        Date start = new Date(2017,05,25,8,0);
        Date end = new Date(2017,05,25,22,0);

        EventDateTime edtZ = new EventDateTime();

        edtZ.setDateTime(s);
        Vector<Calendar_TimeWindow> freeTime = cl.dynamicDate(edtZ,start,end);


        myView = inflater.inflate(R.layout.testwindow, container, false);
        TextView RL = (TextView) myView.findViewById(R.id.testTV);

            String printTW = "";
        for (Calendar_TimeWindow value:freeTime) {
            printTW += value.toString();
            printTW += '\n';
        }
        RL.setText(printTW);

        return myView;
    }
}
