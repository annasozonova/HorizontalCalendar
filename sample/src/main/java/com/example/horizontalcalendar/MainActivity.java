package com.example.horizontalcalendar;

import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.horizontalcalendar.R;
import com.sahana.horizontalcalendar.HorizontalCalendar;

import java.util.Calendar;

/**
 * MainActivity hosts the HorizontalCalendar widget and sets its initial date.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HorizontalCalendar horizontalCalendar = findViewById(R.id.horizontalCalendar);
        Calendar targetDate = Calendar.getInstance();
        horizontalCalendar.setInitialDate(targetDate);
    }
}
