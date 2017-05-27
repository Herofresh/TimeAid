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
     Vector<TimeWindow> suggestedTWs;
     Vector<TimeWindow> distributedTWs;
     Vector<Date> availableDays;

     public void setSuggestedTWs(Vector<TimeWindow> suggestedTWs) {
         this.suggestedTWs = suggestedTWs;
         distributedTWs.clear();
     }

     public void setSuggestedTWs(Vector<TimeWindow> suggestedTWs, int toDistributeLength) {
         this.suggestedTWs = suggestedTWs;
         distributedTWs.clear();
         filterSuggestedTWs(suggestedTWs, toDistributeLength);
     }



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
        // Vector<TimeWindow> suggestedTWs = new Vector<TimeWindow>(); // zur Klassenvariable gemacht
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
                    suggestedTWs.add(originalTw);
                }
            }
        }
        for(Pair<Date,Event> t : results)
        {
            for(TimeWindow tw : suggestedTWs)
            {
                TimeWindow[] split = tw.splitTime(tw, t.getRight().getTime());
                if(split[0].isValid())
                {
                    suggestedTWs.remove(tw);
                    for(TimeWindow newTw : split)
                    {
                        suggestedTWs.add(newTw);
                    }
                    break;
                }
            }
        }
        return suggestedTWs;
    }

     public Vector<TimeWindow> filterSuggestedTWs (long toDistributeLength){
         Vector<int> indexToDel;

         int i = 0;
         for (TimeWindow currentTW : suggestedTWs) {
             if(currentTW.getTimeBetween() < toDistributeLength);
             {
                 indexToDel.add(i);
             }
            else
             {
                 Date day = new Date();
                 day.setYear = currentTW.getStart().getYear();
                 day.setMonth = currentTW.getStart().getMonth();
                 day.setDate = currentTW.getStart().getDate();
                 if(availableDays.indexOf(day) == -1)
                 {
                     availableDays.add(day);
                 }
             }
             i++;
         }
         for (int delIndex: indexToDel) {
             suggestedTWs.remove(delIndex);
         }
     }

    public Vector<TimeWindow> linearDistribution (Vector<TimeWindow> distributedDates, Vector<Termin> toDistributeDates){

        int availableSize = availableDays.size();
        int suggestedSize = suggestedTWs.size();
        int toDistributeAmount = toDistributeDates.size();
        if (toDistributeAmount == 0) return;
        double factor = (double) availableSize / toDistributeAmount;


        if (factor < 1){ // Mehr Termine als Tage -> rekursiver Aufruf
            int k = 0;
            for (Date currentDay:availableDays) {
                for (TimeWindow possibleTW:suggestedTWs) {

                    if        (currentDay.getYear() == possibleTW.getStart().getYear()
                            && currentDay.getMonth() == possibleTW.getStart().getMonth()
                            && currentDay.getDate() == possibleTW.getStart().getDate()){

                        distributedDates.add(possibleTW);
                        break;
                    }
                }
                    suggestedTWs.remove(suggestedTWs.indexOf(usedTW));
                // rekursiver Aufruf
                 if (k++ >= availableSize) linearDistribution(distributedDates, toDistributeDates);
            }
            // temp.setTime(); Keine Ahnung was die Variable machen soll :(
        }

        else if (factor > 1){ // Mehr Tage als Termine -> Algorithmus dann ENDE
            int k = 0;
            double f = factor -1;
            int currentfactor;
            for (Date currentDay:availableDays) {

                int currentfactor = (int) f;
                if (k != currentfactor)  // Wenn der Index des aktuellen Tages nicht dem aktuellen Faktor entspricht,
                {                        // springe zum nächsten Tag und vergleiche erneut.
                    k++;
                    continue;
                }
                for (TimeWindow possibleTW:suggestedTWs) { // wenn Index == Faktor, trage das TW des Tages in distributedTWs ein

                    if        (currentDay.getYear() == possibleTW.getStart().getYear()
                            && currentDay.getMonth() == possibleTW.getStart().getMonth()
                            && currentDay.getDate() == possibleTW.getStart().getDate()){

                        distributedDates.add(possibleTW);
                        break;
                    }
                }
                suggestedTWs.remove(suggestedTWs.indexOf(usedTW));
                if (f += factor > availableSize) return distributedDates;
            }
        }else if (factor == 1){ // Gleich viele Termine wie Tage -> Nach diesem Aufruf ENDE
            int k = 0;
            for (Date currentDay:availableDays) {
                for (TimeWindow possibleTW:filteredTWs) {

                    if        (currentDay.getYear() == possibleTW.getStart().getYear()
                            && currentDay.getMonth() == possibleTW.getStart().getMonth()
                            && currentDay.getDate() == possibleTW.getStart().getDate()){

                        distributedDates.add(possibleTW);
                        break;
                    }
                }
                suggestedTWs.remove(suggestedTWs.indexOf(usedTW));
                k++;
            }
        }
        return distributedDates;

    }

    // TODO progressive & degressive

     /*public Vector<TimeWindow> progressiveDistribution (Vector<TimeWindow> distributedDates, Vector<TimeWindow> suggestedTWs, Vector<Termin> toDistributeDates, long toDistributeLength){
         Vector<int> indexToDel;
         Vector<Date> availableDays;
         //Vector<TimeWindow> distributedDates = new Vector<TimeWindow>;
         int i = 0;
         for (TimeWindow value:suggestedTWs) {
             if(value.getTimeBetween()<toDistributeLength);
             {
                 indexToDel.add(i);
             }
            else
             {
                 Date day = new Date();
                 day.setYear = value.getStart().getYear();
                 day.setMonth = value.getStart().getMonth();
                 day.setDate = value.getStart().getDate();
                 if(availableDays.indexOf(day) == -1)
                 {
                     availableDays.add(day);
                 }
             }
             i++;
         }
         for (int delIndex: indexToDel) {
             suggestedTWs.remove(delIndex);
         }
         int availableSize = availableDays.size();
         int suggestedSize = suggestedTWs.size();
         int toDistributeAmount = toDistributeDates.size();
         if (toDistributeAmount == 0) return;
         double factor = (double) availableSize / toDistributeAmount;


         if (factor < 1){ // Mehr Termine als Tage -> rekursiver Aufruf
             int k = 0;
             for (Date currentDay:availableDays) {
                 for (TimeWindow possibleTW:suggestedTWs) {

                     if        (currentDay.getYear() == possibleTW.getStart().getYear()
                             && currentDay.getMonth() == possibleTW.getStart().getMonth()
                             && currentDay.getDate() == possibleTW.getStart().getDate()){

                         distributedDates.add(possibleTW);
                     }
                 }
                 // rekursiver Aufruf
                 if (k++ >= availableSize) linearDistribution(distributedDates, suggestedTWs, toDistributeDates, toDistributeLength);
             }
             // temp.setTime(); Keine Ahnung was die Variable machen soll :(
         }


         else if (factor > 1){ // Mehr Termine als Tage -> Algorithmus
             int k = 0;
             double f = factor -1;
             int currentfactor;
             for (Date currentDay:availableDays) {

                 int currentfactor = (int) f;
                 if (k != currentfactor)  // Wenn der Index des aktuellen Tages nicht dem aktuellen Faktor entspricht,
                 {                        // springe zum nächsten Tag und vergleiche erneut.
                     k++;
                     continue;
                 }
                 for (TimeWindow possibleTW:suggestedTWs) { // wenn Index == Faktor, trage das TW des Tages in distributedTWs ein

                     if        (currentDay.getYear() == possibleTW.getStart().getYear()
                             && currentDay.getMonth() == possibleTW.getStart().getMonth()
                             && currentDay.getDate() == possibleTW.getStart().getDate()){
                         distributedDates.add(possibleTW);
                     }
                 }
                 if (f += factor > availableSize) return distributedDates;
             }
         }else if (factor == 1){ // Gleich viele Termine wie Tage -> Nach diesem Aufruf Ende
             int k = 0;
             for (Date currentDay:availableDays) {
                 for (TimeWindow possibleTW:suggestedTWs) {

                     if        (currentDay.getYear() == possibleTW.getStart().getYear()
                             && currentDay.getMonth() == possibleTW.getStart().getMonth()
                             && currentDay.getDate() == possibleTW.getStart().getDate()){

                         distributedDates.add(possibleTW);
                     }
                 }
                 k++;
             }
         }


         return distributedDates;

     }*/


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