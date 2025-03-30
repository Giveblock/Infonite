package app.infonitesave.utils.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Json {

    public static JsonObject getFromFile(File file) {
        String data;
        JsonObject obj;
        try {
            data = new String(Files.readAllBytes(Paths.get(file.getPath())));
            obj = JsonParser.parseString(data).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return obj;
    }


}
