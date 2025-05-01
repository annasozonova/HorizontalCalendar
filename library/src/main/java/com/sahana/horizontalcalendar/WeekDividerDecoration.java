package com.sahana.horizontalcalendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

/**
 * ItemDecoration that draws vertical dividers at the end of each week.
 * <p>
 * A divider is drawn after Sunday if weeks start on Monday, or after Saturday if weeks start on Sunday.
 */
public class WeekDividerDecoration extends RecyclerView.ItemDecoration {

    /** Paint used to draw the dividers. */
    private final Paint paint;

    /**
     * Whether the week starts on Monday.
     * If true, dividers are drawn after Sunday; if false, after Saturday.
     */
    private final boolean weekStartsOnMonday;

    /**
     * Adapter position corresponding to the base date (zero offset).
     * Used to calculate the date for each adapter position.
     */
    private final int startPosition;

    /**
     * Constructs a WeekDividerDecoration.
     *
     * @param weekStartsOnMonday true if week starts Monday (divider after Sunday),
     *                           false if week starts Sunday (divider after Saturday)
     * @param startPosition      adapter position corresponding to the base date
     */
    public WeekDividerDecoration(boolean weekStartsOnMonday, int startPosition) {
        this.weekStartsOnMonday = weekStartsOnMonday;
        this.startPosition = startPosition;
        paint = new Paint();
        paint.setColor(0xFFCCCCCC);
        paint.setStrokeWidth(2f);
    }

    /**
     * Draws vertical dividers at week boundaries over the RecyclerView items.
     *
     * @param canvas the Canvas to draw into
     * @param parent the RecyclerView onto which this decoration is applied
     * @param state  the current RecyclerView.State
     */
    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);

            if (position == RecyclerView.NO_POSITION) {
                continue;
            }

            // Compute the date for this position
            Calendar calendar = Calendar.getInstance();
            int dayOffset = position - startPosition;
            calendar.add(Calendar.DAY_OF_YEAR, dayOffset);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            boolean isWeekBoundary = weekStartsOnMonday
                    ? (dayOfWeek == Calendar.SUNDAY)
                    : (dayOfWeek == Calendar.SATURDAY);

            if (isWeekBoundary) {
                float x = child.getRight();
                canvas.drawLine(x, child.getTop(), x, child.getBottom(), paint);
            }
        }
    }

    /**
     * No additional offsets; dividers are drawn on top of item views.
     *
     * @param outRect the output rectangle to receive offsets
     * @param view    the child view to decorate
     * @param parent  the RecyclerView
     * @param state   the current RecyclerView.State
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // Intentionally empty; we do not need to adjust item positions.
    }
}
