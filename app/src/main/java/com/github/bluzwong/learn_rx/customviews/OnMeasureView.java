package com.github.bluzwong.learn_rx.customviews;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by wangzhijie on 2015/11/19.
 */
public class OnMeasureView extends ViewGroup{
    public OnMeasureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static final String TAG = "bruce-onmeasure";

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        logModeAndSize(widthMeasureSpec);
        logModeAndSize(heightMeasureSpec);
        Log.d(TAG, "onMeasure: height" + getMeasuredHeight() + ";width:" + getMeasuredWidth());
    }

    private void logModeAndSize(int measureSpec) {
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                Log.d(TAG, "UNSPECIFIED: "+MeasureSpec.getSize(measureSpec));
                break;
            case MeasureSpec.AT_MOST:
                Log.d(TAG, "AT_MOST: "+MeasureSpec.getSize(measureSpec));
                break;
            case MeasureSpec.EXACTLY:
                Log.d(TAG, "EXACTLY: "+MeasureSpec.getSize(measureSpec));
                break;
        }
    }
}
