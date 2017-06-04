package com.htkshkoder.navigationdrawer;

/**
 * Created by FeJo on 12.05.2017.
 */
import com.google.api.client.util.DateTime;
import com.google.api.client.util.NullValue;
import com.google.api.services.calendar.model.EventDateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.util.logging.*;
import java.util.Arrays;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class Calendar_Logic extends Service {
    HashMap<String, Calendar_A_Event> hashCalendar_A_Events;
    Vector<Calendar_TimeWindow> suggestedTWs;
    Vector<Calendar_TimeWindow> distributedTWs;
    Vector<Calendar_TimeWindow> toDistributeDates; // <- what
    Vector<Date> availableDays;

    //CONSTRUCTORS CALENDAR_LOGIC
    public Calendar_Logic() {
        hashCalendar_A_Events = new HashMap<String, Calendar_A_Event>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setToDistributeDates(Vector<Calendar_TimeWindow> toDistributeDates) {
        this.toDistributeDates = toDistributeDates;
    }

    public void setSuggestedTWs(Vector<Calendar_TimeWindow> suggestedTWs) {
        this.suggestedTWs = suggestedTWs;
        distributedTWs.clear();
    }

    public void setSuggestedTWs(Vector<Calendar_TimeWindow> suggestedTWs, long toDistributeLength) {
        this.suggestedTWs = suggestedTWs;
        distributedTWs.clear();
        filterSuggestedTWs(toDistributeLength);
    }

    //PARSER
    public static Date dateParser(EventDateTime e) {
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

    //NEW EVENT / EDIT EVENT / DELETE EVENT
    public void addCalendar_A_Event(Calendar_A_Event t) {
        hashCalendar_A_Events.put(t.entry.getId(), t);
        try {
            saveCalendar_A_Events();
        }
        catch (Exception e)
        {
            return;
        }
        //google geben
    }

    public void editCalendar_A_Event(Calendar_A_Event t) {
        deleteCalendar_A_Event(t);
        addCalendar_A_Event(t);
        //google geben
    }

    public void deleteCalendar_A_Event(Calendar_A_Event t) {
        hashCalendar_A_Events.remove(t.entry.getId());
    }

    //SAVE AND LOAD FROM LOCAL FILES -> WHERE TO SAVE? (WORK IN PROGRESS)
    public void saveCalendar_A_Events() throws IOException {
        FileOutputStream fos = this.openFileOutput("savedata", Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(this);
        os.close();
        fos.close();
    }

    public Calendar_Logic loadCalendar_A_Events() throws IOException, ClassNotFoundException {
        FileInputStream fis = this.openFileInput("savedata");
        ObjectInputStream is = new ObjectInputStream(fis);
        Calendar_Logic savedstate = (Calendar_Logic) is.readObject();
        is.close();
        fis.close();
        return savedstate;
    }


    //DYNAMIC DATE (TEILT FREIE ZEITEN AUSSERHALB EVENTS IN TIMEWINDOWS EIN)
    public Vector<Calendar_TimeWindow> dynamicDate(EventDateTime dueTo, Date startTime, Date endTime) {
        Date dueToDate = dateParser(dueTo);
        Vector<Calendar_TimeWindow> suggestedDates = new Vector<Calendar_TimeWindow>();
        Vector<Pair<Date, Calendar_A_Event>> results = new Vector<Pair<Date, Calendar_A_Event>>();
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        for (Map.Entry<String, Calendar_A_Event> entry : hashCalendar_A_Events.entrySet()) {
            Date tempDate = dateParser(entry.getValue().entry.getStart());
            if (now.compareTo(tempDate) > 0) {
                if (dueToDate.compareTo(tempDate) < 0) {
                    results.add(new Pair(entry.getValue().entry.getStart(), entry.getValue()));
                }
            }
        }
        for (int y = now.getYear(); y <= dueToDate.getYear(); y++) {
            for (int m = now.getMonth(); m <= dueToDate.getMonth(); m++) {
                for (int d = now.getDate(); d <= dueToDate.getDate(); d++) {
                    Date startDay = new Date();
                    startDay.setYear(y);
                    startDay.setMonth(m);
                    startDay.setDate(d);
                    Date endDay = startDay;
                    startDay.setHours(startTime.getHours());
                    startDay.setMinutes(startTime.getMinutes());
                    endDay.setHours(endTime.getHours());
                    endDay.setMinutes(endTime.getMinutes());
                    Calendar_TimeWindow originalTw = new Calendar_TimeWindow(startDay, endDay);
                    suggestedDates.add(originalTw);
                }
            }
        }
        for (Pair<Date, Calendar_A_Event> t : results) {
            for (Calendar_TimeWindow tw : suggestedDates) {
                Calendar_TimeWindow[] split = tw.splitTime(tw, t.getRight().tW);
                if (split[0].isValid()) {
                    suggestedDates.remove(tw);
                    for (Calendar_TimeWindow newTw : split) {
                        suggestedDates.add(newTw);
                    }
                    break;
                }
            }
        }
        return suggestedDates;
    }

    // NEEDED FOR ALGORITHM (DELETES SMALLER TWs)
    public void filterSuggestedTWs(long toDistributeLength) {
        Vector<Integer> indexToDel = new Vector<Integer>();

        int i = 0;
        for (Calendar_TimeWindow currentTW : suggestedTWs) {
            if (currentTW.getTimeBetween() < toDistributeLength) {
                indexToDel.add(i);
            } else {
                Date day = new Date();
                day.setYear(currentTW.getStart().getYear());
                day.setMonth(currentTW.getStart().getMonth());
                day.setDate(currentTW.getStart().getDate());
                if (availableDays.indexOf(day) == -1) {
                    availableDays.add(day);
                }
            }
            i++;
        }
        for (int delIndex : indexToDel) {
            suggestedTWs.remove(delIndex);
        }
    }

    // NEEDED FOR ALGORITHM (SORTS TWs)
    public void sortSuggestedTWs() {
        Calendar_TimeWindow tempTW;
        int count = 0;
        int index = 0;
        int suggestedSize = (int) suggestedTWs.size();

        do {
            count = 0;
            index = 0;
            for (int i = 0; i < suggestedSize; i++) {
                if (suggestedTWs.elementAt(index).end.after(suggestedTWs.elementAt(index + 1).end)) {
//                    tempTW = suggestedTWs.elementAt(index);
//                    tempTW = suggestedTWs.set(index+1, tempTW);
//                    suggestedTWs.set(index, tempTW);
//                    suggestedTWs. = suggestedTWs.elementAt(index+1);
//                    suggestedTWs[index+1] = tempTW;

                    Collections.swap(suggestedTWs, index, index + 1);
                    count++;
                }
                index++;
            }
        } while (count > 0);
    }

    //NEEDED FOR ALGORITHM (SPLITS INTO HUNDRED BLOCKS FOR TWs ASSIGNMENT)
    public Vector<Vector<Calendar_TimeWindow>> splitBySuggestedTWs() {
        Vector<Vector<Calendar_TimeWindow>> blockSuggested = new Vector<Vector<Calendar_TimeWindow>>(100);
        double blockSize = (double) suggestedTWs.size() / 100; // 1% of suggestedTWs
        // double blockSize = (double) suggestedTWs.size()/4;
        double transfer = 0;
        int currBlock;
        int avSugIndex = 0;

        for (int i = 0; i < 100; i++) {
            transfer += blockSize;
            currBlock = (int) transfer;
            transfer -= currBlock;

            for (int j = 0; j < currBlock; j++) {
                blockSuggested.elementAt(i).add(suggestedTWs.elementAt(avSugIndex++));
            }
        }
        return blockSuggested;
    }

    // NEEDED FOR ALGORITHM (SPLITS INTO 4 BLOCKS FOR PERCENTAGE)
    public Vector<Vector<Date>> splitDay() {
        Collections.sort(availableDays);
        Vector<Vector<Date>> blockDays = new Vector<Vector<Date>>(4);
        double blockDay = (double) availableDays.size() / 4;
        double transferDay = 0;
        int currBlock;
        int avDaysIndex = 0;

        for (int i = 0; i < 4; i++) {
            transferDay += blockDay;
            currBlock = (int) transferDay;
            transferDay -= currBlock;

            for (int j = 0; j < currBlock; j++) {
                blockDays.elementAt(i).add(availableDays.elementAt(avDaysIndex++));
            }
        }
        return blockDays;
    }

    //LINEAR DISTRIBUTION (ALSO NEEDED BY PROG AND DEG DISTRIBUTION)
    public Vector<Calendar_TimeWindow> linearDistribution(Vector<Calendar_TimeWindow> tempDistributedTWs) {

        int availableSize = availableDays.size();
        int suggestedSize = suggestedTWs.size();
        int toDistributeAmount = toDistributeDates.size();
        Calendar_TimeWindow usedTW = new Calendar_TimeWindow();

        if (toDistributeAmount == 0) return new Vector<Calendar_TimeWindow>();
        double factor = (double) availableSize / toDistributeAmount;


        if (factor < 1) { // more events than days -> recursive call of function
            int k = 0;
            for (Date currentDay : availableDays) {
                for (Calendar_TimeWindow possibleTW : suggestedTWs) {

                    if (currentDay.getYear() == possibleTW.getStart().getYear()
                            && currentDay.getMonth() == possibleTW.getStart().getMonth()
                            && currentDay.getDate() == possibleTW.getStart().getDate()) {

                        tempDistributedTWs.add(possibleTW);
                        usedTW = possibleTW;
                        break;
                    }
                }
                suggestedTWs.remove(suggestedTWs.indexOf(usedTW));
                if (k++ >= availableSize) linearDistribution(tempDistributedTWs);
            }
        } else if (factor > 1) { // more days than events -> algortihm then exit
            int k = 0;
            double f = factor - 1;
            int currentfactor;
            for (Date currentDay : availableDays) {

                currentfactor = (int) f;
                if (k != currentfactor)  // when index of current day isn't equal to current factor
                {                        // jump to the next day and compare again
                    k++;
                    continue;
                }
                for (Calendar_TimeWindow possibleTW : suggestedTWs) { // if index == factor, put TW of this day into distributedTWs

                    if (currentDay.getYear() == possibleTW.getStart().getYear()
                            && currentDay.getMonth() == possibleTW.getStart().getMonth()
                            && currentDay.getDate() == possibleTW.getStart().getDate()) {

                        tempDistributedTWs.add(possibleTW);
                        usedTW = possibleTW;
                        break;
                    }
                }
                suggestedTWs.remove(suggestedTWs.indexOf(usedTW));
                if ((f += factor) > availableSize) {
                    distributedTWs = tempDistributedTWs;
                    return tempDistributedTWs;
                }
            }
        } else if (factor == 1) { // same events as available days -> exit after this call
            int k = 0;
            for (Date currentDay : availableDays) {
                for (Calendar_TimeWindow possibleTW : suggestedTWs) {

                    if (currentDay.getYear() == possibleTW.getStart().getYear()
                            && currentDay.getMonth() == possibleTW.getStart().getMonth()
                            && currentDay.getDate() == possibleTW.getStart().getDate()) {

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

    public Vector<Calendar_TimeWindow> progDegDistribution(Vector<Calendar_TimeWindow> distributedTWs, boolean degressive) {
        double transfer = 0;
        int currBlock;
        List<Integer> percent = new ArrayList();
        percent.add(5);
        percent.add(15);
        percent.add(30);
        percent.add(50);
        if (degressive) Collections.reverse(percent);
        double onePercent = (double) toDistributeDates.size() / 100;
        Vector<Calendar_TimeWindow> tempDistributedTWs = new Vector<Calendar_TimeWindow>();
        int percentCount = 0;

        Vector<Vector<Date>> doneSplitDay = splitDay();
        availableDays.clear();
        Vector<Vector<Calendar_TimeWindow>> doneSuggested = splitBySuggestedTWs();
        suggestedTWs.clear();

        for (int i = 0; i < 4; i++) {
            availableDays = doneSplitDay.elementAt(i);
            for (int j = 0; j < percent.get(i); j++) {
                suggestedTWs.addAll(doneSuggested.elementAt(percentCount++));
            }
            linearDistribution(tempDistributedTWs);
        }
        distributedTWs = tempDistributedTWs;
        return tempDistributedTWs;
    }
}

