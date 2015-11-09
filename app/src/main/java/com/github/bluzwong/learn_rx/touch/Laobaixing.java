package com.github.bluzwong.learn_rx.touch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by wangzhijie on 2015/11/9.
 */

public class Laobaixing extends TextView
{
    public Laobaixing(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        Log.d("bruce-touch", "【农民】 任务 <" + Util.actionToString(ev.getAction()) + "> dispatchTouchEvent : 需要分派，我下面没人了，怎么办？自己干吧");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        boolean bo = false;
        Log.d("bruce-touch", "【农民】任务 <" + Util.actionToString(ev.getAction()) + "> onTouchEvent : 自己动手，埋头苦干。能解决？" + bo);
        return bo;
    }
}