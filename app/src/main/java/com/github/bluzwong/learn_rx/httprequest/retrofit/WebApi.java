package com.github.bluzwong.learn_rx.httprequest.retrofit;

import android.util.Log;
import com.github.bluzwong.learn_rx.BaseApplication;
import com.github.bluzwong.learn_rx.httprequest.*;
import okhttp3.*;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

import java.io.File;
import java.io.IOException;

/**
 * Created by wangzhijie on 2016/1/19.
 */
public class WebApi {

    public interface MyService {
        @GET("/valueindex.php")
        Observable<ValueIndex> getUrls();

        @GET
        Observable<Value1> getValue1(@Url String url);

        @GET
        Observable<Value2> getValue2(@Url String url);

        @GET
        Observable<Result> getResult(@Url String url, @Query("value1") String value1, @Query("value2") String value2);
    }
    OkHttpClient client;

    {
        File httpCacheDirectory = new File(BaseApplication.getContext().getExternalCacheDir(), "responses");
        ICacheManager memoryCacheManager= new DiskCacheManager(BaseApplication.getContext());

        Interceptor interceptor = chain -> {
            Request originRequest = chain.request();
            Request request = originRequest;
            String urlString = request.url().url().toString();
            Log.i("bruce-re", "url => " + urlString);

            boolean hasCached = memoryCacheManager.get(urlString);
            if (hasCached) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }

            Response response = chain.proceed(request);
            // try load from cache
            if (hasCached && response.code() == 504) {
                // cache not exists, reload from origin
                response = chain.proceed(originRequest);
            }

            if (!hasCached) {
                memoryCacheManager.put(urlString, 10_000);
            }
            return response;
        };
        client = new OkHttpClient.Builder()
                .cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024))
                .addInterceptor(interceptor)
                .build();
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URLS.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    public MyService myService = retrofit.create(MyService.class);
}
