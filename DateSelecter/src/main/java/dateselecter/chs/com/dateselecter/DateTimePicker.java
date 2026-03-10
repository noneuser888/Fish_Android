package dateselecter.chs.com.dateselecter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Calendar;

import dateselecter.chs.com.dateselecter.wheelview.OnWheelChangedListener;
import dateselecter.chs.com.dateselecter.wheelview.OnWheelScrollListener;
import dateselecter.chs.com.dateselecter.wheelview.WheelView;
import dateselecter.chs.com.dateselecter.wheelview.adapter.NumericWheelAdapter;


public class DateTimePicker {
    private Activity mContext;
    private PopupWindow popupWindow;
    private WheelView wl_start_year;
    private WheelView wl_start_month;
    private WheelView wl_start_day;
    private WheelView wl_end_year;
    private WheelView wl_end_month;
    private WheelView wl_end_day;

    private View rl_main;
    private TextView tv_start_time, tv_end_time, tv_ok;
    private int curYear;
    private int curMonth;
    private String timeArray[] = new String[2];
    private DateTimePickerResultListener mDateTimePickerResultListener;

    public void initDateTimePicker(Activity mContext, View rl_main) {
        this.mContext = mContext;
        this.rl_main = rl_main;
        initPopView();
    }

    public void addDateTimePickerResultListener(DateTimePickerResultListener mDateTimePickerResultListener) {
        this.mDateTimePickerResultListener = mDateTimePickerResultListener;
    }

