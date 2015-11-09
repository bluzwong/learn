package com.github.bluzwong.learn_rx.touch;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by wangzhijie on 2015/11/9.
 *
 */
public class Shengzhang extends LinearLayout {

    public Shengzhang(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("bruce-touch", "【省长】任务<" + Util.actionToString(ev.getAction()) + "> dispatchTouchEvent: 需要分派");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean bo = false;
        Log.d("bruce-touch", "【省长】任务<" + Util.actionToString(ev.getAction()) + "> onInterceptTouchEvent : 拦截吗？" + bo);
        return bo;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean bo = false;
        Log.d("bruce-touch", "【省长 任务<" + Util.actionToString(ev.getAction()) + "> onTouchEvent: 市长是个废物，下次再也不找你了，我自己来尝试一下。能解决？" + bo);
        return bo;
    }

}
