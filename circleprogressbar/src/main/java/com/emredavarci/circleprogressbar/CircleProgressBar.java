package com.emredavarci.circleprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Minimal local implementation to satisfy compile-time usage in
 * android-json-form-wizard. It exposes the API used by the project
 * and renders a very basic circular progress indicator.
 */
public class CircleProgressBar extends View {

    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int strokeWidthPx = 20;
    private int maxValue = 100;
    private int progress = 0;
    private String text = "";

    public CircleProgressBar(Context context) {
        super(context);
        init(null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        backgroundPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.DKGRAY);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
            try {
                // Optional attrs; defaults are fine
                if (a.hasValue(R.styleable.CircleProgressBar_textSize)) {
                    float ts = a.getDimension(R.styleable.CircleProgressBar_textSize, 50f);
                    textPaint.setTextSize(ts);
                }
            } finally {
                a.recycle();
            }
        }

        setStrokeWidthDimension(strokeWidthPx);
        setBackgroundColor("#B6BBBE");
        setProgressColor("#535F67");
        setTextColor("#535F67");
    }

    // API used by project code
    public void setStrokeWidthDimension(int width) {
        this.strokeWidthPx = width;
        backgroundPaint.setStrokeWidth(strokeWidthPx);
        progressPaint.setStrokeWidth(strokeWidthPx);
        invalidate();
    }

    public void setMaxValue(int max) {
        this.maxValue = Math.max(1, max);
        invalidate();
    }

    // Overload distinct from View#setBackgroundColor(int)
    public void setBackgroundColor(String colorHex) {
        try {
            backgroundPaint.setColor(Color.parseColor(colorHex));
        } catch (Exception e) {
            backgroundPaint.setColor(Color.LTGRAY);
        }
        invalidate();
    }

    public void setProgressColor(String colorHex) {
        try {
            progressPaint.setColor(Color.parseColor(colorHex));
        } catch (Exception e) {
            progressPaint.setColor(Color.DKGRAY);
        }
        invalidate();
    }

    public void setTextColor(String colorHex) {
        try {
            textPaint.setColor(Color.parseColor(colorHex));
        } catch (Exception e) {
            textPaint.setColor(Color.DKGRAY);
        }
        invalidate();
    }

    public void setText(String text) {
        this.text = text != null ? text : "";
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, maxValue));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h);
        int radius = (size - strokeWidthPx) / 2;
        int cx = w / 2;
        int cy = h / 2;

        // Background circle
        canvas.drawCircle(cx, cy, radius, backgroundPaint);

        // Progress arc
        float sweep = 360f * progress / Math.max(1, maxValue);
        // Draw arc using stroke: use drawArc with oval bounds
        float left = cx - radius;
        float top = cy - radius;
        float right = cx + radius;
        float bottom = cy + radius;
        canvas.drawArc(left, top, right, bottom, -90f, sweep, false, progressPaint);

        // Center text
        if (!text.isEmpty()) {
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float textY = cy - (fm.ascent + fm.descent) / 2;
            canvas.drawText(text, cx, textY, textPaint);
        }
    }
}

