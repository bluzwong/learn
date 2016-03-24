package com.github.bluzwong.learn_rx;

import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RxLearning {

    public static void main(String[] args) {

        List<Integer> dataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dataList.add(new Random().nextInt(100));
        }


        for (int i = 0; i < dataList.size(); i++) {
            System.out.println(i + " => " + dataList.get(i));
        }

        // todo 将 dataList 中偶数及重复项过滤掉，并且按从小到大排序
        usingRxJava(dataList);
    }

    private static void usingRxJava(List<Integer> dataList) {
        Observable.from(dataList)
                .filter(integer -> integer % 2 == 0)
                .distinct()
                .toSortedList((i1, i2) -> i1 - i2)
                .subscribe(integers -> {
                    for (Integer integer : integers) {
                        System.out.println("after => " + integer);
                    }
                });
    }

}
