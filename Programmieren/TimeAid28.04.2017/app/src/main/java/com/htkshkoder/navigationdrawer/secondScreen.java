package com.htkshkoder.navigationdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bjorna on 18.05.2017.
 */

public class secondScreen extends AppCompatActivity {
    Button btnClose;
    TextView tvEnterNameOfEvent;
    EditText etEnterNameOfEvent,etDescription;
    DatePicker dpStartDate, dpEndDate;
    String eventName, startStringDate,endStringDate, description;
    Date startDate, endDate;
    SimpleDateFormat dateFormatter;
    TimePicker tpStartTime, tpEndTime;
    Time startTime, endTime;

    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondscreen);

        btnClose = (Button)findViewById(R.id.btnCreateEvent);
        tvEnterNameOfEvent =(TextView)findViewById(R.id.tvEnterNameOfEvent);
        etEnterNameOfEvent = (EditText)findViewById(R.id.etEnterNameOfEvent);
        etDescription = (EditText)findViewById(R.id.etDescription);
        dpStartDate = (DatePicker)findViewById(R.id.dpStartDate);
        dpEndDate = (DatePicker)findViewById(R.id.dpEndDate);
        tpStartTime = (TimePicker)findViewById(R.id.tpStartDate);
        tpEndTime = (TimePicker)findViewById(R.id.tpEndDate);

        Intent i = getIntent();

        btnClose.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                eventName = etEnterNameOfEvent.getText().toString();
                description = etDescription.getText().toString();
                // save date from user
                int day = dpStartDate.getDayOfMonth();
                int month = dpStartDate.getMonth();
                int year = dpStartDate.getYear()-1900;
                startDate = new Date(year,month,day);
                dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
                startStringDate = dateFormatter.format(startDate);


                tvEnterNameOfEvent.setText(startStringDate);

                day = dpEndDate.getDayOfMonth();
                month = dpEndDate.getMonth();
                year = dpEndDate.getYear()-1900;
                endDate = new Date(year,month,day);
                dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
                endStringDate = dateFormatter.format(endDate);
                tvEnterNameOfEvent.append(" ");
                tvEnterNameOfEvent.append(endStringDate);


            }
        });
    }
}
