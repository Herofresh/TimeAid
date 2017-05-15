package com.htkshkoder.navigationdrawer;

/**
 * Created by FeJo on 12.05.2017.
 */
import java.util.*;


public class Calendar_TimeWindow {
    Date start;
    Date end;
    boolean valid = true;

    public Calendar_TimeWindow() {
        this.valid = false;
    }

    public Calendar_TimeWindow(Date start, Date end) {
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

    public Calendar_TimeWindow[] splitTime(Calendar_TimeWindow toSplit, Calendar_TimeWindow toComp) {

        /*Check Start and End from both TimeWindow
        If same -> 1 new TimeWindow
        Else -> 2 new TimeWindow
        Start = toSplit.Start
        End = toComp.Start
        Start2 = toComp.End
        End2 = toSplit.End
        */
        Calendar_TimeWindow[] retValue;

        if (toSplit.getStart() == toComp.getStart() && toSplit.getEnd() == toComp.getEnd()){
            Calendar_TimeWindow invalidTw = new Calendar_TimeWindow();
            retValue = new Calendar_TimeWindow[1];
            retValue[0] = invalidTw;
            return retValue;
        }
        else if (toSplit.getStart() == toComp.getStart()){
            Calendar_TimeWindow remainingTw = new Calendar_TimeWindow(toComp.getEnd(), toSplit.getEnd());
            retValue = new Calendar_TimeWindow[1];
            retValue[0] = remainingTw;
            return retValue;
        }
        else if (toSplit.getEnd() == toComp.getEnd()){
            Calendar_TimeWindow remainingTw = new Calendar_TimeWindow(toSplit.getStart(), toComp.getStart());
            retValue = new Calendar_TimeWindow[1];
            retValue[0] = remainingTw;
            return retValue;
        }
        else if (toComp.getStart().compareTo(toSplit.getStart()) > 0 && toComp.getEnd().compareTo(toSplit.getEnd()) < 0){
            Calendar_TimeWindow firstTw = new Calendar_TimeWindow(toSplit.getStart(), toComp.getStart());
            Calendar_TimeWindow secondTw = new Calendar_TimeWindow(toComp.getEnd(), toSplit.getEnd());
            retValue = new Calendar_TimeWindow[2];
            retValue[0] = firstTw;
            retValue[1] = secondTw;
            return retValue;
        }
        else{
            Calendar_TimeWindow invalidTw = new Calendar_TimeWindow();
            retValue = new Calendar_TimeWindow[1];
            retValue[0] = invalidTw;
            return retValue;
        }
    }
}
