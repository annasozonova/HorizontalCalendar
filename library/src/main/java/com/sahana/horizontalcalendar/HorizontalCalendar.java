/*
 * Based on HorizontalCalendar (c) 2020 B-Sahana (MIT License)
 * Modified by Anna Sozonova for educational purposes.
 */

package com.sahana.horizontalcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 * HorizontalCalendar is a custom calendar widget that displays dates in a horizontal list.
 * It supports displaying a range of past and future dates based on XML attributes.
 */
public class HorizontalCalendar extends RelativeLayout {

    /** RecyclerView that displays dates in a horizontal list. */
    private RecyclerView recyclerView;

    /** LayoutManager for horizontal arrangement of date items. */
    private LinearLayoutManager layoutManager;

    /** Helper to snap the nearest date item to the center on scroll. */
    private LinearSnapHelper snapHelper;

    /** View that highlights the centered (selected) date. */
    private View selectionFrame;

    /** TextView showing the currently centered date (day + month + year). */
    private TextView monthYearTextView;

    /** Adapter backing the RecyclerView; generates date items around baseDate. */
    private HorizontalCalendarAdapter adapter;

    /** Decoration drawing vertical dividers at week boundaries. */
    private WeekDividerDecoration weekDividerDecoration;

    /** The “center” date from which visibleRange is counted. */
    private Calendar baseDate = Calendar.getInstance();

    /** Number of days shown before and after baseDate (total = 2*visibleRange + 1). */
    private int visibleRange = 180;

    /** Whether to draw week dividers between Saturdays/Sundays. */
    private boolean showWeekDividers = true;

    /** Whether the week should start on Monday (false = start on Sunday). */
    private boolean weekStartsOnMonday = true;

    /** Index in the adapter of the currently centered (selected) item. */
    private int currentCenterPosition;


    // Constructors to allow XML inflation
    public HorizontalCalendar(Context context) {
        super(context);
        init(context, null);
    }

    public HorizontalCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HorizontalCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Performs view inflation, attribute reading, and RecyclerView setup.
     */
    private void init(Context context, AttributeSet attrs) {
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.widget_horizontal_calendar, this, true);

        // Find views
        recyclerView = findViewById(R.id.recyclerView);
        monthYearTextView = findViewById(R.id.text_month_year);
        selectionFrame = findViewById(R.id.selectionFrame);

