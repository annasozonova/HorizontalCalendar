package com.sahana.horizontalcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * A View that draws a rounded-rectangle selection frame with
 * semi-transparent fill to highlight the currently selected date cell.
 */
public class SelectionFrameView extends View {
    /** Paint used for the semi-transparent fill of the frame. */
    private Paint fillPaint;
    /** Paint used for the border stroke of the frame. */
    private Paint borderPaint;
    /** Rectangle defining the drawing bounds for the frame. */
    private RectF rect;

    /**
     * Constructor for creating the view in code.
     * @param context the context to use
     */
    public SelectionFrameView(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor that is called when inflating from XML.
     * @param context the context to use
     * @param attrs the attribute set from XML
     */
    public SelectionFrameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor that is called when inflating from XML with a style.
     * @param context the context to use
     * @param attrs the attribute set from XML
     * @param defStyleAttr the default style attribute
     */
    public SelectionFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initializes paint objects and the rectangle used for drawing.
     */
    private void init() {
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        // Semi-transparent blue fill
        fillPaint.setColor(0x1A4285F4);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f);
        // Solid blue border
        borderPaint.setColor(0xFF4285F4);

        rect = new RectF();
    }

    /**
     * Draws the rounded rectangle frame and fill.
     * @param canvas the canvas on which to draw
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float halfStroke = borderPaint.getStrokeWidth() / 2f;
        // Inset the rect by half the stroke width so the border is fully visible
        rect.left   = halfStroke;
        rect.top    = halfStroke;
        rect.right  = getWidth()  - halfStroke;
        rect.bottom = getHeight() - halfStroke;

        float radius = 16f; // corner radius in pixels

        // Draw fill first, then border on top
        canvas.drawRoundRect(rect, radius, radius, fillPaint);
        canvas.drawRoundRect(rect, radius, radius, borderPaint);
    }
}

