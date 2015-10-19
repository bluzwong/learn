package com.github.bluzwong.learn_rx;

import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangzhijie on 2015/10/15.
 */
public class RxLearning {
    public static void main(String[] args) {
        // 最简单的
        Observable.just("cccf")
                .subscribe(System.out::println);

        // range -> 从0到9的list
        // buffer -> 2：每2个一组 3：跳过第3个
        Observable.range(0, 10)
                .buffer(2, 3)
                .map(s -> "range + buffer : " + s)
                .subscribe(System.out::println);

        //? why
        Observable.interval(1, TimeUnit.SECONDS)
                .buffer(3, TimeUnit.SECONDS)
                .map(s -> "interval : " + s)
                .subscribe(System.out::println);

        ///? 不是很会用
        /*Observable.range(0, 10)
                .groupBy(i -> i % 2)
                .subscribe(g -> {
                    g.count().subscribe(integer -> {
                        String str = "key: " + g.getKey() + "contains: " + integer;
                        System.out.println(str);
                    });

                });*/
    }
}
