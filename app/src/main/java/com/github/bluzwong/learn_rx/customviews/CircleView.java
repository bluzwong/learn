package com.github.bluzwong.learn_rx.customviews;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangzhijie on 2015/11/19.
 */
public class CircleView extends View {
    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        TypeEvaluator<Float> accCore = new TypeEvaluator<Float>() {
            @Override
            public Float evaluate(float fraction, Float startValue, Float endValue) {
                return fraction * (endValue - startValue);
            }
        };
        ValueAnimator animator = ValueAnimator.ofObject(accCore, 0f, 360f);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ratio = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        bigCirclePaint = new Paint();
        bigCirclePaint.setColor(bigCircleColor);
        bigCirclePaint.setStrokeWidth(bigCircleStrokeWidth);
        bigCirclePaint.setStyle(Paint.Style.STROKE);

        smallBallPaint = new Paint();
        smallBallPaint.setColor(smallBallColor);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    private Paint bigCirclePaint = null;
    private int bigCircleColor = Color.BLACK;
    private float bigCircleStrokeWidth = 5;
    private float bigCircleRadius = 200;

    private Paint smallBallPaint = null;
    private int smallBallColor = Color.RED;
    private float smallBarRadius = 15;
    private float ratio = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        drawBigCircle(canvas);
        drawSmallBall(canvas);
    }

    private void drawBigCircle(Canvas canvas) {
        canvas.save();
        canvas.drawCircle(300, 300, bigCircleRadius, bigCirclePaint);
        canvas.restore();
    }

    private void drawSmallBall(Canvas canvas) {
        double sweepAngle =  (Math.PI / 180 * 270) + Math.PI/180 * ratio;
        float y = (float) Math.sin(sweepAngle) * bigCircleRadius;
        float x = (float) Math.cos(sweepAngle) * bigCircleRadius;
        int restoreCount = canvas.save();
        canvas.translate(300, 300);
        canvas.drawCircle(x, y, smallBarRadius, smallBallPaint);
        canvas.restoreToCount(restoreCount);
    }
}
