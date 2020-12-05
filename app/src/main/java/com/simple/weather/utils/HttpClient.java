package com.simple.weather.utils;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
public class HttpClient {
    public static final String WEATHER_TYPE_BASE = "base";
    public static final String WEATHER_TYPE_ALL = "all";
    private static Gson jsonConvert = new Gson();

    private static OkHttpClient okHttpClient = new OkHttpClient();

    private static final String SERVER_HOST = "http://restapi.amap.com/v3/weather/weatherInfo?";

    public static <T> void query(String adcode, String type, final Class<T> tClass
            , final IHttpCallback callback){
        String parameters = "key=aacc5437de9044399748ccd8eff2afe7"
                + "&city=" + adcode
                + "&extensions=" + type
                + "&output=JSON";

        Request request = new Request.Builder()
                .url(SERVER_HOST + parameters)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callback.onSuccess(null,false);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                callback.onSuccess(jsonConvert.fromJson(response.body().string(),tClass),
                        true);
            }
        });
    }

    public interface IHttpCallback {
        <T> void onSuccess(T result,boolean isSuccess);
    }
}
