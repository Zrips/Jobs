package com.gamingmesh.jobs.stuff;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeManage {

    public static int timeInInt() {
	return timeInInt(System.currentTimeMillis());
    }

    public static int timeInInt(Long time) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeInMillis(time);
	return Integer.valueOf(new SimpleDateFormat("YYMMdd").format(calendar.getTime()));
    }
}
