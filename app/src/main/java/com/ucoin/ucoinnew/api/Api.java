package com.ucoin.ucoinnew.api;

import android.content.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class Api {

    private static final int sTimeout = 30;

    protected static OkHttpClient sClient;
    protected static HashMap<String, String> sRequestUrls = new HashMap<String,String>(){{
        put("getItemList", "/getItemList");
        put("getTaskList", "/getTaskList");
        put("getCoinList", "/getCoinList");
        put("sendVerificationCode", "/auth/send");
        put("register", "/user/create");
        put("login", "/auth/login");
        put("getUserInfo", "/user/info");
        put("getUserCoinList", "/token/owned/list");
        put("getCoinProductList", "/token/product/list");
        put("uploadCoinLogo", "/qiniu/token/logo");
        put("createCoin", "/token/create");
        put("uploadCoinProductImages", "/qiniu/token/product");
        put("createCoinProduct", "/token/product/create");
        put("getCoinProductList", "/token/product/list");
    }};

    public static void request(String name, String method, JSONObject params, Boolean isForm, final Context context, final Callback cb) throws IOException, JSONException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(sTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(sTimeout, TimeUnit.SECONDS);
        sClient = builder.build();

        String url = Util.getProperty("APIRequestHost", context);
        if (name.equals("getItemList") || name.equals("getTaskList") || name.equals("getCoinList")) {
            url = Util.getProperty("APIRequestHostTest", context);
        }
        url = url + sRequestUrls.get(name);
        Logger.d(url);
        Logger.i(params.toString());
        Request.Builder request = new Request.Builder();
        String userToken = Util.getSP("userToken");
        if (userToken != null) {
            request.addHeader("Authorization", "Bearer " + userToken);
        }
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
                if (isForm) {
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                    Iterator iterator = params.keys();
                    while(iterator.hasNext()){
                        String key = (String) iterator.next();
                        String value = params.getString(key);
                        multipartBuilder.addFormDataPart(key, value);
                    }
                    body = multipartBuilder.setType(MultipartBody.FORM).build();
                } else {
                    if (params != null) {
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        body = RequestBody.create(JSON, params.toString());
                    }
                }

                request.url(url);
                request.post(body);
                break;
        }
        Call call = sClient.newCall(request.build());
        call.enqueue(cb);
    }
}
