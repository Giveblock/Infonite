package app.infonitesave.data.quality.metrics;

import app.infonitesave.data.URL;
import app.infonitesave.data.quality.Quality;
import app.infonitesave.utils.FileSystem;
import app.infonitesave.utils.Http;
import app.infonitesave.utils.helpers.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class POD {

    public static void saveDay(LocalDate date) {
        File podFile = FileSystem.qualityJsonFile("POD.json", date);
        JsonObject pod = Json.getFromFile(podFile);
        boolean podSaved = sortData(pod, date);
        if (podSaved) {
            try {
                FileWriter writer = new FileWriter(podFile);
                writer.write(pod.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

    }

    private static boolean sortData(JsonObject pod, LocalDate date) {
        if (pod.has("date-saved")) {
            return false;
        }
        JsonArray httpData = Http.getJson(URL.quality(date)).getAsJsonObject("tableData")
                .getAsJsonObject("da_dsp_station_daily_supplemental_quality")
                .getAsJsonArray("rows");
        if (httpData != null && !httpData.isEmpty()) {
            for (JsonElement element : httpData) {
                JsonObject o = element.getAsJsonObject();
                String id = null;
                if (o.has("transporter_id")) {
                    id = o.get("transporter_id").getAsString();
                }
                int podOpps = 0;
                if (o.has("pod_opportunity")) {
                    podOpps = o.get("pod_opportunity").getAsInt();
                }
                if (id != null && podOpps > 0) {
                    JsonObject x = new JsonObject();
                    if (o.has("da_name")) {
                        x.addProperty("name", o.get("da_name").getAsString());
                    }
                    if (o.has("pod_success_rate")) {
                        x.addProperty("pod-score", o.get("pod_success_rate").getAsDouble());
                    }
                    if (o.has("pod_success_rate_tier")) {
                        x.addProperty("pod-tier", o.get("pod_success_rate_tier").getAsString());
                    }
                    x.addProperty("pod-opps", podOpps);
                    if (o.has("pde_photos_taken")) {
                        x.addProperty("photos-taken", o.get("pde_photos_taken").getAsInt());
                    }
                    if (o.has("pod_success")) {
                        x.addProperty("pod-success", o.get("pod_success").getAsInt());
                    }
                    if (o.has("pod_bypass")) {
                        x.addProperty("pod-bypass", o.get("pod_bypass").getAsInt());
                    }
                    if (o.has("pod_inappropriate_photos")) {
                        x.addProperty("pod-inappropriate", o.get("pod_inappropriate_photos").getAsInt());
                    }

                    pod.getAsJsonObject("data").add(id, x);
                }
            }
            pod.addProperty("date-saved", LocalDate.now().toString());
            return true;
        }
        return false;
    }


}
