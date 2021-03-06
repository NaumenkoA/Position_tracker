package com.example.alex.positiontracker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.alex.positiontracker.R;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatePickerActivity extends AppCompatActivity {

    @BindView(R.id.submitButton) Button mSubmitButton;
    @BindView(R.id.datePicker) DatePicker mDatePicker;

    private boolean mIsEndOfDay;
    public static final String RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        long selectedFromDate = intent.getLongExtra(MainActivity.USER_SELECTED_FROM_DATE, 0);
        long selectedToDate = intent.getLongExtra(MainActivity.USER_SELECTED_TO_DATE, 0);
        if (selectedFromDate !=0) {
            updateDatePicker(selectedFromDate);
            mIsEndOfDay = false;
        } else {
            updateDatePicker(selectedToDate);
            mIsEndOfDay = true;
        }


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(RESULT, getDateFromDatePicker(mIsEndOfDay));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void updateDatePicker(long selectedDate) {
        Date date = new Date(selectedDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker.updateDate(year, month, day);
    }

    private long getDateFromDatePicker(boolean endOfDay){
        int day = mDatePicker.getDayOfMonth();
        int month = mDatePicker.getMonth();
        int year = mDatePicker.getYear();
        int hour;
        int minute;

        Calendar calendar = Calendar.getInstance();
        if (endOfDay) {
            hour = 23;
            minute = 59;
        } else {
            hour = 0;
            minute = 0;
        }

        calendar.set(year, month, day, hour, minute);
        return calendar.getTimeInMillis();
    }
}
