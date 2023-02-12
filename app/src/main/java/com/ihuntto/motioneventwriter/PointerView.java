package com.ihuntto.motioneventwriter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class PointerView extends View {
    private MotionEventWriter mWriter;
    private MotionEvent mEvent;
    private Paint mPaint;

    public PointerView(Context context) {
        super(context);
        init();
    }

    public PointerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PointerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mWriter = new MotionEventWriter(getContext().getExternalCacheDir().getPath(), "touchdata");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mWriter.write(event);
        mEvent = MotionEvent.obtain(event);
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mEvent == null) return;
        MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
        for (int i = 0; i < mEvent.getPointerCount(); i++) {
            mEvent.getPointerCoords(i, pointerCoords);
            drawPointerCoords(canvas, pointerCoords);
        }
    }

    private void drawPointerCoords(Canvas canvas, MotionEvent.PointerCoords pointerCoords) {
        canvas.drawCircle(pointerCoords.x, pointerCoords.y, (pointerCoords.touchMajor + pointerCoords.touchMinor) / 4, mPaint);
    }
}
