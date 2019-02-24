package com.codbking.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.codbking.widget.bean.DateType;
import com.codbking.widget.genview.WheelGeneralAdapter;
import com.codbking.widget.view.WheelView;

import java.util.Date;

import yixia.lib.core.util.TimeUtil;

@SuppressLint("ViewConstructor")
@SuppressWarnings("unused")
class DatePicker extends BaseWheelPick {

    private WheelView yearView;
    private WheelView monthView;
    private WheelView dayView;
    private TextView weekView;
    private WheelView hourView;
    private WheelView minuteView;

    private Integer[] yearArr, mothArr, dayArr, hourArr, minutArr;
    private DatePickerHelper datePicker;

    public DateType type = DateType.TYPE_ALL;

    //开始时间
    private Date startDate = new Date();
    //年份限制，上下5年
    private int yearPast = 5;
    private int yearFuture = 5;

    private OnChangeListener onChangeListener;
    private int selectDay;

    //选择时间回调
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public DatePicker(Context context, DateType type) {
        super(context);
        if (this.type != null) {
            this.type = type;
        }
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setYearLimit(int yearLimit) {
        this.yearPast = yearLimit;
        this.yearFuture = yearLimit;
    }

    public void setYearLimit(int yearPast, int yearFuture) {
        this.yearPast = yearPast;
        this.yearFuture = yearFuture;
    }

    //初始化值
    public void init() {

        this.minuteView = findViewById(R.id.minute);
        this.hourView = findViewById(R.id.hour);
        this.weekView = findViewById(R.id.week);
        this.dayView = findViewById(R.id.day);
        this.monthView = findViewById(R.id.month);
        this.yearView = findViewById(R.id.year);

        switch (type) {
            case TYPE_ALL:
                this.minuteView.setVisibility(VISIBLE);
                this.hourView.setVisibility(VISIBLE);
                this.weekView.setVisibility(VISIBLE);
                this.dayView.setVisibility(VISIBLE);
                this.monthView.setVisibility(VISIBLE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_YMDHM:
                this.minuteView.setVisibility(VISIBLE);
                this.hourView.setVisibility(VISIBLE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(VISIBLE);
                this.monthView.setVisibility(VISIBLE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_YMDH:
                this.minuteView.setVisibility(GONE);
                this.hourView.setVisibility(VISIBLE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(VISIBLE);
                this.monthView.setVisibility(VISIBLE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_YMD:
                this.minuteView.setVisibility(GONE);
                this.hourView.setVisibility(GONE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(VISIBLE);
                this.monthView.setVisibility(VISIBLE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_HM:
                this.minuteView.setVisibility(VISIBLE);
                this.hourView.setVisibility(VISIBLE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(GONE);
                this.monthView.setVisibility(GONE);
                this.yearView.setVisibility(GONE);
                break;
        }

        datePicker = new DatePickerHelper();
        datePicker.setStartDate(startDate, this.yearPast, this.yearFuture);

        dayArr = datePicker.genDay();
        yearArr = datePicker.genYear();
        mothArr = datePicker.genMonth();
        hourArr = datePicker.genHour();
        minutArr = datePicker.genMinute();

        weekView.setText(datePicker.getDisplayStartWeek());

        setWheelListener(yearView, yearArr, false);
        setWheelListener(monthView, mothArr, true);
        setWheelListener(dayView, dayArr, true);
        setWheelListener(hourView, hourArr, true);
        setWheelListener(minuteView, minutArr, true);

        yearView.setCurrentItem(datePicker.findIndexByValue(datePicker.getToady(DatePickerHelper.Type.YEAR), yearArr));
        monthView.setCurrentItem(datePicker.findIndexByValue(datePicker.getToady(DatePickerHelper.Type.MOTH), mothArr));
        dayView.setCurrentItem(datePicker.findIndexByValue(datePicker.getToady(DatePickerHelper.Type.DAY), dayArr));
        hourView.setCurrentItem(datePicker.findIndexByValue(datePicker.getToady(DatePickerHelper.Type.HOUR), hourArr));
        minuteView.setCurrentItem(datePicker.findIndexByValue(datePicker.getToady(DatePickerHelper.Type.MINUTE), minutArr));
    }

    protected String[] convertData(WheelView wheelView, Integer[] data) {
        if (wheelView == yearView) {
            return datePicker.getDisplayValue(data, "年");
        } else if (wheelView == monthView) {
            return datePicker.getDisplayValue(data, "月");
        } else if (wheelView == dayView) {
            return datePicker.getDisplayValue(data, "日");
        } else if (wheelView == hourView) {
            return datePicker.getDisplayValue(data, "");
        } else if (wheelView == minuteView) {
            return datePicker.getDisplayValue(data, "");
        }
        return new String[0];
    }

    @Override
    protected int getLayout() {
        return R.layout.cbk_wheel_picker;
    }

    @Override
    protected int getItemHeight() {
        return dayView.getItemHeight();
    }

    @Override
    protected void setData(Object[] data) {
    }

    private void setChangeDaySelect(int year, int moth) {
        dayArr = datePicker.genDay(year, moth);
        WheelGeneralAdapter adapter = (WheelGeneralAdapter) dayView.getViewAdapter();
        adapter.setData(convertData(dayView, dayArr));

        int index = datePicker.findIndexByValue(selectDay, dayArr);
        if (index == -1) {
            dayView.setCurrentItem(0);
        } else {
            dayView.setCurrentItem(index);
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        int year = yearArr[yearView.getCurrentItem()];
        int moth = mothArr[monthView.getCurrentItem()];
        int day = dayArr[dayView.getCurrentItem()];
        int hour = hourArr[hourView.getCurrentItem()];
        int minute = minutArr[minuteView.getCurrentItem()];

        if (wheel == yearView || wheel == monthView) {
            setChangeDaySelect(year, moth);
        } else {
            selectDay = day;
        }

        if (wheel == yearView || wheel == monthView || wheel == dayView) {
            weekView.setText(datePicker.getDisplayWeek(year, moth, day));
        }

        if (onChangeListener != null) {
            onChangeListener.onChanged(TimeUtil.getDate(year, moth, day, hour, minute));
        }

    }

    @Override
    public void onScrollingStarted(WheelView wheel) {
    }

    @Override
    public void onScrollingFinished(WheelView wheel) {
    }

    //获取选中日期
    public Date getSelectDate() {
        int year = yearArr[yearView.getCurrentItem()];
        int moth = mothArr[monthView.getCurrentItem()];
        int day = dayArr[dayView.getCurrentItem()];
        int hour = hourArr[hourView.getCurrentItem()];
        int minute = minutArr[minuteView.getCurrentItem()];
        return TimeUtil.getDate(year, moth, day, hour, minute);
    }

}
