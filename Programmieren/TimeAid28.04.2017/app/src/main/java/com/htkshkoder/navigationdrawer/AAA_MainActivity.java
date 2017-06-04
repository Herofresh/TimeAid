package com.htkshkoder.navigationdrawer;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;




public class AAA_MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Calendar_Logic cl = new Calendar_Logic();

    ImageView imgLogo;
   //API googleApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //googleApi = new API();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgLogo = (ImageView)findViewById(R.id.imgLogo);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),secondScreen.class);
                startActivityForResult(i, 1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        try
        {
            cl = cl.loadCalendar_A_Events();
        }
        catch (Exception e)
        {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String newEvent = data.getStringExtra("newEventValue");
                Event e = stringToEvent(newEvent);
               Calendar_A_Event newE = new Calendar_A_Event(e);
                cl.addCalendar_A_Event(newE);
            }
        }
    }

    private Event stringToEvent(String input){
        String[] parts = input.split(";");
        Toast t = Toast.makeText(getApplicationContext(),input, Toast.LENGTH_LONG);
        t.show();
        Event e =  new Event();
        for(int i = 0; i<parts.length;i++)
        {
            switch (i) {
                case 0:
                    if (parts[i] != null)
                        e.setId(parts[0]);
                    break;
                case 1:
                    if (parts[i] != null)
                        e.setSummary(parts[1]);
                    break;
                case 2:
                    if (parts[i] != null)
                        e.setDescription(parts[2]);
                    break;
            }
        }

//        EventDateTime eventStart = new EventDateTime();
//        DateTime start = new DateTime(parts[3]);
//        eventStart.setDate(start);
//        eventStart.setDateTime(start);
//        e.setStart(eventStart);
//        start = new DateTime(parts[4]);
//        eventStart.setDate(start);
//        eventStart.setDateTime(start);
//        e.setEnd(eventStart);
        return e;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_first_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new Drawer_D_Settings()).commit();
        } else if (id == R.id.nav_second_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new Drawer_H_Features()).commit();
        } else if (id == R.id.nav_test) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new TestWindow()).commit();
        } else if (id == R.id.nav_third_layout) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ZZZ_Fragment_Template()).commit();
        } else if (id == R.id.nav_calendar) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new Drawer_A_Calendar()).commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
