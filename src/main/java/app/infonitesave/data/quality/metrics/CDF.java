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

public class CDF {

    public static void saveDay(LocalDate date) {
        File cdfFile = FileSystem.qualityJsonFile("CDF.json", date);
        JsonObject cdf = Json.getFromFile(cdfFile);
        boolean cdfDrivers = sortDrivers(cdf, date);
        boolean cdfTBAs = sortTBAs(cdf, date);
        if (cdfDrivers || cdfTBAs) {
            try {
                FileWriter writer = new FileWriter(cdfFile);
                writer.write(cdf.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static boolean sortDrivers(JsonObject cdf, LocalDate date) {
        if (cdf.has("drivers-saved")) {
            return false;
        }
        String url = (
                "https://logistics.amazon.com/performance/api/v1/getData?dataSetId=da_dsp_daily_cdf&dsp=LVEL&from=" +
                        date +
                        "&program=AMZL&station=DCL5&timeFrame=Daily&to=" +
                        date
        );
        JsonArray httpData = Http.getJson(url).getAsJsonObject("tableData")
                .getAsJsonObject("da_dsp_daily_cdf")
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
                    if (cdf.getAsJsonObject("data").has(id)) {
                        x = cdf.getAsJsonObject("data").getAsJsonObject(id);
                    }

                    if (o.has("da_name") && !x.has("name")) {
                        x.addProperty("name", o.get("da_name").getAsString());
                    }
                    if (o.has("positive_response_cnt")) {
                        int pos = o.get("positive_response_cnt").getAsInt();
                        x.addProperty("positive-feedback", pos);
                        if (pos > 0) {
                            JsonObject p = new JsonObject();
                            if (o.has("respectful_of_property_cnt")) {
                                p.addProperty("respectful-of-property", o.get("respectful_of_property_cnt").getAsInt());
                            }
                            if (o.has("followed_instructions_cnt")) {
                                p.addProperty("followed-instructions", o.get("followed_instructions_cnt").getAsInt());
                            }
                            if (o.has("friendly_cnt")) {
                                p.addProperty("friendly", o.get("friendly_cnt").getAsInt());
                            }
                            if (o.has("above_and_beyond_cnt")) {
                                p.addProperty("above-and-beyond", o.get("above_and_beyond_cnt").getAsInt());
                            }
                            if (o.has("delivered_with_care_cnt")) {
                                p.addProperty("delivered-with-care", o.get("delivered_with_care_cnt").getAsInt());
                            }
                            if (o.has("care_for_others_cnt")) {
                                p.addProperty("care-for-others", o.get("care_for_others_cnt").getAsInt());
                            }
                            if (o.has("tmd_cnt")) {
                                p.addProperty("alexa-tmd", o.get("tmd_cnt").getAsInt());
                            }
                            x.add("positive-details", p);
                        }
                    }
                    if (o.has("negative_response_cnt")) {
                        int neg = o.get("negative_response_cnt").getAsInt();
                        x.addProperty("negative-feedback", neg);
                        if (neg > 0) {
                            JsonObject n = new JsonObject();
                            if (o.has("driver_mishandled_package_cnt")) {
                                n.addProperty("mishandled-package", o.get("driver_mishandled_package_cnt").getAsInt());
                            }
                            if (o.has("driver_was_unprofessional_cnt")) {
                                n.addProperty("unprofessional", o.get("driver_was_unprofessional_cnt").getAsInt());
                            }
                            if (o.has("not_delivered_to_preferred_location_cnt")) {
                                n.addProperty("didnt-follow-instructions", o.get("not_delivered_to_preferred_location_cnt").getAsInt());
                            }
                            if (o.has("delivered_to_wrong_address_cnt")) {
                                n.addProperty("wrong-address", o.get("delivered_to_wrong_address_cnt").getAsInt());
                            }
                            if (o.has("never_received_delivery_cnt")) {
                                n.addProperty("never-received-delivery", o.get("never_received_delivery_cnt").getAsInt());
                            }
                            if (o.has("driver_was_driving_unsafely_cnt")) {
                                n.addProperty("unsafe_driving", o.get("driver_was_driving_unsafely_cnt").getAsInt());
                            }
                            x.add("negative-details", n);
                        }
                    }
                    if (!x.has("tba-data")) {
                        x.add("tba-data", tbaObject());
                    }
                    cdf.getAsJsonObject("data").add(id, x);
                }
            }
            cdf.addProperty("drivers-saved", LocalDate.now().toString());
            return true;
        }
        return false;
    }
    private static boolean sortTBAs(JsonObject cdf, LocalDate date) {
        if (cdf.has("tbas-saved")) {
            return false;
        }
        String url = (
                "https://logistics.amazon.com/performance/api/v1/getData?dataSetId=da_dsp_daily_cdf_deep_dive&dsp=LVEL&from=" +
                        date +
                        "&program=AMZL&station=DCL5&timeFrame=Daily&to=" +
                        date
        );
        JsonArray httpData = Http.getJson(url).getAsJsonObject("tableData")
                .getAsJsonObject("da_dsp_daily_cdf_deep_dive")
                .getAsJsonArray("rows");
        if (httpData != null && !httpData.isEmpty()) {
            for (JsonElement element : httpData) {
                JsonObject o = element.getAsJsonObject();
                String id = null;
                if (o.has("transporter_id")) {
                    id = o.get("transporter_id").getAsString();
                }
                int driverFlag = 0;
                if (o.has("da_attributable_flag")) {
                    driverFlag = o.get("da_attributable_flag").getAsInt();
                }

                if (id != null && driverFlag > 0) {
                    JsonObject da = new JsonObject();
                    if (!cdf.getAsJsonObject("data").has(id)) {
                        cdf.getAsJsonObject("data").add(id, da);
                    }
                    da = cdf.getAsJsonObject("data").getAsJsonObject(id);
                    if (o.has("da_name") && !da.has("da_name")) {
                        da.addProperty("name", o.get("da_name").getAsString());
                    }
                    if (!da.has("tba-data")) {
                        da.add("tba-data", tbaObject());
                    }
                    if (o.has("negative_feedback_flag")) {
                        String tba = null;
                        if (o.has("tracking_id")) {
                            tba = o.get("tracking_id").getAsString();
                        }
                        if (tba != null) {
                            int flag = o.get("negative_feedback_flag").getAsInt();
                            JsonObject x = new JsonObject();
                            if (o.has("delivery_date")) {
                                x.addProperty("delivery-date", o.get("delivery_date").getAsString());
                            }
                            if (o.has("delivery_week")) {
                                x.addProperty("delivery-week", o.get("delivery_week").getAsString());
                            }
                            if (o.has("customer_id")) {
                                x.addProperty("customer-id", o.get("customer_id").getAsString());
                            }
                            if (o.has("delivery_id")) {
                                x.addProperty("delivery-id", o.get("delivery_id").getAsString());
                            }
                            if (flag == 0) {
                                if (o.has("respectful_of_property")) {
                                    int rop = o.get("respectful_of_property").getAsInt();
                                    if (rop > 0) {
                                        x.addProperty("respectful-of-property", rop);
                                    }
                                }
                                if (o.has("followed_instructions")) {
                                    int followed = o.get("followed_instructions").getAsInt();
                                    if (followed > 0) {
                                        x.addProperty("followed-instructions", followed);
                                    }
                                }
                                if (o.has("friendly")) {
                                    int friendly = o.get("friendly").getAsInt();
                                    if (friendly > 0) {
                                        x.addProperty("friendly", friendly);
                                    }
                                }
                                if (o.has("above_and_beyond")) {
                                    int aab = o.get("above_and_beyond").getAsInt();
                                    if (aab > 0) {
                                        x.addProperty("above-and-beyond", aab);
                                    }
                                }
                                if (o.has("delivered_with_care")) {
                                    int dwc = o.get("delivered_with_care").getAsInt();
                                    if (dwc > 0) {
                                        x.addProperty("delivered-with-care", dwc);
                                    }
                                }
                                if (o.has("thank_my_driver")) {
                                    int tmd = o.get("thank_my_driver").getAsInt();
                                    if (tmd>0) {
                                        x.addProperty("alexa-tmd", tmd);
                                    }
                                }

                                da.getAsJsonObject("tba-data")
                                        .getAsJsonObject("positive-feedback")
                                        .add(tba, x);
                            }
                            if (flag == 1) {
                                if (o.has("driver_mishandled_package")) {
                                    int mishandled = o.get("driver_mishandled_package").getAsInt();
                                    if (mishandled > 0) {
                                        x.addProperty("mishandled", mishandled);
                                    }


                                }
                                if (o.has("driver_was_unprofessional")) {
                                    int unprofessional = o.get("driver_was_unprofessional").getAsInt();
                                    if (unprofessional > 0) {
                                        x.addProperty("unprofessional", unprofessional);
                                    }

                                }
                                if (o.has("not_delivered_to_preferred_location")) {
                                    int notFollowed = o.get("not_delivered_to_preferred_location").getAsInt();
                                    if (notFollowed > 0) {
                                        x.addProperty("didnt-follow-instructions", notFollowed);
                                    }
                                }
                                if (o.has("delivered_to_wrong_address")) {
                                    int wrongAddress = o.get("delivered_to_wrong_address").getAsInt();
                                    if (wrongAddress > 0) {
                                        x.addProperty("wrong-address", wrongAddress);
                                    }
                                }
                                if (o.has("never_received_delivery")) {
                                    int dnr = o.get("never_received_delivery").getAsInt();
                                    if (dnr > 0) {
                                        x.addProperty("never-received-delivery", dnr);
                                    }
                                }

                                da.getAsJsonObject("tba-data")
                                        .getAsJsonObject("negative-feedback")
                                        .add(tba, x);
                            }
                        }
                    }
                }
            }
            cdf.addProperty("tbas-saved", LocalDate.now().toString());
            return true;
        }
        return false;
    }
    private static JsonObject tbaObject() {
        JsonObject x = new JsonObject();
        x.add("positive-feedback", new JsonObject());
        x.add("negative-feedback", new JsonObject());
        return x;
    }



}
