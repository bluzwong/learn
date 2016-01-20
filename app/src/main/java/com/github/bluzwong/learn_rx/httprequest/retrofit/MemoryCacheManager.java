package com.github.bluzwong.learn_rx.httprequest.retrofit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzhijie on 2016/1/20.
 */
public class MemoryCacheManager implements ICacheManager {
    private  final Map<String, CacheInfo> cacheInfoMap = new HashMap<>();

    public  void put(String key, long timeout) {
        if (key == null || key.equals("")) {
            return;
        }
        long now = System.currentTimeMillis();
        CacheInfo cacheInfo = new CacheInfo(key, now, timeout);
        cacheInfoMap.put(key, cacheInfo);
    }

    public boolean get(String key) {
        if (key == null || key.equals("")) {
            return false;
        }
        if (!cacheInfoMap.containsKey(key)) {
            return false;
        }
        CacheInfo cacheInfo = cacheInfoMap.get(key);
        if (cacheInfo == null) {
            return false;
        }
        long now = System.currentTimeMillis();

        if (now <= cacheInfo.getExpireTime()) {
            // ok
            return true;
        }
        return false;
    }

}
