package app.infonitesave.data;

import app.infonitesave.utils.FileSystem;
import app.infonitesave.utils.Http;
import app.infonitesave.utils.helpers.Date;
import app.infonitesave.utils.helpers.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

public class SuppReports {
    private static JsonArray urls;
    public static void saveSuppReports(LocalDate date) {
        File reports = FileSystem.getReportsFolder(date);

        File cdf = new File(reports, "cdf-report.pdf");
        File pod = new File(reports, "pod-report.pdf");

        if (!cdf.exists() || !pod.exists()) {
            urls = getUrls(date);
            if (!cdf.exists()) {
                saveFile(cdf, getSuppUrl("customer_feedback"));
            }
            if (!pod.exists()) {
                saveFile(pod, getSuppUrl("POD-Details"));
            }
        }

        File scorecard = new File(reports, "scorecard.pdf");
        if (!scorecard.exists()) {
            saveFile(scorecard, getScorecardUrl(date));
        }


    }
    private static JsonArray getUrls(LocalDate date) {
        int week = Date.week(date);
        String weekString = "W" + week;
        if (week < 10) {
            weekString = "W0" + week;
        }
        String dateString = date.getYear() + "-" + weekString;
        String httpUrl = (
            "https://logistics.amazon.com/performance/api/v1/getData?dataSetId=dsp_station_weekly_supp_reports&dsp=LVEL&from=" +
                    dateString +
                    "&station=DCL5&timeFrame=Weekly&to=" +
                    dateString
        );
        return Http.getJson(httpUrl).getAsJsonObject("tableData")
                .getAsJsonObject("dsp_station_weekly_supp_reports")
                .getAsJsonArray("rows");
    }
    private static void saveFile(File file, URL url) {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return;
        }
        if (url != null) {
            try (InputStream in = url.openStream()){
                Files.copy(in, Paths.get(file.getPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static URL getSuppUrl(String filter) {
        if (urls != null && !urls.isEmpty()) {
            for (JsonElement element : urls) {
                JsonObject o = element.getAsJsonObject();
                String name = o.get("name").getAsString();
                if (name.contains(filter) && o.has("downloadUrl")) {
                    try {
                        return new URL(o.get("downloadUrl").getAsString());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
        return null;
    }
    private static URL getScorecardUrl(LocalDate date) {
        int week = Date.week(date);
        String weekString = "W" + week;
        if (week < 10) {
            weekString = "W0" + week;
        }
        String dateString = date.getYear() + "-" + weekString;
        String url = (
                "https://logistics.amazon.com/performance/api/v1/getData?dataSetId=dsp_station_weekly_documents&dsp=LVEL&from=" +
                        dateString +
                        "&station=DCL5&timeFrame=Weekly&to=" +
                        dateString
        );
        JsonArray httpData = Http.getJson(url).getAsJsonObject("tableData")
                .getAsJsonObject("dsp_station_weekly_documents")
                .getAsJsonArray("rows");
        if (httpData != null && !httpData.isEmpty()) {
            for (JsonElement element : httpData) {
                JsonObject o = element.getAsJsonObject();
                String name = null;
                if (o.has("name")) {
                    name = o.get("name").getAsString();
                }
                if (name != null && name.contains("Scorecard") && o.has("downloadUrl")) {
                    try {
                        return new URL(o.get("downloadUrl").getAsString());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        }


        return null;
    }

}
