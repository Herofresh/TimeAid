package com.htkshkoder.navigationdrawer;
import android.app.usage.UsageEvents;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;




/**
 * Created by bjorn on 05.05.2017.
 */

public class Calendar_A_Event {
    /*
        Variablen:
            datum,name,description,priority,kategorie
        Methoden:
            showEintrag()
     */
    Event entry = new Event();
    Calendar_TimeWindow tW;

    public Calendar_A_Event(String id, EventDateTime startDate, EventDateTime endDate)
    {
        entry.setId(id);
        entry.setStart(startDate);
        entry.setEnd(endDate);

        tW = new Calendar_TimeWindow(Calendar_Logic.dateParser(startDate), Calendar_Logic.dateParser(endDate));
    }

    @Override
    public String toString()
    {
        return tW.toString();
    }
}
