package com.github.bluzwong.learn_rx;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.bluzwong.learn_rx.httprequest.*;
import com.github.bluzwong.learn_rx.httprequest.retrofit.MemoryCacheManager;
import com.github.bluzwong.learn_rx.httprequest.retrofit.WebApi;
import com.github.bluzwong.learn_rx.httprequest.volley.RxVolleyHelper;
import com.github.bluzwong.learn_rx.httprequest.volley.VolleyHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    WebApi api;
    MemoryCacheManager memCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VolleyHelper.init(this.getApplicationContext());
        RxVolleyHelper.init(this.getApplicationContext());

        /*goVolleySync();
        goVolleyAsync();

        goRxVolley();
        goRxVolleyWithLambda();*/


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });
        api = new WebApi();
        memCache = new MemoryCacheManager();
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRetrofit();
            }
        });
    }

    private <T> Observable<T> memoryCacheWrapper(Observable<T> originOb, String key) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                Object obj = memCache.get(key);
                if (obj != null) {
                    subscriber.onNext((T) obj);
                    return;
                }
                subscriber.onCompleted();
            }
        }).concatWith(originOb.map(t -> {
            memCache.put(key, 10_000, t);
            return t;
        }));
    }

    private void goRetrofit() {
        Timer timer = new Timer();
        timer.setStartTime();
        memoryCacheWrapper(api.myService.getUrls(), "0")
                .flatMap(valueIndex -> api.myService.getValue1(valueIndex.getUrl_value1())
                        .zipWith(api.myService.getValue2(valueIndex.getUrl_value2()),
                                Values::new))
                .flatMap(values -> api.myService.getResult(URLS.RESULT_INDEX, values.value1.getValue1(), values.value2.getValue2()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    timer.printUsingTime("bruce-re");
                    Log.i("bruce-re", "result  => " + result.getResult());
                });
    }

    static class Timer {
        private long startTime;

        void setStartTime() {
            startTime = System.currentTimeMillis();
        }

        long getUsingTime() {
            return System.currentTimeMillis() - startTime;
        }

        void printUsingTime(String owner) {
            Log.i("httprequest", owner + " using time => " + getUsingTime() + " ms");
        }
    }

    static class Values {
        Value1 value1;
        Value2 value2;

        public Values(Value1 value1, Value2 value2) {
            this.value1 = value1;
            this.value2 = value2;
        }
    }

    private void goRxVolley() {
        Timer timer = new Timer();
        timer.setStartTime();
        RxVolleyHelper instance = RxVolleyHelper.getInstance();
        instance.jsonRequest(URLS.VALUE_INDEX, null, ValueIndex.class)
                .flatMap(new Func1<ValueIndex, Observable<Values>>() {
                    @Override
                    public Observable<Values> call(ValueIndex valueIndex) {
                        return instance.jsonRequest(valueIndex.getUrl_value1(), null, Value1.class)
                                .zipWith(instance.jsonRequest(valueIndex.getUrl_value2(), null, Value2.class),
                                        new Func2<Value1, Value2, Values>() {
                                            @Override
                                            public Values call(Value1 value1, Value2 value2) {
                                                return new Values(value1, value2);
                                            }
                                        });
                    }
                })
                .map(new Func1<Values, Map<String, String>>() {
                    @Override
                    public Map<String, String> call(Values values) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("value1", values.value1.getValue1());
                        map.put("value2", values.value2.getValue2());
                        return map;
                    }
                })
                .flatMap(new Func1<Map<String, String>, Observable<Result>>() {
                    @Override
                    public Observable<Result> call(Map<String, String> map) {
                        return instance.jsonRequest(URLS.RESULT_INDEX, map, Result.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result>() {
                    @Override
                    public void call(Result result) {
                        Log.i("httprequest", "rx result => " + result);
                        timer.printUsingTime("goRxVolley ");
                    }
                });
    }


    private void goRxVolleyWithLambda() {
        Timer timer = new Timer();
        timer.setStartTime();
        RxVolleyHelper instance = RxVolleyHelper.getInstance();
        instance.jsonRequest(URLS.VALUE_INDEX, null, ValueIndex.class)
                .flatMap(valueIndex -> instance.jsonRequest(valueIndex.getUrl_value1(), null, Value1.class)
                        .zipWith(instance.jsonRequest(valueIndex.getUrl_value2(), null, Value2.class), Values::new))
                .map(values -> {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("value1", values.value1.getValue1());
                    map.put("value2", values.value2.getValue2());
                    return map;
                })
                .flatMap(map -> instance.jsonRequest(URLS.RESULT_INDEX, map, Result.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Log.i("httprequest", "rx with lambda result => " + result);
                    timer.printUsingTime("goRxVolleyWithLambda ");
                });
    }

    private void goVolleyAsync() {
        Timer timer = new Timer();
        timer.setStartTime();
        VolleyHelper instance = VolleyHelper.getInstance();
        instance.jsonRequestAsync(URLS.VALUE_INDEX, null, ValueIndex.class, new Response.Listener<ValueIndex>() {
            // 第一层地狱
            @Override
            public void onResponse(ValueIndex response) {
                Log.i("httprequest", "async result => " + response);
                final CountDownLatch latch = new CountDownLatch(2);
                final Value1[] value1 = new Value1[1];
                final Value2[] value2 = new Value2[1];
                new Thread(new Runnable() {
                    // 第二层地狱
                    @Override
                    public void run() {
                        try {
                            latch.await();
                            HashMap<String, String> map = new HashMap<>();
                            map.put("value1", value1[0].getValue1());
                            map.put("value2", value2[0].getValue2());
                            instance.jsonRequestAsync(URLS.RESULT_INDEX, map, Result.class, new Response.Listener<Result>() {
                                // 第三层地狱
                                @Override
                                public void onResponse(Result response) {
                                    Log.i("httprequest", "async got result => " + response);
                                    timer.printUsingTime("goVolleyAsync ");

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                instance.jsonRequestAsync(response.getUrl_value1(), null, Value1.class, new Response.Listener<Value1>() {
                    // 第二层地狱
                    @Override
                    public void onResponse(Value1 response) {
                        Log.i("httprequest", "async got value1 => " + response);
                        value1[0] = response;
                        latch.countDown();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                instance.jsonRequestAsync(response.getUrl_value2(), null, Value2.class, new Response.Listener<Value2>() {
                    // 第二层地狱
                    @Override
                    public void onResponse(Value2 response) {
                        Log.i("httprequest", "async got value2 => " + response);
                        value2[0] = response;
                        latch.countDown();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    static class MyHandler extends Handler {
        WeakReference<Activity> activityWeakReference;

        public MyHandler(Activity activity) {
            activityWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // do with activity
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                Log.i("httprequest", "handle msg at main thread => " + msg.obj);
            }
        }
    }

    private MyHandler handler = new MyHandler(this);

    private void goVolleySync() {
        Timer timer = new Timer();
        timer.setStartTime();
        VolleyHelper instance = VolleyHelper.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ValueIndex index = instance.jsonRequest(URLS.VALUE_INDEX, null, ValueIndex.class);
                    Value1 value1 = instance.jsonRequest(index.getUrl_value1(), null, Value1.class);
                    Value2 value2 = instance.jsonRequest(index.getUrl_value2(), null, Value2.class);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("value1", value1.getValue1());
                    map.put("value2", value2.getValue2());
//                    Result result = instance.jsonRequest(index.getUrl_result(), map, Result.class);
                    Result result = instance.jsonRequest(URLS.RESULT_INDEX, map, Result.class);
                    Message message = new Message();
                    message.obj = result;
                    handler.sendMessage(message);
                    timer.printUsingTime("goVolleySync ");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
