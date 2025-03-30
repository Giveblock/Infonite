package app.infonitesave.data.safety;

import app.infonitesave.utils.FileSystem;
import app.infonitesave.utils.Http;
import app.infonitesave.utils.helpers.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class Safety {


    public static void saveDay(LocalDate date) {
        File safetyFile = FileSystem.safetyJsonFile("Safety.json", date);
        JsonObject safety = Json.getFromFile(safetyFile);
        boolean safetyDrivers = sortDrivers(safety, date);
        boolean safetyAlerts = sortAlerts(safety, date);

        if (safetyDrivers || safetyAlerts) {
            try {
                FileWriter writer = new FileWriter(safetyFile);
                writer.write(safety.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static boolean sortDrivers(JsonObject safety, LocalDate date) {
        if (safety.has("drivers-saved")) {
            return false;
        }
        String url = (
                "https://logistics.amazon.com/performance/api/v1/getData?dataSetId=da_dsp_station_daily_safety_oss_intraday&dsp=LVEL&dspId=59b162d3-7dd4-4141-920d-13fd2293e565&from=" +
                        date +
                        "&station=DCL5&timeFrame=Daily&to=" +
                        date
        );
        JsonArray httpData = Http.getJson(url).getAsJsonObject("tableData")
                .getAsJsonObject("da_dsp_station_daily_safety_oss_intraday")
                .getAsJsonArray("rows");
        if (httpData != null && !httpData.isEmpty()) {
            for (JsonElement element : httpData) {
                JsonObject o = element.getAsJsonObject();
                String id = null;
                if (o.has("transporter_id")) {
                    id = o.get("transporter_id").getAsString();
                }
                if (id != null) {
                    JsonObject x = new JsonObject();
                    if (safety.getAsJsonObject("data").has(id)) {
                        x = safety.getAsJsonObject("data").getAsJsonObject(id);
                    }
                    if (o.has("da_name") && !x.has("name")) {
                        x.addProperty("name", o.get("da_name").getAsString());
                    }
                    //Overall
                    if (o.has("da_oss_score")) {
                        x.addProperty("oss-score", o.get("da_oss_score").getAsDouble());
                        if (o.has("da_oss_tier")) {
                            x.addProperty("oss-tier", o.get("da_oss_tier").getAsString());
                        }
                    }

                    //Metric Details
                    JsonObject metrics = new JsonObject();
                    if (o.has("fico_score")) {
                        if (o.has("fico_raw")) {
                            metrics.addProperty("fico-raw", o.get("fico_raw").getAsInt());
                        }
                        metrics.addProperty("fico-score", o.get("fico_score").getAsDouble());
                    }
                    if (o.has("seatbelt_score")) {
                        metrics.addProperty("seatbelt-score", o.get("seatbelt_score").getAsInt());
                    }
                    if (o.has("speeding_score")) {
                        metrics.addProperty("speeding-score", o.get("speeding_score").getAsDouble());
                    }
                    if (o.has("distraction_score")) {
                        metrics.addProperty("distractions-score", o.get("distraction_score").getAsDouble());
                    }
                    if (o.has("following_distance_score")) {
                        double fdScore = Double.parseDouble(o.get("following_distance_score").getAsString());
                        metrics.addProperty("follow-distance-score", fdScore);
                    }
                    if (o.has("sign_signal_score")) {
                        metrics.addProperty("sign-signal-score", o.get("sign_signal_score").getAsDouble());
                    }
                    x.add("metric-scores", metrics);

                    //Total Events
                    JsonObject events = new JsonObject();
                    if (o.has("seatbelt_events")) {
                        int i = o.get("seatbelt_events").getAsInt();
                        if (o.has("seatbelt_events_weighted")) {
                            int w = o.get("seatbelt_events_weighted").getAsInt();
                            if (w != i) {
                                i = w;
                            }
                        }
                        events.addProperty("seatbelt-events", i);
                    }
                    if (o.has("speeding_events")) {
                        int i = o.get("speeding_events").getAsInt();
                        if (o.has("speeding_events_weighted")) {
                            int w = o.get("speeding_events_weighted").getAsInt();
                            if (w != i) {
                                i = w;
                            }
                        }

                        events.addProperty("speeding-events", i);
                    }
                    if (o.has("distraction_events")) {
                        int i = o.get("distraction_events").getAsInt();
                        if (o.has("distraction_events_weighted")) {
                            int w = o.get("distraction_events_weighted").getAsInt();
                            if (w != i) {
                                i = w;
                            }
                        }

                        events.addProperty("distraction-events", i);
                    }
                    if (o.has("following_distance_events")) {
                        int i = o.get("following_distance_events").getAsInt();
                        if (o.has("following_distance_events_weighted")) {
                            int w = o.get("following_distance_events_weighted").getAsInt();
                            if (w != i) {
                                i = w;
                            }
                        }
                        events.addProperty("follow-distance-events", i);
                    }
                    if (o.has("sign_signal_events")) {
                        int i = o.get("sign_signal_events").getAsInt();
                        if (o.has("sign_signal_events_weighted")) {
                            int w = o.get("sign_signal_events_weighted").getAsInt();
                            if (w != i) {
                                i = w;
                            }
                        }

                        events.addProperty("sign-signal-events", i);
                    }
                    x.add("total-events", events);

                    if (!x.has("events")) {
                        x.add("events", new JsonArray());
                    }
                    safety.getAsJsonObject("data").add(id, x);
                }
            }
            safety.addProperty("drivers-saved", LocalDate.now().toString());
            return true;
        }
        return false;
    }

    private static boolean sortAlerts(JsonObject safety, LocalDate date) {
        if (safety.has("alerts-saved")) {
            return false;
        }
        String url = (
                "https://logistics.amazon.com/performance/api/v1/getData?dataSetId=da_dsp_station_daily_safety_oss_events_intraday&dsp=LVEL&dspId=59b162d3-7dd4-4141-920d-13fd2293e565&from=" +
                        date +
                        "&station=DCL5&timeFrame=Daily&to=" +
                        date
        );
        JsonArray httpData = Http.getJson(url).getAsJsonObject("tableData")
                .getAsJsonObject("da_dsp_station_daily_safety_oss_events_intraday")
                .getAsJsonArray("rows");
        if (httpData != null && !httpData.isEmpty()) {
            for (JsonElement element : httpData) {
                JsonObject o = element.getAsJsonObject();
                String id = null;
                if (o.has("transporter_id")) {
                    id = o.get("transporter_id").getAsString();
                }
                if (id != null) {
                    JsonObject da = new JsonObject();
                    if (!safety.getAsJsonObject("data").has(id)) {
                        if (o.has("da_name")) {
                            da.addProperty("name", o.get("da_name").getAsString());
                        }
                        da.add("events", new JsonArray());
                        safety.getAsJsonObject("data").add(id, da);
                    }
                    da = safety.getAsJsonObject("data").getAsJsonObject(id);
                    if (o.has("oss_metric")) {
                        JsonObject x = new JsonObject();
                        if (o.has("source")) {
                            x.addProperty("source", o.get("source").getAsString());
                        }
                        if (o.has("event_id")) {
                            x.addProperty("event-id", o.get("event_id").getAsString());
                        }
                        if (o.has("oss_metric")) {
                            x.addProperty("metric", o.get("oss_metric").getAsString());
                            JsonObject details = new JsonObject();
                            if (o.has("subtype")) {
                                details.addProperty("metric-subtype", o.get("subtype").getAsString());
                            }
                            if (o.has("severity")) {
                                String severity = o.get("severity").getAsString().toLowerCase();
                                details.addProperty("severity", StringUtils.capitalize(severity));
                            }
                            x.add("details", details);
                        }
                        if (o.has("oss_impact")) {
                            x.addProperty("oss-impact", o.get("oss_impact").getAsString());
                        }
                        JsonObject vehicle = new JsonObject();
                        if (o.has("vehicle_id")) {
                            vehicle.addProperty("vin", o.get("vehicle_id").getAsString());
                        }
                        if (o.has("vehicle_ownershiptype")) {
                            vehicle.addProperty("ownership-type", o.get("vehicle_ownershiptype").getAsString());
                        }
                        x.add("vehicle-info", vehicle);

                        da.getAsJsonArray("events").add(x);
                    }
                }
            }
            safety.addProperty("alerts-saved", LocalDate.now().toString());
            return true;
        }
        return false;
    }


}
