package app.infonitesave.utils.helpers;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

public class Date {


    public static int getWeek(LocalDate date) {
        int w = date.getDayOfWeek().getValue();
        if (w == 7) {
            w=0;
        }
        LocalDate sunday = date.minusDays(w);
        return ((sunday.getDayOfYear() + 6)/7) + 1;
    }

    public static int week(LocalDate date) {
        int doy = date.getDayOfYear();
        return (doy/7) + 1;
    }

    public static String dow(LocalDate date) {
        String day = date.getDayOfWeek().toString().toLowerCase();
        return StringUtils.capitalize(day);
    }

    public static String fs(LocalDate date) {

        return dow(date) + " " + date.getMonthValue() + "-" + date.getDayOfMonth();

    }




}
