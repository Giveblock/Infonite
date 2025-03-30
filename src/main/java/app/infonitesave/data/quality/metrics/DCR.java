package app.infonitesave.data.quality.metrics;

import app.infonitesave.data.URL;
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

public class DCR {

    public static void saveDay(LocalDate date) {
        File dcrFile = FileSystem.qualityJsonFile("DCR.json", date);
        JsonObject dcr = Json.getFromFile(dcrFile);
        boolean dcrSaved = sortData(dcr, date);
        if (dcrSaved) {
            try {
                FileWriter writer = new FileWriter(dcrFile);
                writer.write(dcr.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static boolean sortData(JsonObject dcr, LocalDate date) {
        if (dcr.has("date-saved")) {
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
                int dispatched = 0;
                if (o.has("dispatched")) {
                    dispatched = o.get("dispatched").getAsInt();
                }
                if (id != null && dispatched > 0) {
                    JsonObject x = new JsonObject();
                    if (o.has("da_name")) {
                        x.addProperty("name", o.get("da_name").getAsString());
                    }
                    if (o.has("delivery_success")) {
                        x.addProperty("dcr-score", o.get("delivery_success").getAsDouble());
                    }
                    if (o.has("delivery_success_tier")) {
                        x.addProperty("dcr-tier", o.get("delivery_success_tier").getAsString());
                    }
                    x.addProperty("dispatched", dispatched);
                    if (o.has("delivered")) {
                        x.addProperty("delivered", o.get("delivered").getAsInt());
                    }
                    if (o.has("return_to_station_all")) {
                        int returns = o.get("return_to_station_all").getAsInt();
                        x.addProperty("returns", returns);
                        if (returns > 0) {
                            JsonObject r = new JsonObject();
                            //Business Closed
                            if (o.has("return_to_station_bc")) {
                                int bc = o.get("return_to_station_bc").getAsInt();
                                if (bc > 0) {
                                    r.addProperty("business-closed", bc);
                                }
                            }
                            //Customer Unavailable
                            if (o.has("return_to_station_cu")) {
                                int cu = o.get("return_to_station_cu").getAsInt();
                                if (cu > 0) {
                                    r.addProperty("customer-unavailable", cu);
                                }
                            }
                            //No Safe Location
                            if (o.has("return_to_station_nsl")) {
                                int nsl = o.get("return_to_station_nsl").getAsInt();
                                if (nsl > 0) {
                                    r.addProperty("no-safe-location", nsl);
                                }
                            }
                            //Other Returns
                            if (o.has("rts_other")) {
                                int other = o.get("rts_other").getAsInt();
                                if (other > 0) {
                                    r.addProperty("other-returns", other);
                                }
                            }
                            //Out of Drive Time
                            if (o.has("return_to_station_oodt")) {
                                int oodt = o.get("return_to_station_oodt").getAsInt();
                                if (oodt > 0) {
                                    r.addProperty("out-of-drive-time", oodt);
                                }
                            }
                            //Unable to Access
                            if (o.has("return_to_station_uta")) {
                                int uta = o.get("return_to_station_uta").getAsInt();
                                if (uta > 0) {
                                    r.addProperty("unable-to-access", uta);
                                }
                            }
                            //Unable to Locate
                            if (o.has("return_to_station_utl")) {
                                int utl = o.get("return_to_station_utl").getAsInt();
                                if (utl > 0) {
                                    r.addProperty("unable-to-locate", utl);
                                }
                            }
                            //Dog Returns
                            if (o.has("return_to_station_dog")){
                                int dog = o.get("return_to_station_dog").getAsInt();
                                if (dog > 0) {
                                    r.addProperty("dog-returns", dog);
                                }
                            }

                            x.add("return-details", r);
                        }
                    }
                    if (o.has("dnr")) {
                        x.addProperty("dnr", o.get("dnr").getAsInt());
                    }
                    if (o.has("delivery_attempt")) {
                        x.addProperty("delivery-attempts", o.get("delivery_attempt").getAsInt());
                    }

                    dcr.getAsJsonObject("data").add(id, x);
                }
            }
            dcr.addProperty("date-saved", LocalDate.now().toString());
            return true;
        }
        return false;
    }




}
