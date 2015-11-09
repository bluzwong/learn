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
public class Shizhang extends LinearLayout {

    public Shizhang(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 是否要通知子类这个event
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("bruce-touch", "【市长】 任务<" + Util.actionToString(ev.getAction()) + "> dispatchTouchEvent: 需要分派");
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否要自己在此处理，返回true 在此处理的话不会再发送到子view
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean bo = false;
        Log.d("bruce-touch", "【市长】 任务<" + Util.actionToString(ev.getAction()) + "> onInterceptTouchEvent: 拦截吗？" + bo);
        return bo;
    }

    /**
     * 自己是否处理完了？如果希望父类继续处理则返回false，反之true
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean bo = false;
        Log.d("bruce-touch", "【市长】任务<" + Util.actionToString(ev.getAction()) + "> onTouchEvent: 老百姓是个废物，下次再也不找你了，我自己来尝试一下。能解决？" + bo);
        return bo;
    }

}
