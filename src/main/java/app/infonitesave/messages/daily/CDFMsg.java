package app.infonitesave.messages.daily;

import app.infonitesave.data.drivers.Drivers;
import app.infonitesave.utils.FileSystem;
import app.infonitesave.utils.helpers.Json;
import app.infonitesave.utils.helpers.WhatsAppAPI;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class CDFMsg {

    public static void sendMsg(LocalDate date) {
        File cdfFile = FileSystem.qualityJsonFile("CDF.json", date);
        JsonObject cdf = Json.getFromFile(cdfFile);
        boolean driverMsg = sendDrivers(date, cdf);
        if (driverMsg) {
            try {
                FileWriter writer = new FileWriter(cdfFile);
                writer.write(cdf.toString());
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private static boolean sendDrivers(LocalDate date, JsonObject cdf) {
        if (cdf.has("driver-message") || !cdf.has("drivers-saved")) {
            return false;
        }
        for (String id : cdf.getAsJsonObject("data").keySet() ) {
            if (Drivers.hasWhatsApp(id) && Drivers.getNumber(id) != null) {
                JsonObject o = cdf.getAsJsonObject("data").getAsJsonObject(id);
                int pos = o.get("positive-feedback").getAsInt();
                int neg = o.get("negative-feedback").getAsInt();
                StringBuilder b = new StringBuilder();
                b.append("Customer Feedback - " + date.getMonthValue() + "/" + date.getDayOfMonth());
                b.append("\n");
                b.append("\nPositive Feedback:");
                int respectful = 0;
                int followed = 0;
                int friendly = 0;
                int above = 0;
                int delCare = 0;
                if (pos > 0) {
                    JsonObject p = o.getAsJsonObject("positive-details");
                    respectful = p.get("respectful-of-property").getAsInt();
                    followed = p.get("followed-instructions").getAsInt();
                    friendly = p.get("friendly").getAsInt();
                    above = p.get("above-and-beyond").getAsInt();
                    delCare = p.get("delivered-with-care").getAsInt();
                }
                b.append("\n\t- Respectful of Property: " + respectful);
                b.append("\n\t- Followed Notes: " + followed);
                b.append("\n\t- Friendly: " + friendly);
                b.append("\n\t- Above and Beyond: " + above);
                b.append("\n\t- Delivered With Care: " + delCare);

                b.append("\n");
                b.append("\n\tNegative Feedback:");
                int mishandle = 0;
                int unprof = 0;
                int noFollow = 0;
                int wrongAddress = 0;
                int dnr = 0;
                if (neg > 0) {
                    JsonObject n = o.getAsJsonObject("negative-details");
                    mishandle = n.get("mishandled-package").getAsInt();
                    unprof = n.get("unprofessional").getAsInt();
                    noFollow = n.get("didnt-follow-instructions").getAsInt();
                    wrongAddress = n.get("wrong-address").getAsInt();
                    dnr = n.get("never-received-delivery").getAsInt();
                }
                b.append("\n\t- Mishandled Package: " + mishandle);
                b.append("\n\t- Unprofessional: " + unprof);
                b.append("\n\t- Didn't Follow Notes: " + noFollow);
                b.append("\n\t- Wrong Address: " + wrongAddress);
                b.append("\n\t- Never Received: " + dnr);

                b.append("\n");
                b.append("\nEverytime a customer leaves feedback it can fall under more than one category."
                        + "If a customer leaves feedback on more than one package it will still only count as one towards your total.");

                WhatsAppAPI.sendMsg(b.toString(), Drivers.getNumber(id));
            }
        }
        cdf.addProperty("driver-message", true);
        return true;
    }



}
