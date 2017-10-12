package com.salmonzhg.histogramview_demo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.salmonzhg.histogramview_demo.R;
import com.salmonzhg.histogramview_demo.utils.DisplayUtil;

import java.util.Arrays;

/**
 * Created by Salmon on 2016/6/20 0020.
 */
public class HistogramView extends HorizontalScrollView {
    private static final int DEFAULT_COLUMN_PER_SCREEN = 7;
    private static final String[] DEFAULT_DATE_TEXT = new String[]{"一", "二", "三", "四", "五", "六", "日"};
    private static final int DEFAULT_COLOR = 0XFF3F51B5;
    private static final int DEFAULT_TEXT_SIZE = 14;
    private static final int PLAY = 0;
    private String[] mDefaultDateText = DEFAULT_DATE_TEXT;
    private int mColumnPerScreen = DEFAULT_COLUMN_PER_SCREEN;
    private int mColumnWid = 0;
    private int mBottomTextColor = DEFAULT_COLOR;
    private int mHeaderTextColor = DEFAULT_COLOR;
    private int mPressedHeaderTextColor = DEFAULT_COLOR;
    private int mHistogramColor = DEFAULT_COLOR;
    private int mTabColor = DEFAULT_COLOR;
    private int mHeaderContentDividerColor = DEFAULT_COLOR;
    private int mHeaderHeaderDividerColor = DEFAULT_COLOR;
    private int mContainerColor = DEFAULT_COLOR;
    private int mBottomTextSize = DEFAULT_TEXT_SIZE;
    private int mHeaderTextSize = DEFAULT_TEXT_SIZE;
    private LinearLayout llHistogram;
    private LinearLayout llBottom;
    private LinearLayout llHeader;
    private LinearLayout llTab;
    private FrameLayout flDivider;
    private LinearLayout parent;
    private int mIndex = 0;
    private boolean isPlaying = false;
    private int mLastSelected = 0;
    private OnSelectListener mSelectListener;
    private OnClickListener mColumnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setCheck(v.getId());
        }
    };
    private AnimationListener mAnimationListener;
    private Handler mPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY:
                    if (mIndex >= llHistogram.getChildCount()) {
                        // 滑动到最右侧
                        fullScroll(FOCUS_RIGHT);
                        // 默认选择最右边的那个
                        ColumnView v = (ColumnView) llHistogram.getChildAt(llHistogram.getChildCount() - 1);
                        v.performClick();
                        isPlaying = false;
                        mIndex = 0;
                        if (mAnimationListener != null)
                            mAnimationListener.onAnimationDone();
                        break;
                    }
                    ColumnView v = (ColumnView) llHistogram.getChildAt(mIndex);
                    v.startAnim();
                    mIndex++;
                    sendEmptyMessageDelayed(PLAY, 50);
                    break;
            }
        }
    };

    public HistogramView(Context context) {
        super(context);

        init(context, null);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // 隐藏滑动条
        setHorizontalScrollBarEnabled(false);

        parent = new LinearLayout(context);
        parent.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        addView(parent);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HistogramView);

            int columnsPer = a.getInteger(R.styleable.HistogramView_column_per_screen,
                    DEFAULT_COLUMN_PER_SCREEN);
            mBottomTextColor = a.getColor(R.styleable.HistogramView_bottom_text_color, DEFAULT_COLOR);
            mHeaderTextColor = a.getColor(R.styleable.HistogramView_header_text_color, DEFAULT_COLOR);
            mPressedHeaderTextColor = a.getColor(R.styleable.HistogramView_header_text_color_pressed, DEFAULT_COLOR);
            mHistogramColor = a.getColor(R.styleable.HistogramView_histogram_color, DEFAULT_COLOR);
            mContainerColor = a.getColor(R.styleable.HistogramView_container_color, DEFAULT_COLOR);
            mTabColor = a.getColor(R.styleable.HistogramView_tab_color, DEFAULT_COLOR);
            mHeaderContentDividerColor = a.getColor(R.styleable.HistogramView_header_content_divider_color, DEFAULT_COLOR);
            mHeaderHeaderDividerColor = a.getColor(R.styleable.HistogramView_header_header_divider_color, DEFAULT_COLOR);
            int bottomTextSizeSp = a.getDimensionPixelSize(R.styleable.HistogramView_bottom_text_size, -1);
            int headerTextSizeSp = a.getDimensionPixelSize(R.styleable.HistogramView_header_text_size, -1);

            setColumnPerScreen(columnsPer);
            setBottomTextColor(mBottomTextColor);
            setHeaderTextColor(mHeaderTextColor);
            setPressedHeaderTextColor(mPressedHeaderTextColor);
            setBottomTextSize(bottomTextSizeSp);
            setHeaderTextSize(headerTextSizeSp);

            a.recycle();
        }
    }

    public void setData(HistogramEntity[] data) {
        if (isPlaying) {
            return;
        }
        if (data == null || data.length == 0) {
            return;
        }

        isPlaying = true;

        mColumnWid = getMeasuredWidth() / mColumnPerScreen;

        mLastSelected = 0;

        double max = maxInArray(data);

        llHistogram.removeAllViews();
        llBottom.removeAllViews();
        llHeader.removeAllViews();
//        llHeader1.removeAllViews();
//        llHeader2.removeAllViews();
        llTab.removeAllViews();
        flDivider.removeAllViews();

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(mColumnWid,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams header2Param = new LinearLayout.LayoutParams(mColumnWid,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        header2Param.topMargin = dp2px(5);

        LinearLayout.LayoutParams tabParam = new LinearLayout.LayoutParams(mColumnWid - dp2px(20), dp2px(2));

        LinearLayout.LayoutParams dividerParam = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        dividerParam.topMargin = dp2px(4);
        dividerParam.bottomMargin = dp2px(4);

        tabParam.leftMargin = dp2px(10);
        tabParam.rightMargin = dp2px(10);

//        param.leftMargin = mColumnWid;

        for (int i = 0; i < data.length; i++) {
            double d = data[i].count;
            ColumnView view = new ColumnView(getContext());
            view.setLayoutParams(param);
            if (max != 0) {
                view.setRatio((float) d / (float) max);
                view.setPercentage((float) data[i].ratio);
//                if (d != 0)
                view.setShowText(String.valueOf(d));
            } else {
                view.setRatio(0);
                // 全部为0则不显示数字
                // view.setShowText(String.valueOf(0));
            }
            view.setId(i);
            view.setColumnColor(mHistogramColor);
            view.setContainerColor(mContainerColor);
            view.setLineColor(mContainerColor);
            view.setOnClickListener(mColumnListener);
            llHistogram.addView(view);
        }

        for (int i = 0; i < data.length; i++) {
            LinearLayout headerItem = new LinearLayout(getContext());
            headerItem.setOrientation(LinearLayout.VERTICAL);
            headerItem.setLayoutParams(param);
            llHeader.addView(headerItem);

            TextView header1 = new TextView(getContext());
            header1.setGravity(Gravity.CENTER);
            header1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHeaderTextSize);
            header1.setTextColor(mHeaderTextColor);
            header1.setLayoutParams(param);
            header1.setText(data[i].header1);
            header1.setId(i);
            header1.setOnClickListener(mColumnListener);
            headerItem.addView(header1);

            TextView header2 = new TextView(getContext());
            header2.setGravity(Gravity.CENTER);
            header2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHeaderTextSize);
            header2.setTextColor(mHeaderTextColor);
            header2.setLayoutParams(header2Param);
            header2.setText(data[i].header2);
            header2.setId(i);
            header2.setOnClickListener(mColumnListener);
            headerItem.addView(header2);

            if (i < data.length - 1) {
                TextView divider = new TextView(getContext());
                divider.setGravity(Gravity.CENTER);
                divider.setLayoutParams(dividerParam);
                divider.setBackgroundColor(mHeaderHeaderDividerColor);
                llHeader.addView(divider);
            }

            TextView tab = new TextView(getContext());
            tab.setLayoutParams(tabParam);
            tab.setPadding(dp2px(2), 0, dp2px(2), 0);
            tab.setId(i);
            tab.setBackgroundColor(mTabColor);
            tab.setOnClickListener(mColumnListener);
            tab.setVisibility(INVISIBLE);
            llTab.addView(tab);

            TextView view = new TextView(getContext());
            view.setGravity(Gravity.CENTER);
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBottomTextSize);
            view.setTextColor(mBottomTextColor);
            view.setLayoutParams(param);
            view.setText(data[i].bottom);
            view.setId(i);
            view.setOnClickListener(mColumnListener);
            llBottom.addView(view);
        }

        LinearLayout.LayoutParams contentDividerParam = new LinearLayout.LayoutParams(mColumnWid * data.length, 4);

        TextView tab = new TextView(getContext());
        tab.setLayoutParams(contentDividerParam);
        tab.setBackgroundColor(mHeaderContentDividerColor);
        flDivider.addView(tab);

        requestLayout();

        play();
    }

    private void play() {
        mPlayHandler.sendEmptyMessage(PLAY);
    }

    private void initHeader() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp2px(5);
        llHeader = new LinearLayout(getContext());
        llHeader.setOrientation(LinearLayout.HORIZONTAL);
        llHeader.setLayoutParams(params);
        parent.addView(llHeader);
    }

    private void initTab() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp2px(2);
        llTab = new LinearLayout(getContext());
        llTab.setOrientation(LinearLayout.HORIZONTAL);
        llTab.setLayoutParams(params);
        parent.addView(llTab);
    }

    private void initHeaderContentDivider() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flDivider = new FrameLayout(getContext());
        flDivider.setLayoutParams(params);
        parent.addView(flDivider);
    }

    private void initHistogram() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        param.weight = 1;
        llHistogram = new LinearLayout(getContext());
        llHistogram.setOrientation(LinearLayout.HORIZONTAL);
        llHistogram.setLayoutParams(param);
        parent.addView(llHistogram);
    }

    private void initBottom() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp2px(5);
        params.bottomMargin = dp2px(5);
        llBottom = new LinearLayout(getContext());
        llBottom.setOrientation(LinearLayout.HORIZONTAL);
        llBottom.setLayoutParams(params);
        parent.addView(llBottom);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mColumnWid = w / mColumnPerScreen;

