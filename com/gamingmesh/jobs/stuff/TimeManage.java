
package com.gamingmesh.jobs.stuff;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeManage {
    public static int timeInInt() {
	return timeInInt(System.currentTimeMillis());
    }

    public static int timeInInt(Long time) {
	SimpleDateFormat formatter = new SimpleDateFormat("YYMMdd");
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeInMillis(time);
	return Integer.valueOf(formatter.format(calendar.getTime()));
    }
}
