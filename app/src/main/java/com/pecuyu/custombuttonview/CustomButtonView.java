package com.pecuyu.custombuttonview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by pecuyu on 2017/8/19.
 */

public class CustomButtonView extends View {
    private Paint mPaint;
    private GestureDetector gestureDetector;

    private int TYPE_START = 0;
    private int TYPE_STOP = 1;
    private int curType = TYPE_STOP;
    private float fraction = 0.0f;
    private float radius;
    private String msgText = "";
    private String btnText = "点击开始录屏";
    private boolean isInCycle = false;

    public CustomButtonView(Context context) {
        this(context, null);
    }

    public CustomButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        CustomButtonViewGestureListener listener = new CustomButtonViewGestureListener();
        gestureDetector = new GestureDetector(context, listener);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        radius = getRadius();
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = 200;
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = 200;
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制圆形
        mPaint.setColor(color);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);

        // 绘制文本
        drawCenterText(msgText, canvas, mPaint, 50, getHeight() / 8);
        drawCenterText(btnText, canvas, mPaint, 80, 0);

    }

    /**
     * 在中间绘制文字
     *
     * @param msgText
     * @param canvas
     * @param paint
     * @param textSize
     * @param offsetY
     */
    private void drawCenterText(String msgText, Canvas canvas, Paint paint, int textSize, int offsetY) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        //获取paint中的字体信息 ， setTextSize方法要在他前面
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        // 计算文字高度baseline
        float textBaseY = getHeight() / 2 + (fontHeight / 2 - fontMetrics.bottom);
        //获取字体的长度
        float fontWidth = paint.measureText(msgText);
        //计算文字长度的baseline
        float textBaseX = (getWidth() - fontWidth) / 2;
        canvas.drawText(msgText, textBaseX, textBaseY + offsetY, paint);
    }


    /**
     * 获取圆得半径
     * @return
     */
    private int getRadius() {
        return Math.min(getWidth() / 2, getHeight() / 2);
    }

    private int color = getResources().getColor(R.color.color_start);

    /**
     * 点击开始时的颜色变化动画效果
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onStartColorAnimation() {
        ValueAnimator animator = ValueAnimator.ofArgb(getResources().getColor(R.color.color_start), getResources().getColor(R.color.color_stop));
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                color = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();

    }

    /**
     * 点击结束时的颜色变化动画效果
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onStopColorAnimation() {
        ValueAnimator animator = ValueAnimator.ofArgb(getResources().getColor(R.color.color_stop), getResources().getColor(R.color.color_start));
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                color = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                    startButtonViewAnimatedBreathCycle();
            }

        });
        animator.start();
    }


    class CustomButtonViewGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;  // 一定要返回true
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (curType == TYPE_STOP) {
                Log.e("TAG", "onSingleTapUp");
                cancelButtonViewBreathAnimatedCycle();
                curType = TYPE_START;
                onStartColorAnimation();
                zoomInOrOutAnimation(true);
                if (listener != null) listener.onStart();
            } else if (curType == TYPE_START) {
                curType = TYPE_STOP;
                zoomInOrOutAnimation(false);
                onStopColorAnimation();
                if (listener != null) listener.onStop();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consume = gestureDetector.onTouchEvent(event);
        return consume;
    }

    /**
     * 缩放
     *
     * @param in
     */
    public void zoomInOrOutAnimation(final boolean in) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (in) {
                    fraction = animation.getAnimatedFraction();
                    radius = getRadius() * (1 + fraction);
                } else {
                    fraction = -animation.getAnimatedFraction();
                    radius = getRadius() * (2 + fraction);
                }
            }
        });
        animator.setDuration(1500);
        animator.start();
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
        postInvalidate();
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
        postInvalidate();
    }

    /**
     * 状态改变监听
     */
    public interface OnStateChangeListener {
        /**
         * 开始时调用
         */
        void onStart();

        /**
         * 结束时调用
         */
        void onStop();
    }

    private OnStateChangeListener listener;

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.listener = listener;
    }

    ValueAnimator cycleAnimator;

    /**
     * 开始按钮呼吸动画效果
     */
    private void startButtonViewAnimatedBreathCycle() {
        if (curType==TYPE_START) {
            return;
        }
        cycleAnimator = ValueAnimator.ofFloat(1, 0);
        cycleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        cycleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        cycleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (curType == TYPE_START) cycleAnimator.cancel();
                fraction = 1.0f - animation.getAnimatedFraction() * 0.2f;
                radius = getRadius() * fraction;
                invalidate();
            }
        });
        cycleAnimator.setDuration(1500);
        cycleAnimator.start();
    }

    /**
     * 取消按钮呼吸效果
     */
    private void cancelButtonViewBreathAnimatedCycle() {
        if (cycleAnimator != null && cycleAnimator.isRunning()) {
            cycleAnimator.cancel();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {    // view可见时开启呼吸效果
            startButtonViewAnimatedBreathCycle();
        } else {
            cancelButtonViewBreathAnimatedCycle();
        }
    }
}
