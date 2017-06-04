package com.example.bjorn.firstscreenlayout;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 * Created by bjorn on 18.05.2017.
 */

public class SecondScreenLayout extends AppCompatActivity {
    Button btnClose;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.secondscreen);

        btnClose = (Button)findViewById(R.id.button);
        Intent i = getIntent();


        btnClose.setOnClickListener(new View.OnClickListener(){
            public  void onClick(View v){
                finish();
            }
        });

    }

}
