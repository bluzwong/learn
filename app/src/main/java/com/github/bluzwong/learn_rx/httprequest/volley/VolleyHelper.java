package com.github.bluzwong.learn_rx.httprequest.volley;

import android.content.Context;
import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.*;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangzhijie on 2015/10/21.
 */
public class VolleyHelper {
    private static VolleyHelper instance;
    private RequestQueue queue;
    private VolleyHelper(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public static void init(Context context) {
        if (instance != null) return;
        instance = new VolleyHelper(context);
    }

    public static VolleyHelper getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException("must call init before");
        }
        return instance;
    }

    public <T> T jsonRequest (String url, Map<String, String> params, Class<T> clz) throws ExecutionException, InterruptedException {
        String stringResponse = stringRequest(url, params);
        T fromJson = new Gson().fromJson(stringResponse, clz);

        Log.i("httprequest", "json object is : " + fromJson);
        return fromJson;
    }

    public String stringRequest(String url, Map<String, String> params) throws ExecutionException, InterruptedException {
        RequestFuture<String> future = RequestFuture.newFuture();
        String finalUrl = makeUrl(url, params);
        StringRequest request = new StringRequest(finalUrl, future, future);
        queue.add(request);
        String s = future.get();
        Log.i("httprequest", "json sync response from: " + finalUrl + "  response: \n " + s);
        return s;
    }

    public void stringRequestAsync(String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(makeUrl(url, params), listener, errorListener);
        queue.add(request);
    }

    public <T> void  jsonRequestAsync(String url, Map<String, String> params, Class<T> clz,  Response.Listener<T> listener, Response.ErrorListener errorListener) {
        queue.add(new StringRequest(makeUrl(url, params), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(new Gson().fromJson(response, clz));
            }
        }, errorListener));
    }

    private String makeUrl(String url, Map<String, String> params) {
        // 添加url参数
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb != null ? sb.toString() : "";
        }
        return url;
    }
}
