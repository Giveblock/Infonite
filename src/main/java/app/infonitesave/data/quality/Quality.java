package app.infonitesave.data.quality;

import app.infonitesave.data.quality.metrics.CC;
import app.infonitesave.data.quality.metrics.CDF;
import app.infonitesave.data.quality.metrics.DCR;
import app.infonitesave.data.quality.metrics.POD;
import com.google.gson.JsonObject;

import java.time.LocalDate;

public class Quality {

    public static void saveDay(LocalDate date) {
        CC.saveDay(date);
        POD.saveDay(date);
        DCR.saveDay(date);
        CDF.saveDay(date);
    }



}
