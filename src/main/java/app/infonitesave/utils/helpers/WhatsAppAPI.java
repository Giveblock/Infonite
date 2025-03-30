package app.infonitesave.utils.helpers;

import okhttp3.*;

import java.io.IOException;

public class WhatsAppAPI {
    /*
    curl -X 'POST' \
            'http://15.204.199.123:3000/api/sendText' \
            -H 'accept: application/json' \
            -H 'Content-Type: application/json' \
            -d '{
            "chatId": "14194100404@c.us",
            "text": "Hi there!",
            "session": "default"
     */

    public static void sendMsg(String message, String number) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("chatId", "1" + number + "@c.us")
                .add("text", message)
                .add("session", "default")
                .build();

        Request request = new Request.Builder()
                .url("http://15.204.199.123:3000/api/sendText")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            assert response.body() != null;
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static void sendMetrics(String message) {

    }


}
