package com.htkshkoder.navigationdrawer;

/**
 * Created by FeJo on 12.05.2017.
 */
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.util.logging.*;

public class Calendar_Logic {
    public static Date dateParser(EventDateTime e)
    {
        if (e == null) return null;

        String dts = e.getDateTime().toString();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date d = new Date();
        try {
             d = format.parse(dts);

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return d;
    }


    HashMap<String,Calendar_A_Event> hashCalendar_A_Events;
    private static final Logger fLogger =
            Logger.getLogger(Calendar.class.getPackage().getName())
            ;

    public void addCalendar_A_Event (Calendar_A_Event t){
        hashCalendar_A_Events.put(t.entry.getId(), t);
        //google geben
    }
    public void editCalendar_A_Event (Calendar_A_Event t){
        deleteCalendar_A_Event(t);
        addCalendar_A_Event(t);
        //google geben
    }
    public void deleteCalendar_A_Event (Calendar_A_Event t){
        hashCalendar_A_Events.remove(t.entry.getId());
    }
    /*public void saveCalendar_A_Events(String path)
    {
        //serialize the List
        try (
                OutputStream file = new FileOutputStream(path);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);
        ){
            output.writeObject(hashCalendar_A_Events);
        }
        catch(IOException ex){
            fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
        }

    }

    public void loadCalendar_A_Events(String path)
    {

        try(
                InputStream file = new FileInputStream("path");
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream (buffer);
        ){
            //deserialize the List
            hashCalendar_A_Events = (HashMap<String,Calendar_A_Event>)input.readObject();
        }
        catch(ClassNotFoundException ex){
            fLogger.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
        }
        catch(IOException ex){
            fLogger.log(Level.SEVERE, "Cannot perform input.", ex);
        }

    }*/

    public Vector<Calendar_TimeWindow> dynamicDate (EventDateTime dueTo, Date startTime, Date endTime){
        Date dueToDate = dateParser(dueTo);
        Vector<Calendar_TimeWindow> suggestedDates = new Vector<Calendar_TimeWindow>();
        Vector<Pair<Date,Calendar_A_Event>> results = new Vector<Pair<Date,Calendar_A_Event>>();
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        for(Map.Entry<String, Calendar_A_Event> entry : hashCalendar_A_Events.entrySet()){
            Date tempDate = dateParser(entry.getValue().entry.getStart());
            if(now.compareTo(tempDate)>0)
            {
                if(dueToDate.compareTo(tempDate)<0)
                {
                    results.add(new Pair(entry.getValue().entry.getStart(), entry.getValue()));
                }
            }
        }
        for(int y = now.getYear(); y <= dueToDate.getYear(); y++)
        {
            for(int m = now.getMonth(); m <= dueToDate.getMonth(); m++)
            {
                for(int d = now.getDate(); d <= dueToDate.getDate(); d++)
                {
                    Date startDay = new Date();
                    startDay.setYear(y);
                    startDay.setMonth(m);
                    startDay.setDate(d);
                    Date endDay = startDay;
                    startDay.setHours(startTime.getHours());
                    startDay.setMinutes(startTime.getMinutes());
                    endDay.setHours(endTime.getHours());
                    endDay.setMinutes(endTime.getMinutes());
                    Calendar_TimeWindow originalTw = new Calendar_TimeWindow(startDay,endDay);
                    suggestedDates.add(originalTw);
                }
            }
        }
        for(Pair<Date,Calendar_A_Event> t : results)
        {
            for(Calendar_TimeWindow tw : suggestedDates)
            {
                Calendar_TimeWindow[] split = tw.splitTime(tw, t.getRight().tW);
                if(split[0].isValid())
                {
                    suggestedDates.remove(tw);
                    for(Calendar_TimeWindow newTw : split)
                    {
                        suggestedDates.add(newTw);
                    }
                    break;
                }
            }
        }
        return suggestedDates;
    }
}
