/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kalenderlogik;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * @author Miel
 */
public class Termin {
    String ID;
    String status;
    Date created;
    Date updated;
    String summary;
    String description;
    String colorID;
    Date start;
    Date end;
    TimeWindow time;
    boolean endTimeUnspecified;
    reminders remind; 
    String groupID;

    public String getID() {
        return ID;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getColorID() {
        return colorID;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public boolean isEndTimeUnspecified() {
        return endTimeUnspecified;
    }

    public reminders getRemind() {
        return remind;
    }

    public String getGroupID() {
        return groupID;
    }

    public TimeWindow getTime() {
        return time;
    }


    public Termin(String ID, String status, Date created, Date updated, String summary, String description, String colorID, Date start, Date end, boolean endTimeUnspecified, reminders remind, String groupID) {
        this.ID = ID;
        this.status = status;
        this.created = created;
        this.updated = updated;
        this.summary = summary;
        this.description = description;
        this.colorID = colorID;
        this.start = start;
        this.end = end;
        this.endTimeUnspecified = endTimeUnspecified;
        this.remind = remind;
        this.groupID = groupID;
        time = new TimeWindow(start,end);
    }
    
    
}

public class reminders {
    public boolean useDefault;
    public LinkedList<Pair<String,Integer>> overrides = new LinkedList();
}

