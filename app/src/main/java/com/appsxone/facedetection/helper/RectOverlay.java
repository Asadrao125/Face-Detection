package com.appsxone.facedetection.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectOverlay extends GraphicOverlay.Graphic {

    private int mRectColor = Color.GREEN;
    private float mStrokeWidth = 0.4f;
    private Paint mRectPaint;
    private GraphicOverlay graphicOverlay;
    private Rect mRect;

    public RectOverlay(GraphicOverlay overlay, Rect rect) {
        super(overlay);

        mRectPaint = new Paint();
        mRectPaint.setColor(mRectColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(mStrokeWidth);

        this.graphicOverlay = overlay;
        this.mRect = rect;

    }

    @Override
    public void draw(Canvas canvas) {
        RectF rectF = new RectF(mRect);
        rectF.left = translateX(rectF.left);
        rectF.right = translateX(rectF.right);
        rectF.top = translateX(rectF.top);
        rectF.bottom = translateX(rectF.bottom);

        canvas.drawRect(rectF, mRectPaint);
    }
}