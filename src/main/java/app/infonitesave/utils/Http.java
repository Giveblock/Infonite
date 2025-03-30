package app.infonitesave.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class Http {
    private static final OkHttpClient client = new OkHttpClient();
    private static Request getRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", "session-id=143-6123881-7777508; session-id-time=2082787201l; ubid-main=131-3031902-7728561; sst-main=Sst1|PQGeEz0kTpi0aONBtpYVrW-BCSD1thBS837qnNRuJvQGC4IbI3wocLGA5p7YS7opM7X4w4R-EkU-rFV2soOQxJ7da-CQmEzHxMnXTbZhsiTQvjZ_PV7RYUazaxgcdH5KtrQusnAnNZvxeh4fbltR36CvquEzCXIHLDkILR0Wq4z2ktSd5FwEJqcVBZ-yFbyQSTct8SM0e0saqnXoBgOPskRCQBXGWzCO4htlcx10DQm5BeOcwzE-WqiFj1eq5B_Z9OcI0Mbn7uGSjTWpVrbvf_KDYjrVVAGO64IccLURu-t76xQ; x-amz-log-portal-locale=en-US; AMCV_4A8581745834114C0A495E2B%40AdobeOrg=-2121179033%7CMCIDTS%7C19635%7CMCMID%7C49028962881216360851393569552569903765%7CMCAID%7CNONE%7CMCOPTOUT-1696394802s%7CNONE%7CvVersion%7C5.3.0; mbox=PC#ef6c12fa7f7649539c4c8f194be6f9ff.35_0#1759632403|session#abfd134b4fb8412180ecadb3d693ef11#1696389463; AMCV_7742037254C95E840A4C98A6%40AdobeOrg=1585540135%7CMCIDTS%7C19744%7CMCMID%7C20267622172722085944841551275358958651%7CMCAID%7CNONE%7CMCOPTOUT-1705885904s%7CNONE%7CvVersion%7C4.4.0; aws-target-visitor-id=1698173577491-143141.44_0; aws-target-data=%7B%22support%22%3A%221%22%7D; regStatus=pre-register; session-token=j6Msqnyb3DuBU6GNIyI1+Yqyje8hIw0al510fEjFGWIO1uQ0F1su3+cemtKGIvWpQkzjwMkeA6stow2ZLJxTyuw+ap/zlJDWBXcdl2eHtDROpm+ga49squcQMVWjAK4hYOc3cNW23tjL91D3N19a+6PXzQZOVBQX8+WvLXSY5STOFmExdJPuiqJBvXmWyXzSEp1uaUYykqOm3N1EJgwoGMrZ1S8r/HCltGBITIwtnvTpA8zmdUh4JflfdhdN2GWcvMGbr3jfsyF9E9fnDlykhVzWmhV0MHFFtMcZkkJ03AnnPBcf5LSOWvlWMORMiwOmSb7LJdZGQlckjFTKoMaW6ZJ3Zx+7Qu2HZKtAwn7PmGo; x-main=\"pcjFNeUzCX4muvyAQFiQyhxqVzDJrr@RDZAgHf1MmdhxM3WWvwIGoU?se21Ox4Oc\"; at-main=Atza|IwEBIEHEO56YLZODSzbiykYqF2RxnIL9T_CdRgDi9CAGXK7-daV9qpRB_6IipqOZlJceLRouKMUmtgHnoCfuIhWqTQV76lrbtnEYcFq7Qt8hYCBt1fZ6nS9P5Dtdw6Rciw_upH-Jj4pDu1ftNjfQRTr3OGSJBlMXnz8u3YlrmTweFbPX3hEnengSWcSsO2HjdkDs0RfgzzwsrOixtcQsJoJW-04OR5qpq4hOn2W7wgTUx4bEZQ; sess-at-main=\"CiG5q9tbIAJhrQHOMFFHP36RvuYTBHHQPJbGXUFshXA=\"; lc-main=en_US; ak_bmsc=B61BED600BD9828678604B05656C2B72~000000000000000000000000000000~YAAQE3s1FwJ+UIyNAQAA6floqRaXLAHOzUQgMUk/cc8i0p4y45gpRiZqrsk+hzcXCXJNaIJ5DES6/hKavEGIpnS6zFc9nJfmlCfCnGz2evDjeNRkC9nFh/uG3vfLVre1QWV/GXCMFrGai6wga6N4hD2qDmgIe/GjfNcLLhLMU/VcABmqTTqiheAejEYwJSNMMoIo475gGd2ZFhJq32lVav2iX9oiCoEV0I6n3O7s/BXrYJi4k3j5rzsY6CwTzPjmrUQkJoYlh9wDR1Kb/t1hfMWQChBruEPluux+/FOrdBRah6E5Jhe0neCPusk6Mky2ogq8RHqCF38IdT+yAi4SISvaihh3NxXxWjJunvuplrXssF5I3XzzgetDxuiUkGKBeRByiSe6MnRqapO8; bm_sv=06F2F9F398CB01C3B9B4A8D36D6F630F~YAAQE3s1F3fuUIyNAQAAXa9rqRYTONHMgN/fWCnOcHeLcNW1j5iGX76ZpuXNo4jIIZYJx3/3Inf3Lvd6HNUHrwB/9xBGvsoD2DGSrMFjUdWfMsGXnTm7qdsx/kAA79s9p9yW0esHReID//nyPgqIBthkDfDf0+t8hIMw/g3cow6kfURt7KdoSevx8mHvJmMywHSxa0hfeJv8x7osvwzZ6lTnt8FgYfusSV91KcRdso3miI4bZko1kWqiGhEZblMMwA==~1")
                .addHeader("Host", "logistics.amazon.com")
                .addHeader("Referer", "https://logistics.amazon.com")
                .addHeader("TE", "trailers")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/118.0")
                .build();
    }
    @Nullable
    private static JsonObject getJsonObject(Request request) {
        Response response;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("Http Connection Successful");
            }
            if (!response.isSuccessful()) {
                System.out.println("Http Connection Failed");
            }
            if (response.body() != null) {
                String data;
                data = response.body().string().replaceAll("\\\\", "");
                data = data.replaceAll("\"\\{", "{");
                data = data.replaceAll("}\"", "}");
                data = data.replaceAll("\"\"\\[", "[");
                data = data.replaceAll("]\"\"", "]");
                return JsonParser.parseString(data).getAsJsonObject();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static JsonObject getJson(String url) {
        Request request = getRequest(url);

        return getJsonObject(request);
    }

}
