package com.github.bluzwong.learn_rx;

import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by wangzhijie on 2015/10/15.
 */
public class RxLearning {
    public static void main(String[] args) {
       /* // 最简单的
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
*/
        ///? 不是很会用
        /*Observable.range(0, 10)
                .groupBy(i -> i % 2)
                .subscribe(g -> {
                    g.count().subscribe(integer -> {
                        String str = "key: " + g.getKey() + "contains: " + integer;
                        System.out.println(str);
                    });

                });*/

        // 跳过出错的数据
        Observable.just(1,2,0,3,4,5)
                //.map(i -> 100 / i)
                .flatMap(RxLearning::divide)
                .subscribe(System.out::println, Throwable::printStackTrace);
    }

    private static Observable<Integer> divide(int number) {
        return Observable.just(number)
                .map(i -> {
                    if (i == 4) {
                        throw new RuntimeException();
                    }
                    return 100 / i;
                })
                // 在这个observable出错时发射空
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof ArithmeticException) {
                        return Observable.empty();
                    }
                    return Observable.error(throwable);
                });
    }
}
