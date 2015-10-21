package com.github.bluzwong.learn_rx;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.bluzwong.learn_rx.httprequest.*;
import com.github.bluzwong.learn_rx.httprequest.volley.VolleyHelper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VolleyHelper.init(this.getApplicationContext());
        goVolleyAsync();
        goVolleySync();
    }

    private void goVolleyAsync() {
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
                            instance.jsonRequestAsync("http://mt58866.xicp.net:66/getvalue.php", map, Result.class, new Response.Listener<Result>() {
                                // 第三层地狱
                                @Override
                                public void onResponse(Result response) {
                                    Log.i("httprequest", "async got result => " + response);
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
            Log.i("httprequest", "handle msg at main thread => " + msg.obj);
        }
    }

    private MyHandler handler = new MyHandler(this);

    private void goVolleySync() {
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
                    Result result = instance.jsonRequest("http://mt58866.xicp.net:66/getvalue.php", map, Result.class);
                    Message message = new Message();
                    message.obj = result;
                    handler.sendMessage(message);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
