package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDate {
    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();
        return formatter.format(date);
    }
}
