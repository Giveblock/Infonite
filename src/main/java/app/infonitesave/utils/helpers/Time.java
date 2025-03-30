package app.infonitesave.utils.helpers;

import java.time.LocalTime;

public class Time {

    public static boolean before(LocalTime time) {
        return LocalTime.now().isBefore(time);
    }

    public static boolean after(LocalTime time) {
        return LocalTime.now().isAfter(time);
    }


}
