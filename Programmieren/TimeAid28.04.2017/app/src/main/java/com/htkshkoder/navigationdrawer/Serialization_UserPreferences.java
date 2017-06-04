package com.htkshkoder.navigationdrawer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

/**
 * Created by bjorn on 05.05.2017.
 */

public class Serialization_UserPreferences extends AppCompatActivity{
    /*
    *  change settings
    *
    * */
    Intent i = getIntent();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userpref);

        finish();
    }
}

