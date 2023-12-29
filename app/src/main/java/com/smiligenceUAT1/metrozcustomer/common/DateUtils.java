package com.smiligenceUAT1.metrozcustomer.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.smiligenceUAT1.metrozcustomer.common.Constant.DATE_FORMAT;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.DATE_FORMAT_YYYYMD;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.DATE_TIME_FORMAT;
import static com.smiligenceUAT1.metrozcustomer.common.Constant.DATE_TIME_FORMAT_NEW;

public class DateUtils
{
    public static String fetchCurrentDate ()
    {
        DateFormat dateFormat = new SimpleDateFormat (DATE_FORMAT);
        String currentDateAndTime = dateFormat.format(new Date ());
        return currentDateAndTime;
    }

    public static String fetchFormatedCurrentDate ()
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_YYYYMD);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

    public static String fetchCurrentDateAndTime ()
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

    public static String fetchCurrentTime ()
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_NEW);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

}
