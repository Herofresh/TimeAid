package com.htkshkoder.navigationdrawer;

import com.google.api.services.calendar.model.EventDateTime;

/**
 * Created by bjorn on 05.05.2017.
 */

public class Calendar_L_MultiDate extends Calendar_A_Event {
    public Calendar_L_MultiDate(String id, EventDateTime startDate, EventDateTime endDate) {
        super(id, startDate, endDate);
    }

    /*
        SingleDate[]
        showEintrag()
        createMultiDateEintrag()
     */
}
