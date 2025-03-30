package app.infonitesave.messages;

import app.infonitesave.messages.daily.CDFMsg;
import app.infonitesave.utils.helpers.Time;

import java.time.LocalDate;
import java.time.LocalTime;

public class Messages {

    public static void sendDaily(LocalDate date) {

        //After 10am and Before 8pm
        if (Time.after(LocalTime.of(10, 0)) && Time.before(LocalTime.of(20, 0))) {
            CDFMsg.sendMsg(date);
        }



    }


}