//        initHeader1();
//        initHeader2();
        initHeader();
        initTab();
        initHeaderContentDivider();
        initHistogram();
        initBottom();
    }

    private double maxInArray(HistogramEntity[] array) {
        double[] temp = new double[array.length];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = array[i].count;
        }
        Arrays.sort(temp);
        return temp[temp.length - 1];
    }

    protected int dp2px(int dpValue) {
        if (!isInEditMode())
            return DisplayUtil.dip2px(getContext(), dpValue);
        else
            return 20;
    }

    protected int px2dp(int pxValue) {
        if (!isInEditMode())
            return DisplayUtil.px2dip(getContext(), pxValue);
        else
            return 20;
    }

    public void setDefaultDateTextArray(String[] defaultDateTextArray) {
        if (defaultDateTextArray == null || defaultDateTextArray.length == 0)
            return;
        mDefaultDateText = defaultDateTextArray;
    }

    public void setCheck(int position) {
        if (isPlaying || llHistogram == null)
            return;
        if (position < 0 || position > llHistogram.getChildCount())
            return;
        ColumnView columnOld = (ColumnView) llHistogram.getChildAt(mLastSelected);
        columnOld.setSelect(false);
        ColumnView columnNew = (ColumnView) llHistogram.getChildAt(position);
        columnNew.setSelect(true);

//        TextView header1 = (TextView) llHeader1.getChildAt(mLastSelected);
//        TextView header2 = (TextView) llHeader2.getChildAt(mLastSelected);
//        header1.setTextColor(mHeaderTextColor);
//        header2.setTextColor(mHeaderTextColor);
//
//        header1 = (TextView) llHeader1.getChildAt(position);
//        header2 = (TextView) llHeader2.getChildAt(position);
//        header1.setTextColor(mPressedHeaderTextColor);
//        header2.setTextColor(mPressedHeaderTextColor);

        LinearLayout headerItemLayout = (LinearLayout) llHeader.getChildAt(handleHeaderLayoutIndex(mLastSelected));
        setTextColorInLayout(headerItemLayout, mHeaderTextColor);
        headerItemLayout = (LinearLayout) llHeader.getChildAt(handleHeaderLayoutIndex(position));
        setTextColorInLayout(headerItemLayout, mPressedHeaderTextColor);

        TextView tab = (TextView) llTab.getChildAt(mLastSelected);
        tab.setVisibility(INVISIBLE);
        tab = (TextView) llTab.getChildAt(position);
        tab.setVisibility(VISIBLE);

        mLastSelected = position;
        if (mSelectListener != null)
            mSelectListener.onSelected(position);
    }

    private int handleHeaderLayoutIndex(int originIndex) {
        return originIndex * 2;
    }

    private void setTextColorInLayout(LinearLayout layout, int color) {
        int count = layout.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = layout.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTextColor(color);
            }
        }
    }

    public void setColumnPerScreen(int columnPerScreen) {
        if (columnPerScreen < 1 || columnPerScreen > 10) {
            return;
        }
        mColumnPerScreen = columnPerScreen;
    }

    public void setBottomTextColor(int color) {
        mBottomTextColor = color;
    }

    public void setHeaderTextColor(int color) {
        mHeaderTextColor = color;
    }

    public void setPressedHeaderTextColor(int color) {
        mPressedHeaderTextColor = color;
    }

    private void setBottomTextSize(int size) {
        mBottomTextSize = size;
    }

    private void setHeaderTextSize(int size) {
        mHeaderTextSize = size;
    }

    public void setSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }

    public void setAnimationListener(AnimationListener listener) {
        mAnimationListener = listener;
    }

    public interface OnSelectListener {
        void onSelected(int index);
    }

    public interface AnimationListener {
        void onAnimationDone();
    }

    public static class HistogramEntity {
        public String header1;
        public String header2;
        public String bottom;
        public double count;
        public double ratio;

        public HistogramEntity(String header1, String header2, String bottom, double count, double ratio) {
            this.header1 = header1;
            this.header2 = header2;
            this.bottom = bottom;
            this.count = count;
            if (ratio < 0) ratio = 0;
            else if (ratio >= 1) ratio = 1;
            this.ratio = ratio;
        }
    }
}
