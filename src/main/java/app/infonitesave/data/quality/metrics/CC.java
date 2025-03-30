package app.infonitesave.data.quality.metrics;

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
import java.time.LocalTime;

public class CC {

    public static void saveDay(LocalDate date) {
        File ccFile = FileSystem.qualityJsonFile("CC.json", date);
        JsonObject cc = Json.getFromFile(ccFile);
        boolean ccDrivers = sortDrivers(cc, date);
        boolean ccTBAs = sortTBAs(cc, date);
        if (ccDrivers || ccTBAs) {
            try {
                FileWriter writer = new FileWriter(ccFile);
                writer.write(cc.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static boolean sortDrivers(JsonObject cc, LocalDate date) {
        if (cc.has("drivers-saved")) {
            return false;
        }
        String url = (
                "https://logistics.amazon.com/performance/api/v1/getData?dataSetId=da_dsp_daily_lmcx_call_text&dsp=LVEL&from=" +
                        date +
                        "&program=AMZL&station=DCL5&timeFrame=Daily&to=" +
                        date
        );
        JsonArray httpData = Http.getJson(url).getAsJsonObject("tableData")
                .getAsJsonObject("da_dsp_daily_lmcx_call_text")
                .getAsJsonArray("rows");
        if (httpData != null && !httpData.isEmpty()) {
            for (JsonElement element : httpData) {
                JsonObject o = element.getAsJsonObject();
                String id = null;
                //ID Check
                if (o.has("transporter_id")) {
                    id = o.get("transporter_id").getAsString();
                }
                if (id != null) {
                    JsonObject x = new JsonObject();
                    if (cc.getAsJsonObject("data").has(id)) {
                        x = cc.getAsJsonObject("data").getAsJsonObject(id);
                    }
                    if (o.has("da_name") && !x.has("name")) {
                        x.addProperty("name", o.get("da_name").getAsString());
                    }
                    if (o.has("contact_compliance") && !x.has("cc-score")) {
                        x.addProperty("cc-score", o.get("contact_compliance").getAsDouble());
                    }
                    if (o.has("contact_compliance_tier") && !x.has("cc-tier")) {
                        x.addProperty("cc-tier", o.get("contact_compliance_tier").getAsString());
                    }
                    if (o.has("contact_opportunity") && !x.has("cc-opps")) {
                        x.addProperty("cc-opps", o.get("contact_opportunity").getAsInt());
                    }
                    if (o.has("contacts") && !x.has("contacts")) {
                        x.addProperty("contacts", o.get("contacts").getAsInt());
                    }
                    if (o.has("calls") && !x.has("calls")) {
                        x.addProperty("calls", o.get("calls").getAsInt());
                    }
                    if (o.has("texts") && !x.has("texts")) {
                        x.addProperty("texts", o.get("texts").getAsInt());
                    }
                    if (!x.has("tba-data")) {
                        JsonObject i = new JsonObject();
                        i.add("contacts", new JsonArray());
                        i.add("no-contact", new JsonArray());
                        x.add("tba-data", i);
                    }
                    cc.getAsJsonObject("data").add(id, x);
                }
            }
            cc.addProperty("drivers-saved", LocalDate.now().toString());
            return true;
        }
        return false;
    }
    private static boolean sortTBAs(JsonObject cc, LocalDate date) {
        if (cc.has("tbas-saved")) {
            return false;
        }
        String url = (
            "https://logistics.amazon.com/performance/api/v1/getData?dataSetId=dsp_station_lmcx_call_text_tba&dsp=LVEL&from=" +
                    date +
                    "&program=AMZL&station=DCL5&timeFrame=Daily&to=" +
                    date
        );
        JsonArray httpData = Http.getJson(url).getAsJsonObject("tableData")
                .getAsJsonObject("dsp_station_lmcx_call_text_tba")
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
                    if (!cc.getAsJsonObject("data").has(id)) {
                        cc.getAsJsonObject("data").add(id, x);
                    }
                    x = cc.getAsJsonObject("data").getAsJsonObject(id);
                    if (!x.has("name")&& o.has("da_name")) {
                        x.addProperty("name", o.get("da_name").getAsString());
                    }
                    if (!x.has("tba-data")) {
                        JsonObject i = new JsonObject();
                        i.add("contacts", new JsonArray());
                        i.add("no-contact", new JsonArray());
                        x.add("tba-data", i);
                    }

                    String completed = "";
                    if (o.has("contact_completed") && o.has("tracking_id")) {
                        completed = o.get("contact_completed").getAsString();
                        String tba = o.get("tracking_id").getAsString();
                        if (completed.equalsIgnoreCase("Y") && !tba.equalsIgnoreCase("")) {
                            cc.getAsJsonObject("data").getAsJsonObject(id)
                                    .getAsJsonObject("tba-data")
                                    .getAsJsonArray("contacts").add(tba);
                        }
                        if (completed.equalsIgnoreCase("N") && !tba.equalsIgnoreCase("")) {
                            cc.getAsJsonObject("data").getAsJsonObject(id)
                                    .getAsJsonObject("tba-data")
                                    .getAsJsonArray("no-contact").add(tba);
                        }
                    }

                }

            }

            cc.addProperty("tbas-saved", LocalDate.now().toString());
            return true;
        }
        return false;
    }


}