        // Read custom attributes from XML
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalCalendar, 0, 0);
            showWeekDividers = typedArray.getBoolean(R.styleable.HorizontalCalendar_showWeekDividers, this.showWeekDividers);
            weekStartsOnMonday = typedArray.getBoolean(R.styleable.HorizontalCalendar_weekStartsOnMonday, this.weekStartsOnMonday);
            visibleRange = typedArray.getInt(
                    R.styleable.HorizontalCalendar_visibleRange,
                    visibleRange
            );
            typedArray.recycle();
        }

        // Set up RecyclerView
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Attach snapping helper to center items nicely
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // Handle scroll events to update month-year
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                updateHeaderOnScroll();
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateCenterPosition();
                }
            }
        });
    }

    /**
     * Smoothly scrolls to and centers the specified date.
     *
     * @param date target date; must not be null
     */
    public void scrollToDateSmooth(Calendar date) {
        if (layoutManager == null || adapter == null || date == null) return;

        long diff = (date.getTimeInMillis() - baseDate.getTimeInMillis())
                / (24L * 60 * 60 * 1000);
        int targetPos = adapter.getStartPosition() + (int) diff;

        final float MILLISECONDS_PER_INCH = 100f;  // speed of the scroll


        LinearSmoothScroller scroller = new LinearSmoothScroller(getContext()) {
            @Override
            public PointF computeScrollVectorForPosition(int pos) {
                return layoutManager.computeScrollVectorForPosition(pos);
            }

            @Override
            public int calculateDtToFit(int viewStart, int viewEnd,
                                           int boxStart,  int boxEnd,
                                           int snapPreference) {
                int viewCenter = (viewStart + viewEnd) / 2;
                int boxCenter  = (boxStart  + boxEnd ) / 2;
                return boxCenter - viewCenter;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics dm) {
                return MILLISECONDS_PER_INCH / dm.densityDpi;
            }

            @Override
            protected int getHorizontalSnapPreference() {
                return SNAP_TO_ANY;
            }
        };
        scroller.setTargetPosition(targetPos);
        layoutManager.startSmoothScroll(scroller);
    }

    /**
     * Instantly (no animation) centers the specified date.
     *
     * @param date target date; must not be null
     */
    public void scrollToDateInstant(Calendar date) {
        if (layoutManager == null || adapter == null || date == null) return;

        long days = (date.getTimeInMillis() - baseDate.getTimeInMillis())
                / (24L * 60 * 60 * 1000);
        final int targetPos = adapter.getStartPosition() + (int) days;

        recyclerView.scrollToPosition(targetPos);

        recyclerView.post(() -> {
            View targetView = layoutManager.findViewByPosition(targetPos);
            if (targetView == null) return;

            int[] dist = snapHelper.calculateDistanceToFinalSnap(layoutManager, targetView);
            if (dist != null) {
                recyclerView.scrollBy(dist[0], dist[1]);
            }

            currentCenterPosition = targetPos;
            adapter.setSelectedPosition(targetPos);
            updateUpDate();
        });
    }

    /** Returns to the original baseDate with smooth animation. */
    public void resetToInitialDate() {
        scrollToDateSmooth(baseDate);
    }

    /**
     * Sets the date to center on initial display.
     *
     * @param date base date; must not be null
     */
    public void setInitialDate(Calendar date) {
        if (date == null) return;

        baseDate = (Calendar) date.clone();
        baseDate.set(Calendar.HOUR_OF_DAY, 0);
        baseDate.set(Calendar.MINUTE,      0);
        baseDate.set(Calendar.SECOND,      0);
        baseDate.set(Calendar.MILLISECOND, 0);

        adapter = new HorizontalCalendarAdapter(baseDate, visibleRange);
        recyclerView.setAdapter(adapter);
        adapter.setOnDateClickListener(this::scrollToDateSmooth);

        currentCenterPosition = adapter.getStartPosition();
        if (showWeekDividers) {
            weekDividerDecoration = new WeekDividerDecoration(
                    weekStartsOnMonday,
                    adapter.getStartPosition()
            );
            recyclerView.addItemDecoration(weekDividerDecoration);
        }

        // center once after layout pass
        recyclerView.post(() -> scrollToDateInstant(baseDate));
    }

    /**
     * Updates the displayed date based on the centered item.
     */
    private void updateUpDate() {
        if (layoutManager == null || adapter == null) return;

        int dayOffset = currentCenterPosition - adapter.getStartPosition();

        Calendar calendar = (Calendar) baseDate.clone();
        calendar.add(Calendar.DAY_OF_YEAR, dayOffset);

        int day   = calendar.get(Calendar.DAY_OF_MONTH);
        String month = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)];
        int year  = calendar.get(Calendar.YEAR);

        String header = String.format(
                java.util.Locale.getDefault(),
                "%d %s %d",
                day, month, year
        );

        monthYearTextView.setText(header);
    }

    /** Fixes selection frame when scrolling stops. */
    private void updateCenterPosition() {
        if (layoutManager == null || adapter == null) return;

        int centerX = recyclerView.getWidth() / 2;
        int minDistance = Integer.MAX_VALUE;
        int centerPosition = RecyclerView.NO_POSITION;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            int childCenterX = (child.getLeft() + child.getRight()) / 2;
            int distance = Math.abs(childCenterX - centerX);

            if (distance < minDistance) {
                minDistance = distance;
                centerPosition = recyclerView.getChildAdapterPosition(child);
            }
        }

        if (centerPosition != RecyclerView.NO_POSITION) {
            currentCenterPosition = centerPosition;
            adapter.setSelectedPosition(centerPosition);
            updateUpDate();
        }
    }

    /** Updates header text dynamically during scrolling. */
    private void updateHeaderOnScroll() {
        if (layoutManager == null || adapter == null) return;

        int centerX = recyclerView.getWidth() / 2;
        int minDistance = Integer.MAX_VALUE;
        View closest = null;
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            int childCenterX = (child.getLeft() + child.getRight()) / 2;
            int dist = Math.abs(childCenterX - centerX);
            if (dist < minDistance) {
                minDistance = dist;
                closest = child;
            }
        }
        if (closest == null) return;

        int pos = recyclerView.getChildAdapterPosition(closest);
        long dayOffset = pos - adapter.getStartPosition();
        Calendar cal = (Calendar) baseDate.clone();
        cal.add(Calendar.DAY_OF_YEAR, (int) dayOffset);

        String month = new DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)];
        String header = String.format(
                Locale.getDefault(),
                "%d %s %d",
                cal.get(Calendar.DAY_OF_MONTH),
                month,
                cal.get(Calendar.YEAR)
        );
        monthYearTextView.setText(header);
    }

    /**
     * Enables or disables week dividers.
     *
     * @param enabled true to show, false to hide
     */
     public void setShowWeekDividers(boolean enabled){
         if (adapter == null) return;
         if (enabled) {
             if (weekDividerDecoration == null) {
                 weekDividerDecoration = new WeekDividerDecoration(
                         weekStartsOnMonday,
                         adapter.getStartPosition()
                 );
                 recyclerView.addItemDecoration(weekDividerDecoration);
             }
         } else {
             if (weekDividerDecoration != null) {
                 recyclerView.removeItemDecoration(weekDividerDecoration);
                 weekDividerDecoration = null;
             }
         }
         this.showWeekDividers = enabled;
     }

    /**
     * Sets whether weeks should start on Monday.
     *
     * @param startsOnMonday true if week starts Monday, false if Sunday
     */
    public void setWeekStartsOnMonday(boolean startsOnMonday){
        this.weekStartsOnMonday = startsOnMonday;

        if (adapter == null) return;

        if (weekDividerDecoration != null) {
            recyclerView.removeItemDecoration(weekDividerDecoration);
            weekDividerDecoration = new WeekDividerDecoration(
                    weekStartsOnMonday,
                    adapter.getStartPosition()
            );
            recyclerView.addItemDecoration(weekDividerDecoration);

            recyclerView.invalidateItemDecorations();
        }
    }
}


