package com.sahana.horizontalcalendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sahana.horizontalcalendar.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 * Adapter for {@link HorizontalCalendar}. Dynamically generates date items
 * based on a base date and visible range, without storing a full date list.
 */
public class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.DateViewHolder> {
    /** The base date from which offsets are calculated. */
    private final Calendar baseDate;

    /** Number of days shown before and after baseDate (half of totalDays - 1). */
    private final int visibleRange;

    /** Total number of days in the adapter = visibleRange * 2 + 1. */
    private final int totalDays;

    /** Adapter position that corresponds to baseDate (center position). */
    private final int startPosition;

    /** Currently selected adapter position, or -1 if none. */
    private int selectedPosition = -1;

    private OnDateClickListener dateClickListener;


    /**
     * Listener for click events on individual date items.
     */
    public interface OnDateClickListener {
        /**
         * Called when a date item is clicked.
         *
         * @param date the Calendar instance representing the clicked date
         */
        void onDateClick(Calendar date);
    }

    /**
     * Constructs the adapter.
     *
     * @param baseDate     the center date for this calendar (will be cloned)
     * @param visibleRange number of days before and after baseDate to display
     */
    public HorizontalCalendarAdapter(Calendar baseDate, int visibleRange) {
        // Clone and normalize baseDate to midnight
        this.baseDate = (Calendar) baseDate.clone();
        this.baseDate.set(Calendar.HOUR_OF_DAY,0);
        this.baseDate.set(Calendar.MINUTE,0);
        this.baseDate.set(Calendar.SECOND,0);
        this.baseDate.set(Calendar.MILLISECOND,0);

        this.visibleRange = visibleRange;
        this.totalDays    = visibleRange * 2 + 1;
        this.startPosition= visibleRange;
    }

    /**
     * Inflates the date item view and creates a ViewHolder.
     */
    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a single date item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    /**
     * Binds the date data (day of week and day of month) to the ViewHolder,
     * and sets up click listener for smooth scrolling to that date.
     */
    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        // Compute offset in days from baseDate
        int offset = position - startPosition;
        Calendar calendar = (Calendar) baseDate.clone();
        calendar.add(Calendar.DAY_OF_YEAR, offset); // Move forward or backward by offset

        // Bind click listener
        holder.itemView.setOnClickListener(v -> {
            if (dateClickListener != null) {
                dateClickListener.onDateClick(calendar);
            }
        });

        // Format day of week and day of month
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
        String weekday = dfs.getShortWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)];
        String dayOfMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        holder.dayOfMonthText.setText(dayOfMonth);
        holder.dayOfWeekText.setText(weekday);

        // Highlight selection
        holder.itemView.setSelected(position == selectedPosition
        );
    }

    /**
     * Returns the total number of date items.
     */
    @Override
    public int getItemCount() {
        return totalDays;
    }

    /**
     * Returns the adapter position corresponding to {@code baseDate}.
     *
     * @return the start (center) position
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * Updates which position is marked as selected and refreshes the view.
     *
     * @param position the new selected adapter position
     */
    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    /**
     * Registers a listener to be notified when a date is clicked.
     *
     * @param listener the listener, or null to clear
     */
    public void setOnDateClickListener(OnDateClickListener listener) {
        this.dateClickListener = listener;
    }

    /**
     * ViewHolder for a single date item (day number and day of week).
     */
    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfMonthText;
        TextView dayOfWeekText;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfMonthText = itemView.findViewById(R.id.text_day);
            dayOfWeekText = itemView.findViewById(R.id.text_day_of_week);
        }
    }
}
