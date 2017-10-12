package com.salmonzhg.histogramview_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.salmonzhg.histogramview_demo.utils.DateUtils;
import com.salmonzhg.histogramview_demo.utils.StepConvertUtil;
import com.salmonzhg.histogramview_demo.views.HistogramView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SalmonZhg";
    private RadioGroup mRadioGroup;
    private HistogramView mHistogram;
    private TextView mTextDate, mTextStep, mTextDistance, mTextCalories;
    private RadioButton mRadioButtonWeek, mRadioButtonMonth;
    private Toast mToast;
    private HistogramView.HistogramEntity[] mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHistogram = (HistogramView) findViewById(R.id.histogram);
        mRadioGroup = (RadioGroup) findViewById(R.id.time_radio_group);
        mTextDate = (TextView) findViewById(R.id.text_date);
        mTextStep = (TextView) findViewById(R.id.text_step);
        mTextDistance = (TextView) findViewById(R.id.text_distance);
        mTextCalories = (TextView) findViewById(R.id.text_calories);
        mRadioButtonWeek = ((RadioButton) findViewById(R.id.radio_week_button));
        mRadioButtonMonth = ((RadioButton) findViewById(R.id.radio_month_button));

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mRadioButtonMonth.setClickable(false);
                mRadioButtonWeek.setClickable(false);
                switch (checkedId) {
                    case R.id.radio_week_button:
                        mHistogram.setData(mData = genRandomWeekData());
                        break;
                    case R.id.radio_month_button:
                        mHistogram.setData(mData = genRandomMonthData());
                        break;
                }
            }
        });

        mHistogram.setSelectListener(new HistogramView.OnSelectListener() {
            @Override
            public void onSelected(int index) {
                showDetail(mData[index]);
            }
        });

        mHistogram.setAnimationListener(new HistogramView.AnimationListener() {
            @Override
            public void onAnimationDone() {
                mRadioButtonMonth.setClickable(true);
                mRadioButtonWeek.setClickable(true);
                mHistogram.setCheck(mData.length-1);
            }
        });

        mHistogram.post(new Runnable() {
            @Override
            public void run() {
                ((RadioButton) findViewById(R.id.radio_week_button)).setChecked(true);
            }
        });
    }

    private void showDetail(HistogramView.HistogramEntity data) {
        mTextDate.setText(data.bottom);
        mTextStep.setText(String.valueOf(data.count));
        mTextDistance.setText(StepConvertUtil.stepToDistance(StepConvertUtil.MALE,
                StepConvertUtil.DEFAULT_TALL, (int) data.count)+"");
        mTextCalories.setText(StepConvertUtil.stepToCalories(StepConvertUtil.DEFAULT_TALL,
                StepConvertUtil.DEFAULT_WEIGHT, (int) data.count)+"");
    }

    private void showToast(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(s);
        mToast.show();
    }

    private HistogramView.HistogramEntity[] genRandomWeekData() {
        String[] header1s = daysToWeek();
        String[] header2s = daysToDate(7);
        HistogramView.HistogramEntity[] result = new HistogramView.HistogramEntity[7];
        for (int i = 0; i < result.length; i++) {

            double num = 0.1d * ((int)(Math.random() * 41));
            HistogramView.HistogramEntity e = new HistogramView.HistogramEntity(header1s[i], header2s[i],
                    String .format("%.1f", num) + "h", num, 0.5);
            result[i] = e;
        }
        return result;
    }

    private HistogramView.HistogramEntity[] genRandomMonthData() {
        String[] days = daysToDate(28);
        HistogramView.HistogramEntity[] result = new HistogramView.HistogramEntity[28];
        for (int i = 0; i < result.length; i++) {
            int num = (int) (2000 + 3000 * Math.random());
            HistogramView.HistogramEntity e = new HistogramView.HistogramEntity("SUN", "9/3",
                    String.valueOf(days[i]), num, 0.5);
            result[i] = e;
        }
        return result;
    }

    /**
     * 根据天数倒退生成日期
     *
     * @param days 最近多少天
     * @return
     */
    private String[] daysToDate(int days) {
        if (days < 0) {
            return new String[0];
        }
        String[] dates = new String[days];
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, -days);
        for (int i = 0; i < days; i++) {
            calendar.add(Calendar.DATE, 1);
            dates[i] = (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.DATE);
        }
        return dates;
    }

    /**
     * 最近一星期
     * @return
     */
    private String[] daysToWeek() {
        String[] dates = new String[7];
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, -8);
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, 1);
            dates[i] = DateUtils.intToWeek(calendar.get(Calendar.DAY_OF_WEEK));
        }
        return dates;
    }
}
