/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calendarlogic;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.*;
import com.google.api.client.util.ExponentialBackOff;
 /**
 *
 * @author Miel
 */
public class Calendar {
    HashMap<String,Event> hashEvents;
    private static final Logger fLogger =
            Logger.getLogger(Calendar.class.getPackage().getName())
            ;

    public void addEvent (Event t){
        hashEvents.put(t.ID, t);
        //google geben
    }
    public void editEvent (Event t){
        hashEvents.replace(t.ID, t);
        //google geben
    }
    public void deleteEvent (Event t){
        hashEvents.remove(t.ID);
    }
    public void saveEvents(String path)
    {
        //serialize the List
        try (
                OutputStream file = new FileOutputStream(path);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);
        ){
            output.writeObject(hashEvents);
        }
        catch(IOException ex){
            fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
        }

    }

    public void loadEvents(String path)
    {
        try(
                InputStream file = new FileInputStream("path");
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream (buffer);
        ){
            //deserialize the List
            hashEvents = (HashMap<String,Event>)input.readObject();
        }
        catch(ClassNotFoundException ex){
            fLogger.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
        }
        catch(IOException ex){
            fLogger.log(Level.SEVERE, "Cannot perform input.", ex);
        }
    }

    public Vector<TimeWindow> dynamicDate (Date endDate, Date startTime, Date endTime){
        Vector<TimeWindow> suggestedDates = new Vector<TimeWindow>();
        Vector<Pair<Date,Event>> results = new Vector<Pair<Date,Event>>();
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        for(Map.Entry<String, Event> entry : hashEvents.entrySet()){
            if(now.compareTo(entry.getValue().getStart())>0)
            {
                if(endDate.compareTo(entry.getValue().getStart())<0)
                {
                    results.add(new Pair(entry.getValue().start, entry.getValue()));
                }
            }
        }
        for(int y = now.getYear(); y <= endDate.getYear(); y++)
        {
            for(int m = now.getMonth(); m <= endDate.getMonth(); m++)
            {
                for(int d = now.getDate(); d <= endDate.getDate(); d++)
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
                    TimeWindow originalTw = new TimeWindow(startDay,endDay);
                    suggestedDates.add(originalTw);
                }
            }
        }
        for(Pair<Date,Event> t : results)
        {
            for(TimeWindow tw : suggestedDates)
            {
                TimeWindow[] split = tw.splitTime(tw, t.getRight().getTime());
                if(split[0].isValid())
                {
                    suggestedDates.remove(tw);
                    for(TimeWindow newTw : split)
                    {
                        suggestedDates.add(newTw);
                    }
                    break;
                }
            }
        }
        return suggestedDates;
    }

    public Vector<Termin> linearDistribution (Vector<TimeWindow> suggestedDates, Vector<Termin> newEvent, long eventLength){
        Vector<int> indexToDel;
        Vector<Date> availableDates;
        int i = 0;
        for (TimeWindow value:suggestedDates) {
            if(value.getTimeBetween()<eventLength);
            {
                indexToDel.add(i)
            }
            else
            {
                Date day = new Date();
                day.setYear = value.getStart().getYear();
                day.setMonth = value.getStart().getMonth();
                day.setDate = value.getStart().getDate();
                if(availableDates.indexOf(day) == -1)
                {
                    availableDates.add(day);
                }
            }
            i++;
        }
        for (int delIndex: indexToDel) {
            suggestedDates.remove(delIndex);
        }

        double factor = availableDates.size() / newEvent.size();
        if (factor == 1){
            // füge termin bei jedem tag hinzu
            int mielCountYouStupidBitch = 0;
                for (Date currentDay:availableDates) {
                    for (TimeWindow possibleTimeWindow:suggestedDates) {
                        if(currentDay.getYear() == possibleTimeWindow.getStart().getYear()
                        && currentDay.getMonth() == possibleTimeWindow.getStart().getMonth()
                        && currentDay.getDate() == possibleTimeWindow.getStart().getDate()){

                        }
                    }
                    mielCountYouStupidBitch++;
                }
                temp.setTime();
        }
        else if (factor < 1){
            // füge termin bei jedem tag hinzu und gehe mit übrehang
            // nochmal faktor ausrechnen
            // ergebnis bestimmt anzahl der wh.
            // rekursiv
        }
        else {
            //gehe in algorithmus
        }
    }
}

public class TimeWindow
{
    Date start;
    Date end;
    boolean valid = true;

    public TimeWindow() {
        this.valid = false;
    }

    public TimeWindow(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public Date getEnd() {
        return end;
    }

    public long getTimeBetween() {
        long difference = end.getTime() - start.getTime();
        return difference;
    }

    public TimeWindow[] splitTime(TimeWindow toSplit, TimeWindow toComp) {
        
        /*Check Start and End from both TimeWindow
        If same -> 1 new TimeWindow
        Else -> 2 new TimeWindow
        Start = toSplit.Start
        End = toComp.Start
        Start2 = toComp.End
        End2 = toSplit.End
        */
        TimeWindow[] retValue;

        if (toSplit.getStart() == toComp.getStart() && toSplit.getEnd() == toComp.getEnd()){
            TimeWindow invalidTw = new TimeWindow();
            retValue = new TimeWindow[1];
            retValue[0] = invalidTw;
            return retValue;
        }
        else if (toSplit.getStart() == toComp.getStart()){
            TimeWindow remainingTw = new TimeWindow(toComp.getEnd(), toSplit.getEnd());
            retValue = new TimeWindow[1];
            retValue[0] = remainingTw;
            return retValue;
        }
        else if (toSplit.getEnd() == toComp.getEnd()){
            TimeWindow remainingTw = new TimeWindow(toSplit.getStart(), toComp.getStart());
            retValue = new TimeWindow[1];
            retValue[0] = remainingTw;
            return retValue;
        }
        else if (toComp.getStart().compareTo(toSplit.getStart()) > 0 && toComp.getEnd().compareTo(toSplit.getEnd()) < 0){
            TimeWindow firstTw = new TimeWindow(toSplit.getStart(), toComp.getStart());
            TimeWindow secondTw = new TimeWindow(toComp.getEnd(), toSplit.getEnd());
            retValue = new TimeWindow[2];
            retValue[0] = firstTw;
            retValue[1] = secondTw;
            return retValue;
        }
        else{
            TimeWindow invalidTw = new TimeWindow();
            retValue = new TimeWindow[1];
            retValue[0] = invalidTw;
            return retValue;
        }
    }
}