    public void showPopView() {
        popupWindow.showAtLocation(rl_main, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
    }


    private void initPopView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupWindowView = inflater.inflate(R.layout.pop_term_select, null);
        tv_start_time = popupWindowView.findViewById(R.id.tv_start_time);
        tv_end_time = popupWindowView.findViewById(R.id.tv_end_time);
        tv_ok = popupWindowView.findViewById(R.id.tv_ok);
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        initWheelView(popupWindowView);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hiddenPopWindow();
            }
        });

    }


    private void hiddenPopWindow() {
        timeArray[0] = tv_start_time.getText().toString();
        timeArray[1] = tv_end_time.getText().toString();
        if (mDateTimePickerResultListener != null)
            mDateTimePickerResultListener.onResult(timeArray);
    }


    private void initWheelView(View view) {
        Calendar c = Calendar.getInstance();
        curYear = c.get(Calendar.YEAR);
        curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        /*****************开始时间***********************/
        wl_start_year = (WheelView) view.findViewById(R.id.wl_start_year);
        wl_start_month = (WheelView) view.findViewById(R.id.wl_start_month);
        wl_start_day = (WheelView) view.findViewById(R.id.wl_start_day);

        NumericWheelAdapter numericWheelAdapterStart1 = new NumericWheelAdapter(mContext, 2000, 2100);
        numericWheelAdapterStart1.setLabel(" ");
        wl_start_year.setViewAdapter(numericWheelAdapterStart1);
        numericWheelAdapterStart1.setTextColor(R.color.black);
        numericWheelAdapterStart1.setTextSize(20);
        wl_start_year.setCyclic(true);//是否可循环滑动
        wl_start_year.addScrollingListener(startScrollListener);
        wl_start_year.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                curYear = newValue + 2000;
                initStartDayAdapter();
            }
        });

        NumericWheelAdapter numericWheelAdapterStart2 = new NumericWheelAdapter(mContext, 1, 12, "%02d");
        numericWheelAdapterStart2.setLabel(" ");
        wl_start_month.setViewAdapter(numericWheelAdapterStart2);
        numericWheelAdapterStart2.setTextColor(R.color.black);
        numericWheelAdapterStart2.setTextSize(20);
        wl_start_month.setCyclic(true);
        wl_start_month.addScrollingListener(startScrollListener);
        wl_start_month.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                curMonth = newValue + 1;
                initStartDayAdapter();
            }
        });
        initStartDayAdapter();

        /********************结束时间*********************/

        wl_end_year = (WheelView) view.findViewById(R.id.wl_end_year);
        wl_end_month = (WheelView) view.findViewById(R.id.wl_end_month);
        wl_end_day = (WheelView) view.findViewById(R.id.wl_end_day);

        NumericWheelAdapter numericWheelAdapterEnd1 = new NumericWheelAdapter(mContext, 2000, 2100);
        numericWheelAdapterEnd1.setLabel(" ");
        wl_end_year.setViewAdapter(numericWheelAdapterEnd1);
        numericWheelAdapterEnd1.setTextColor(R.color.black);
        numericWheelAdapterEnd1.setTextSize(20);
        wl_end_year.setCyclic(true);//是否可循环滑动
        wl_end_year.addScrollingListener(endScrollListener);
        wl_end_year.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                curYear = newValue + 2000;
                initEndDayAdapter();
            }
        });

        NumericWheelAdapter numericWheelAdapterEnd2 = new NumericWheelAdapter(mContext, 1, 12, "%02d");
        numericWheelAdapterEnd2.setLabel(" ");
        wl_end_month.setViewAdapter(numericWheelAdapterEnd2);
        numericWheelAdapterEnd2.setTextColor(R.color.black);
        numericWheelAdapterEnd2.setTextSize(20);
        wl_end_month.setCyclic(true);
        wl_end_month.addScrollingListener(endScrollListener);
        wl_end_month.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                curMonth = newValue + 1;
                initEndDayAdapter();
            }
        });

        initEndDayAdapter();

        wl_start_year.setCurrentItem(curYear - 2000);
        wl_start_month.setCurrentItem(curMonth - 1);
        wl_start_day.setCurrentItem(curDate - 1);

        wl_end_year.setCurrentItem(curYear - 2000);
        wl_end_month.setCurrentItem(curMonth - 1);
        wl_end_day.setCurrentItem(curDate - 1);
    }

    private void initStartDayAdapter() {
        NumericWheelAdapter numericWheelAdapterStart3 = new NumericWheelAdapter(mContext, 1, getDay(curYear, curMonth), "%02d");
        numericWheelAdapterStart3.setLabel(" ");
        wl_start_day.setViewAdapter(numericWheelAdapterStart3);
        numericWheelAdapterStart3.setTextColor(R.color.black);
        numericWheelAdapterStart3.setTextSize(20);
        wl_start_day.setCyclic(true);
        wl_start_day.addScrollingListener(startScrollListener);
    }

    private void initEndDayAdapter() {
        NumericWheelAdapter numericWheelAdapterEnd3 = new NumericWheelAdapter(mContext, 1, getDay(curYear, curMonth), "%02d");
        numericWheelAdapterEnd3.setLabel(" ");
        wl_end_day.setViewAdapter(numericWheelAdapterEnd3);
        numericWheelAdapterEnd3.setTextColor(R.color.black);
        numericWheelAdapterEnd3.setTextSize(20);
        wl_end_day.setCyclic(true);
        wl_end_day.addScrollingListener(endScrollListener);
    }

    @SuppressLint("SetTextI18n")
    private OnWheelScrollListener startScrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = wl_start_year.getCurrentItem() + 2000;//年
            int n_month = wl_start_month.getCurrentItem() + 1;//月
            int n_day = wl_start_day.getCurrentItem() + 1;//日
            tv_start_time.setText(n_year + "/" + n_month + "/" + n_day);
        }
    };
    private OnWheelScrollListener endScrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = wl_end_year.getCurrentItem() + 2000;//年
            int n_month = wl_end_month.getCurrentItem() + 1;//月
            int n_day = wl_end_day.getCurrentItem() + 1;//日
            tv_end_time.setText(n_year + "/" + n_month + "/" + n_day);
        }
    };

    /**
     * 根据年月获得 这个月总共有几天
     *
     * @param year
     * @param month
     */
    public int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        if (year % 4 == 0) flag = true;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                break;
        }
        return day;
    }

   public interface DateTimePickerResultListener {
         void onResult(String[] timeArray);
    }

//    /**
//     * 让屏幕变暗
//     */
//    private void makeWindowDark() {
//        Window window = mContext.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.alpha = 0.5f;
//        window.setAttributes(lp);
//    }
//
//    /**
//     * 让屏幕变亮
//     */
//    private void makeWindowLight() {
//        Window window = mContext.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.alpha = 1f;
//        window.setAttributes(lp);
//    }
}