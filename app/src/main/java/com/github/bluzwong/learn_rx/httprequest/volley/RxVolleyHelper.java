package com.github.bluzwong.learn_rx.httprequest.volley;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by wangzhijie on 2015/10/21.
 */
public class RxVolleyHelper {
    private static RxVolleyHelper instance;
    private VolleyHelper volleyHelper;

    private RxVolleyHelper(Context context) {
        VolleyHelper.init(context);
        volleyHelper = VolleyHelper.getInstance();
    }

    public static void init(Context context) {
        if (instance != null) return;
        instance = new RxVolleyHelper(context);
    }

    public static RxVolleyHelper getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException("must call init before");
        }
        return instance;
    }

    public <T> Observable<T> jsonRequest (String url, Map<String, String> params, Class<T> clz) {
        Observable<T> defer = Observable.defer(new Func0<Observable<T>>() {
            @Override
            public Observable<T> call() {
                try {
                    return Observable.just(volleyHelper.stringRequest(url, params))
                            .map(new Func1<String, T>() {
                                @Override
                                public T call(String s) {
                                    T fromJson = new Gson().fromJson(s, clz);
                                    Log.i("httprequest", "json object is : " + fromJson);
                                    return fromJson;
                                }
                            });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return Observable.empty();
            }
        });

        return defer;
    }
}
