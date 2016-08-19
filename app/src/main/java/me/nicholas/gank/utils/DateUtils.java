package me.nicholas.gank.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class DateUtils {

    public static final int ALL = 0;
    public static final int YEAR = 1;
    public static final int MONTH = 2;
    public static final int DAY = 3;

    private DateUtils() {
        throw new UnsupportedOperationException("Do not need instantiate!");
    }

    public static String getNowTime2Save() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmsssss", Locale.CHINA);
        return sdf.format(new Date());
    }

    public static String getNowLongTimes() {
        Date date = new Date();
        return String.valueOf(date.getTime() / 1000);
    }

    public static String getGankDate(String gankdate, int type) {

        SimpleDateFormat sdfOld = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = null;

        try {
            date = sdfOld.parse(gankdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdfNew;

        if (type == ALL) {
            sdfNew = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
            return sdfNew.format(date);
        } else if (type == YEAR) {
            sdfNew = new SimpleDateFormat("yyyy", Locale.CHINA);
            return sdfNew.format(date);
        } else if (type == MONTH) {
            sdfNew = new SimpleDateFormat("MM", Locale.CHINA);
            return sdfNew.format(date);
        } else if (type == DAY) {
            sdfNew = new SimpleDateFormat("dd", Locale.CHINA);
            return sdfNew.format(date);
        }

        return null;
    }

    public static String getGankDate2Show(String gankdate, int type) {

        SimpleDateFormat sdfOld = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.CHINA);
        Date date = null;

        try {
            date = sdfOld.parse(gankdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdfNew;

        if (type == ALL) {
            sdfNew = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return sdfNew.format(date);
        } else if (type == YEAR) {
            sdfNew = new SimpleDateFormat("yyyy", Locale.CHINA);
            return sdfNew.format(date);
        } else if (type == MONTH) {
            sdfNew = new SimpleDateFormat("MM", Locale.CHINA);
            return sdfNew.format(date);
        } else if (type == DAY) {
            sdfNew = new SimpleDateFormat("dd", Locale.CHINA);
            return sdfNew.format(date);
        }

        return null;
    }

    public static String getDate(int type) {

        SimpleDateFormat sdf;

        if (type == ALL) {
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
            return sdf.format(new Date());
        } else if (type == YEAR) {
            sdf = new SimpleDateFormat("yyyy", Locale.CHINA);
            return sdf.format(new Date());
        } else if (type == MONTH) {
            sdf = new SimpleDateFormat("MM", Locale.CHINA);
            return sdf.format(new Date());
        } else if (type == DAY) {
            sdf = new SimpleDateFormat("dd", Locale.CHINA);
            return sdf.format(new Date());
        }

        return null;
    }
}
