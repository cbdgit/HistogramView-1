package com.salmonzhg.histogramview_demo.views;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.salmonzhg.histogramview_demo.R;
import com.salmonzhg.histogramview_demo.utils.DisplayUtil;


/**
 * Created by Salmon on 2016/6/20 0020.
 */
public class ColumnView extends View {
    private static final int DEFAULT_COLOR = 0XFF3F51B5;
    private Paint mColumnPaint;
    private Paint mContainerPaint;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private int mColumnHeight = 0;
    private float mAlpha = 0f;
    private int maxHei;
    private int lineTop;
    private String mShowText = "";
    private float mRatio = 0f;
    private float mPercentage = 0f;
    private int mWid;
    private int mHei;

    public ColumnView(Context context) {
        super(context);

        init(context, null);
    }

    public ColumnView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mColumnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColumnPaint.setStyle(Paint.Style.FILL);

        mContainerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mContainerPaint.setStyle(Paint.Style.STROKE);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColumnPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(dp2px(12));

        int color = DEFAULT_COLOR;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColumnView);

            color = a.getColor(R.styleable.ColumnView_column_color, DEFAULT_COLOR);

            a.recycle();
        }

        setColumnColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widMode = MeasureSpec.getMode(widthMeasureSpec);
        int heiMode = MeasureSpec.getMode(heightMeasureSpec);

        int widSize = MeasureSpec.getSize(widthMeasureSpec);
        int heiSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widMode == MeasureSpec.AT_MOST) {
            widSize = dp2px(50);
        }

        setMeasuredDimension(widSize, heiSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxHei = (int) (h * 0.8);
        lineTop = (int) (h * 0.95);
        mWid = w;
        mHei = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine((float) (mWid/2 - 1),(float) (mHei - lineTop), (float) (mWid/2 +1), mHei - (mRatio * maxHei), mLinePaint);
        Rect bound = new Rect(mWid / 3, (int)(mHei - (mRatio * maxHei)), mWid * 2 / 3, mHei);
        canvas.drawRect(bound, mContainerPaint);
        canvas.drawRect(mWid / 3, mHei - mColumnHeight,
                mWid * 2 / 3, mHei, mColumnPaint);
//        Rect bounds = new Rect();
//        mTextPaint.getTextBounds(mShowText, 0, mShowText.length(), bounds);
//        canvas.drawText(mShowText, (mWid - bounds.width()) / 2, mHei - mColumnHeight - dp2px(8), mTextPaint);
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    public void setPercentage(float percentage) {
        mPercentage = percentage;
    }

    public void setShowText(String showText) {
        mShowText = showText;
    }

    public void startAnim() {
        mTextPaint.setAlpha(0);
        ValueAnimator lineAnim = ValueAnimator.ofFloat(0.0f, mRatio * mPercentage);
        lineAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mColumnHeight = (int) (value * maxHei);
                invalidate();
            }
        });
        lineAnim.setDuration(300);
//        lineAnim.setInterpolator(new BounceInterpolator());

        ValueAnimator alphaAnim = ValueAnimator.ofInt(0, 255);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int a = (int) animation.getAnimatedValue();
                mTextPaint.setAlpha(a);
                invalidate();
            }
        });
        alphaAnim.setDuration(300);

        AnimatorSet set = new AnimatorSet();
        set.play(lineAnim).before(alphaAnim);
        set.start();
    }

    protected int dp2px(int dpValue) {
        return DisplayUtil.dip2px(getContext(), dpValue);
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        mTextPaint.setAlpha(0);
    }

    public void setColumnColor(int color) {
        mColumnPaint.setColor(color);
        setTextColor(color);
    }

    public void setLineColor(int color) {
        mLinePaint.setColor(color);
    }

    public void setContainerColor(int color) {
        mContainerPaint.setColor(color);
    }

    public void setSelect(boolean isSelected) {
        mColumnPaint.setAlpha(isSelected ? 128 : 255);
        invalidate();
    }
}
