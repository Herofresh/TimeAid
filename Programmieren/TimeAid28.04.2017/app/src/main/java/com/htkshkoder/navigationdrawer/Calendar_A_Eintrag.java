package com.htkshkoder.navigationdrawer;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by bjorn on 05.05.2017.
 */

public class Calendar_A_Eintrag extends Activity{
    /*
        Variablen:
            datum,name,description,priority,kategorie
        Methoden:
            showEintrag()
     */
    Button btn;
    EditText et;
    TextView tv;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_a_eintrag_lo);

        btn = (Button) findViewById(R.id.btnSubmit);
        et = (EditText)findViewById(R.id.eTName);
        tv = (TextView)findViewById(R.id.tVeintrag);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText(et.getText());
            }
        });
    }
}
