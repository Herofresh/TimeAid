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
     Vector<Termin> toDistributeDates;
     Vector<Date> availableDays;

     public void setToDistributeDates(Vector<Termin> toDistributeDates) {
         this.toDistributeDates = toDistributeDates;
     }

     public void setSuggestedTWs(Vector<TimeWindow> suggestedTWs) {
         this.suggestedTWs = suggestedTWs;
         distributedTWs.clear();
     }

     public void setSuggestedTWs(Vector<TimeWindow> suggestedTWs, long toDistributeLength) {
         this.suggestedTWs = suggestedTWs;
         distributedTWs.clear();
         filterSuggestedTWs(toDistributeLength);
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

    public void sortSuggestedTWs () {
        TimeWindow tempTW;
        int count = 0;
        int index = 0;
        int suggestedSize = (int) suggestedTWs.size();

        do {
            count = 0;
            index = 0;
            for(int i = 0; i<suggestedSize; i++) {
                if (suggestedTWs[index].end > suggestedTWs[index+1].end)
                {
                    tempTW = suggestedTWs[index];
                    suggestedTWs[index] = suggestedTWs[index+1];
                    suggestedTWs[index+1] = tempTW;
                    count++;
                }
                index++;
            }
        }while (count);
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

     public Vector<TimeWindow>[] splitBySuggestedTWs () {
         Vector<TimeWindow>[] blockSuggested = new Vector<TimeWindow>[100];
         double blockSize = (double) suggestedTWs.size()/100; // 1% of suggestedTWs
         // double blockSize = (double) suggestedTWs.size()/4;
         double transfer = 0;
         int currBlock;
         int avSugIndex = 0;

         for (int i = 0; i < 100; i++)
         {
             transfer += blockSize;
             currBlock = (int) transfer;
             transfer -= currBlock;

             for (int j = 0; j < currBlock; j++)
             {
                 blockSuggested[i].add(suggestedTWs.at(avSugIndex++));
             }
         }
         return blockSuggested;
     }

     // NICHT MEHR BENÖTIGT WEGEN splitBySuggestedTWs()
     public Vector<Date>[] splitDay () {
         Collections.sort(availableDays);
         Vector<Date>[] blockDays = new Vector<Date>[4];
         double blockDay = (double) availableDays.size()/4;
         double transferDay = 0;
         int currBlock;
         int avDaysIndex = 0;

         for (int i = 0; i < 4; i++)
         {
             transferDay += blockDay;
             currBlock = (int) transferDay;
             transferDay -= currBlock;

             for (int j = 0; j < currBlock; j++)
             {
                 blockDays[i].add(availableDays.at(avDaysIndex++));
             }
         }
         return blockDays;
     }


     public Vector<TimeWindow> progDegDistribution (Vector<TimeWindow> distributedTWs, bool degressive) {
         double transfer = 0;
         int currBlock;
         int[] percent = {5,15,30,50};
         if (degressive) ArrayUtils.reverse(percent);
         double onePercent = (double) toDistributeDates.size()/100;
         Vector<TimeWindow>tempDistributedTWs = new Vector<TimeWindow>;
        int percentCount = 0;

         Vector<Date>[] doneSplitDay = splitDay();
         availableDays.clear();
         Vector<TimeWindow>[] doneSuggested = splitBySuggestedTWs();
         suggestedTWs.clear();

         for (int i = 0; i < 4; i++)
         {
             availableDays = doneSplitDay[i];
             for (int j = 0; j < percent[i]; j++)
             {
                 suggestedTWs.addAll(doneSuggested[percentCount++]);
             }
            linearDistribution(tempDistributedTWs);
         }
         distributedTWs = tempDistributedTWs;
         return tempDistributedTWs;
     }

    public Vector<TimeWindow> linearDistribution (Vector<TimeWindow> tempDistributedTWs){

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

                        tempDistributedTWs.add(possibleTW);
                        break;
                    }
                }
                    suggestedTWs.remove(suggestedTWs.indexOf(usedTW));
                 if (k++ >= availableSize) linearDistribution(tempDistributedTWs);
            }
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

                        tempDistributedTWs.add(possibleTW);
                        break;
                    }
                }
                suggestedTWs.remove(suggestedTWs.indexOf(usedTW));
                if (f += factor > availableSize) {
                    distributedTWs = tempDistributedTWs;
                    return tempDistributedTWs;
                }
            }
        }else if (factor == 1){ // Gleich viele Termine wie Tage -> Nach diesem Aufruf ENDE
            int k = 0;
            for (Date currentDay:availableDays) {
                for (TimeWindow possibleTW:filteredTWs) {

                    if        (currentDay.getYear() == possibleTW.getStart().getYear()
                            && currentDay.getMonth() == possibleTW.getStart().getMonth()
                            && currentDay.getDate() == possibleTW.getStart().getDate()){

                        tempDistributedTWs.add(possibleTW);
                        break;
                    }
                }
                suggestedTWs.remove(suggestedTWs.indexOf(usedTW));
                k++;
            }
        }
        distributedTWs = tempDistributedTWs;
        return tempDistributedTWs;

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