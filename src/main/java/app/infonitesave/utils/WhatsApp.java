package app.infonitesave.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class WhatsApp {

    public static boolean checkWhatsApp(String number) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.ultramsg.com/instance64141/contacts/contact?token=vkx9lbzlhdurqfxs&chatId=1"
                        + number
                        + "@c.us")
                .get()
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            JsonObject o = JsonParser.parseString(data).getAsJsonObject();
            if (!o.get("id").getAsString().equalsIgnoreCase("")) {
                return true;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
    public static void sendMsg(String message, String number) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", "token")
                .add("to", "1" + number)
                .add("body", message)
                .build();

        Request request = new Request.Builder()
                .url("WhatsApp Instance Link")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public static void sendMetrics(String message) {
        //120363193782265040@g.us
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", "token")
                .add("to", "Instance Address")
                .add("body", message)
                .build();

        Request request = new Request.Builder()
                .url("WhatsApp Instance Link")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
