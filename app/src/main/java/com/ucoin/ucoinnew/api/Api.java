package com.ucoin.ucoinnew.api;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class Api {

    protected static final OkHttpClient sClient = new OkHttpClient();
    protected static HashMap<String, String> sRequestUrls = new HashMap<String,String>(){{
        put("getItemList", "/getItemList");
        put("getTaskList", "/getTaskList");
        put("getCoinList", "/getCoinList");
        put("sendVerificationCode", "/auth/send");
    }};

    public static void request(String name, String method, JSONObject params, Context context, Callback cb) throws IOException, JSONException {
        String url = Util.getProperty("APIRequestHost", context);
        url = url + sRequestUrls.get(name);
        Logger.d(url);
        Logger.i(params.toString());
        Request.Builder request = new Request.Builder();
        switch (method) {
            case "GET":
                if (params != null) {
                    String ps = "";
                    Iterator iterator = params.keys();
                    while(iterator.hasNext()){
                        String key = (String) iterator.next();
                        String value = params.getString(key);
                        ps += key + "=" + value + "&";
                    }
                    url = url + "?" + ps;
                    url = url.substring(0, url.length() - 1);
                }
                request.url(url);
                request.get();
                break;
            case "POST":
                RequestBody body = null;
                if (params != null) {
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    body = RequestBody.create(JSON, params.toString());
                }
                request.url(url);
                request.post(body);
                break;
        }
        Call call = sClient.newCall(request.build());
        call.enqueue(cb);
    }
}
