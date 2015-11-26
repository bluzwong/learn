package com.github.bluzwong.learn_rx.customviews;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by wangzhijie on 2015/11/26.
 */
public class ViewDragLinearLayout extends LinearLayout {
    public ViewDragLinearLayout(Context context) {
        this(context, null);
    }

    public ViewDragLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewDragLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ViewDragHelper helper;

    private View view1, view2;

    private void init() {
        helper = ViewDragHelper.create(this, 1, new DragCallback());
        helper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        view1 = getChildAt(0);
        view2 = getChildAt(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        helper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (helper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return helper.shouldInterceptTouchEvent(ev);
    }

    class DragCallback extends ViewDragHelper.Callback {

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.i("bruce-custom", "x方向加速度xvel => " + xvel);
            Log.i("bruce-custom", "y方向加速度vel => " + yvel);
            int childLeft = releasedChild.getLeft();
            int childTop = releasedChild.getTop();

            float xy = Math.abs(xvel) + Math.abs(yvel);

            int movX = (int) (200 * Math.min(Math.abs(xvel / 1000), 2.7f) * xvel / xy);
            int movY = (int) (200 * Math.min(Math.abs(yvel / 1000), 2.7f) * yvel / xy);

            int leftBound = getLeft();
            int rightBound = getWidth() - releasedChild.getWidth();
            int topBound = getTop();
            int bottomBound = getHeight() - releasedChild.getHeight();

            int moveToX = between(childLeft + movX, leftBound, rightBound);
            int moveToY = between(childTop + movY, topBound, bottomBound);
            if (helper.smoothSlideViewTo(releasedChild, moveToX, moveToY)) {
                ViewCompat.postInvalidateOnAnimation(ViewDragLinearLayout.this);
            }
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 是否要捕获子控件
            if (child == view1) {
                Log.i("bruce-custom", "view1 return false");
                return false;
            }
            Log.i("bruce-custom", "return true ");
            return true;
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            Log.i("bruce-custom", "capture view1" + edgeFlags);
            helper.captureChildView(view1, pointerId);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            // child 被拖动的子控件， top该空间将要被拖拽到的位置Y， dy速度
            int topBound = getPaddingTop();
            int bottomBound = getHeight() - child.getHeight();
            return between(top, topBound, bottomBound);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int leftBound = getPaddingLeft();
            int rightBound = getWidth() - child.getWidth();
            return between(left, leftBound, rightBound);
        }
    }

    private int between(int target, int min, int max) {
        if (min > max) {
            int tmp = min;
            min = max;
            max = tmp;
        }
        return Math.max(min, Math.min(target, max));
    }
}
