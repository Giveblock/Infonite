package app.infonitesave.data;

import app.infonitesave.data.quality.Quality;
import app.infonitesave.data.safety.Safety;
import app.infonitesave.utils.FileSystem;

import java.time.LocalDate;

public class Data {

    public static void saveDay(LocalDate date) {
        if (FileSystem.getDayFolder(date).exists()) {
            try {
                Quality.saveDay(date);
                Safety.saveDay(date);
            } catch (Exception ignored) {}
        }
    }

}
