package app.infonitesave.data.tracking;

import app.infonitesave.utils.FileSystem;
import app.infonitesave.utils.helpers.Json;
import com.google.gson.JsonObject;

import java.io.File;
import java.time.LocalDate;

public class PaceCount {

    public static void updatePace(LocalDate date) {
        File paceFile = FileSystem.paceJsonFile(date);
        JsonObject pace = Json.getFromFile(paceFile);


    }

    private static boolean sortSummaries(JsonObject pace, LocalDate date) {



        return false;
    }


    private static String url(LocalDate date) {

        return "https://logistics.amazon.com/operations/execution/api/summaries?localDate=" +
                date +
                "&serviceAreaId=72a5d4a1-72f2-4648-a696-5a7437b6d96e";
    }

}
