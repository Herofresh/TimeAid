package com.htkshkoder.navigationdrawer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.UUID;

import com.google.api.services.calendar.model.Event;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bjorna on 18.05.2017.
 */

public class secondScreen extends AppCompatActivity {
    Button btnClose, btnusrPref;
    TextView tvEnterNameOfEvent;
    EditText etEnterNameOfEvent, etDescription;
    DatePicker dpStartDate, dpEndDate;
    String eventName, startStringDate, endStringDate, description;
    Date startDate, endDate;
    SimpleDateFormat dateFormatter;
    TimePicker tpStartTime, tpEndTime;
    Time startTime, endTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondscreen);

        btnClose = (Button) findViewById(R.id.btnCreateEvent);
        btnusrPref = (Button) findViewById(R.id.btnUsrPref);
        tvEnterNameOfEvent = (TextView) findViewById(R.id.tvEnterNameOfEvent);
        etEnterNameOfEvent = (EditText) findViewById(R.id.etEnterNameOfEvent);
        etDescription = (EditText) findViewById(R.id.etDescription);
        dpStartDate = (DatePicker) findViewById(R.id.dpStartDate);
        dpEndDate = (DatePicker) findViewById(R.id.dpEndDate);
        tpStartTime = (TimePicker) findViewById(R.id.tpStartDate);
        tpEndTime = (TimePicker) findViewById(R.id.tpEndDate);

        Intent i = getIntent();

        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Event newEvent = new Event();
                newEvent.setId(UUID.randomUUID().toString());
                newEvent.setSummary(etEnterNameOfEvent.getText().toString());
                newEvent.setDescription(etDescription.getText().toString());

                //newEvent.setStart();
//                int day = dpStartDate.getDayOfMonth();
//                int month = dpStartDate.getMonth();
//                int year = dpStartDate.getYear()-1900;
//                startDate = new Date(year,month,day);
//                dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
//                startStringDate = dateFormatter.format(startDate);
//
//                tvEnterNameOfEvent.setText(startStringDate);
//
//                day = dpEndDate.getDayOfMonth();
//                month = dpEndDate.getMonth();
//                year = dpEndDate.getYear()-1900;
//                endDate = new Date(year,month,day);
//                dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
//                endStringDate = dateFormatter.format(endDate);
//                tvEnterNameOfEvent.append(" ");
//                tvEnterNameOfEvent.append(endStringDate);
//
                Intent resultIntent = new Intent();

                resultIntent.putExtra("newEventValue", eventToString(newEvent));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    btnusrPref.setOnClickListener(new View.OnClickListener(){
        public void onClick(View v){
            Intent i = new Intent(getApplicationContext(), Serialization_UserPreferences.class);
            startActivity(i);
        }
    });
    }


    private String eventToString(Event e) {
        String result = "";
        result += e.getId() + ";";
        result += e.getSummary() + ";";
        result += e.getDescription() + ";";
        //result += e.getStart().toString() + ";";
        // result += e.getEnd().toString() + ";";
        return result;
    }
}