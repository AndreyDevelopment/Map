package sadev.map;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Reader;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Andrey Shloma on 08.06.2016.
 */
public class Requester {

    private static final Gson GSON = new Gson();
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    private static final String ERROR = "error";

    @Nullable
    public static Weather getWater(@NonNull String address) throws IOException {
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" +
                URLEncoder.encode(address, "utf-8") +
                "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        Reader charStream = get(url);

        JsonElement element = GSON.fromJson(charStream, JsonElement.class);
        JsonObject jsonObject = element.getAsJsonObject();
        if (jsonObject.has(ERROR)) {
            return null;
        } else {
            Weather weather = GSON.fromJson(element, Weather.class);
            return weather;
        }
    }

    private static Reader get(@NonNull String url) throws IOException {
        Log.d("URL:", url);
        Request request = new Request.Builder().url(url).build();
        Response response = mOkHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        return responseBody.charStream();
    }
}
