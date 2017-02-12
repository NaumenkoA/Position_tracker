package com.example.alex.positiontracker.ui;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alex.positiontracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimePickerActivity extends AppCompatActivity {

    public static final String RESULT_HOUR = "hour" ;
    public static final String RESULT_MINUTE = "minute" ;
    @BindView(R.id.timePicker) TimePicker mTimePicker;
    @BindView(R.id.submitButton) Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        ButterKnife.bind(this);
        mTimePicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= 23 ) {
            mTimePicker.setHour(0);
            mTimePicker.setMinute(0);
        } else {
            mTimePicker.setCurrentHour(0);
            mTimePicker.setCurrentMinute(0);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour;
                int minute;
                if (Build.VERSION.SDK_INT >= 23 ) {
                    hour = mTimePicker.getHour();
                    minute = mTimePicker.getMinute();
                } else {
                    hour = mTimePicker.getCurrentHour();
                    minute = mTimePicker.getCurrentMinute();
                }
                if (hour == 0 && minute == 0) {
                    Toast.makeText(TimePickerActivity.this, "Notification period should be 1 min or more", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(RESULT_HOUR, hour);
                intent.putExtra(RESULT_MINUTE, minute);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
