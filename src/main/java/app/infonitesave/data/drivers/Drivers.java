package app.infonitesave.data.drivers;

import app.infonitesave.data.URL;
import app.infonitesave.utils.Http;
import app.infonitesave.utils.WhatsApp;
import app.infonitesave.utils.helpers.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class Drivers {

    public static JsonObject driverData() {

        return Json.getFromFile(getOrCreateFile()).getAsJsonObject("data");

    }

    private static File getOrCreateFile() {
        File drivers = new File("Archive/Drivers/Drivers.json");
        if (!drivers.exists()) {
            try {
               if (drivers.createNewFile()) {
                   JsonObject o = new JsonObject();
                   o.addProperty("last-updated", "");
                   o.add("data", new JsonObject());

                   FileWriter writer = new FileWriter(drivers);
                   writer.write(o.toString());
                   writer.close();
               }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        return drivers;
    }
    public static void updateDrivers() {
        File driverFile = getOrCreateFile();
        JsonObject drivers = Json.getFromFile(driverFile);
        if (drivers.get("last-updated").getAsString().equalsIgnoreCase(LocalDate.now().toString())) {
            return;
        }
        JsonArray httpDrivers = Http.getJson(URL.drivers())
                .getAsJsonObject("tableData")
                .getAsJsonObject("dsp-associates-table-data")
                .getAsJsonArray("rows");

        sortDrivers(drivers.getAsJsonObject("data"), httpDrivers);
        updateWhatsApp(drivers.getAsJsonObject("data"));

        drivers.remove("last-updated");
        drivers.addProperty("last-updated", LocalDate.now().toString());

        try {
            FileWriter writer = new FileWriter(driverFile);
            writer.write(drivers.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            DriverSheet.createSheet(drivers.getAsJsonObject("data"));
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }


    }
    private static void sortDrivers(JsonObject data, JsonArray httpData) {
        for (JsonElement element : httpData) {
            JsonObject o = element.getAsJsonObject();
            String id = o.get("transporter_id").getAsString();
            String status = o.get("operational_status").getAsString();
            if (status.equalsIgnoreCase("ACTIVE") && !data.has(id)) {
                JsonObject x = new JsonObject();
                x.addProperty("full-name", o.get("full_name").getAsString());
                x.addProperty("nick-name", "");
                String phone = o.get("personal_phone_number").getAsString();
                x.addProperty("phone-number", phone);
                x.addProperty("whatsapp", WhatsApp.checkWhatsApp(phone));
                x.addProperty("email-address", o.get("email_address").getAsString());
                x.addProperty("qualifications", o.get("qualifications").getAsString());
                data.add(id, x);
            }
            if (status.equalsIgnoreCase("INACTIVE") && data.has(id)) {
                data.remove(id);
            }
        }
        ;
    }
    private static void updateWhatsApp(JsonObject data) {
        for (String key : data.keySet()) {
            JsonObject o = data.getAsJsonObject(key);
            if (!o.get("whatsapp").getAsBoolean()) {
                o.remove("whatsapp");
                o.addProperty("whatsapp", WhatsApp.checkWhatsApp(o.get("phone-number").getAsString()));
            }
        }
    }

    public static boolean hasWhatsApp(String id) {
        if (driverData().has(id)) {
            JsonObject o = driverData().getAsJsonObject(id);
            if (o.has("whatsapp")) {
                return o.get("whatsapp").getAsBoolean();
            }
        }
        return false;
    }
    public static String getNumber(String id) {
        JsonObject o = driverData().getAsJsonObject(id);
        if (o.has("phone-number")) {
            String number = o.get("phone-number").getAsString();
            if (!number.equalsIgnoreCase("")) {
                return number;
            }
        }
        return null;
    }
}
