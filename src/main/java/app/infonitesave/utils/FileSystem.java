package app.infonitesave.utils;

import app.infonitesave.utils.helpers.Date;
import app.infonitesave.utils.helpers.Json;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Locale;

public class FileSystem {

    public static File getDayFolder(LocalDate date) {
        File file = new File("Archive/" + date.getYear() + "/Week-" + Date.getWeek(date) + "/" + Date.fs(date));
        if (!file.exists()) {
            if (file.mkdirs()) {
                new File(file, "Quality").mkdirs();
                new File(file, "Safety").mkdirs();
            }
        }
        return file;
    }

    public static File paceJsonFile(LocalDate date) {
        File file = new File(getDayFolder(date), "pace.json");
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("data-date", date.toString());
                    o.add("data", new JsonObject());
                    FileWriter writer = new FileWriter(file);
                    writer.write(o.toString());
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        return file;
    }

    public static File safetyJsonFile(String name, LocalDate date) {
        File file = new File(getDayFolder(date), "Safety/" + name);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("data-date", date.toString());
                    o.add("data", new JsonObject());
                    FileWriter writer = new FileWriter(file);
                    writer.write(o.toString());
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
        return file;
    }
    public static File qualityJsonFile(String name, LocalDate date) {
        File file = new File(getDayFolder(date), "Quality/" + name);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("data-date", date.toString());
                    o.add("data", new JsonObject());

                    FileWriter writer = new FileWriter(file);
                    writer.write(o.toString());
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

        return file;
    }
    public static File getWeekFolder(LocalDate date) {
        File file = new File("Archive/" + date.getYear() + "/Week-" + Date.getWeek(date));
        if (!file.exists() && file.mkdirs()) {
            return file;
        }
        return file;
    }
    public static File getReportsFolder(LocalDate date) {
        File file = new File(getWeekFolder(date), "WeeklyReports");
        if (!file.exists() && file.mkdirs()) {
            try {
                File json = new File(file, "URLs.json");
                if (!json.exists()) {
                    json.createNewFile();
                }
                JsonObject o = new JsonObject();
                FileWriter writer = new FileWriter(json);
                writer.write(o.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }
    public static void saveReport(File file, URL url) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (InputStream in = url.openStream()){
            Files.copy(in, Paths.get(file.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
