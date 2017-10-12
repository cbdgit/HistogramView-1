package com.salmonzhg.histogramview_demo.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Salmon on 2016/6/21 0021.
 */
public class DateUtils {

    /**
     * calendar转字符串
     *
     * @param calendar
     * @return 格式如："2016-5-25 00:00:00"的字符串
     */
    public static String calendarToString(Calendar calendar) {
        String s = calendar.get(Calendar.YEAR) + "-" +
                (calendar.get(Calendar.MONTH) + 1) + "-" +
                calendar.get(Calendar.DAY_OF_MONTH) + " 00:00:00";
        return s;
    }

    /**
     * 整形转换星期
     *
     * @param index
     * @return
     */
    public static String intToWeek(int index) {
        String result = "";
        switch (index) {
            case 1:
                result = "MON";
                break;
            case 2:
                result = "TUE";
                break;
            case 3:
                result = "WED";
                break;
            case 4:
                result = "THU";
                break;
            case 5:
                result = "FRI";
                break;
            case 6:
                result = "SAT";
                break;
            case 7:
                result = "SUN";
                break;
        }
        return result;
    }
}
