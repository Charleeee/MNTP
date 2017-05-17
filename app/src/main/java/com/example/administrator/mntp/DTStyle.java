package com.example.administrator.mntp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DTStyle
{
    private static String defaultDateStyle = "yyyy-MM-dd";

    private static String defaultTimeStyle = "yyyy-MM-dd HH:mm:ss";


    public static String getPresentTime()
    {
        Date today = new Date();
        return convertDateToString(today,defaultDateStyle);
    }

    public static String getPresentTimeString()
    {
        Date today = new Date();
        return convertDateToString(today, defaultTimeStyle);
    }

    public static String convertDateToString(Date date)
    {
        return convertDateToString(date, defaultDateStyle);
    }

    public static String convertDateToString(Date date, String pattern)
    {
        String returnValue = "";

        if (date != null)
        {
            SimpleDateFormat ds = new SimpleDateFormat(pattern);
            returnValue = ds.format(date);
        }
        return returnValue;
    }
}
