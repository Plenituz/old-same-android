package com.plenituz.same.obstacles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;

import com.plenituz.same.util.P;

/**
 * Created by Plenituz on 25/07/2015.
 */
public class Line extends Obstacle {

    public static final int FROM_TOP = 1;
    public static final int FROM_LEFT = 2;
    private final int startAt;

    private final int from;
    private Rect rect;

    public Line(int color, int speed, int thickness, int startAt, int from){
        super(color, speed, thickness);
        this.from = from;
        this.startAt = startAt;
    }

    @Override
    public void doTick() {
        rect.offset(from == FROM_TOP ? 0 : (int) speed, from == FROM_TOP ? (int) speed : 0);
        region.set(rect);
    }

    @Override
    protected void setupPaint(int thickness, int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

    @Override
    protected void setupRegion() {
        rect = new Rect(0, 0, (from == FROM_TOP) ? P.poc(1.0f)[0] : getThickness(), (from == FROM_TOP) ? getThickness() : P.poc(1.1f)[1]);
        if(startAt == START_AT_BEGIN){
            rect.offsetTo(from == FROM_TOP ? 0 : -getThickness(), from == FROM_TOP ? -getThickness() : 0);
        }else{
            this.speed *= -1;
            originalSpeed = speed;
            rect.offsetTo(from == FROM_TOP ? 0 : P.poc(1.0f)[0], from == FROM_TOP ? P.poc(1.0f)[1] + getThickness() : 0);
        }
        region = new Region();
    }

    @Override
    protected DrawProcess initDrawProcess() {
        return new DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawRect(rect, paint);
            }
        };
    }

    @Override
    public float getProgress() {
        if(from == FROM_TOP)
            return startAt == START_AT_BEGIN ? Math.abs((float) rect.top / P.poc(1.1f)[1]) : (P.poc(1.1f)[1] - (rect.top + thickness) ) / P.poc(1.1f)[1];
        else
            return startAt == START_AT_BEGIN ? Math.abs((float) rect.left / P.poc(1.1f)[0]) : (P.poc(1.1f)[0] - (rect.left + thickness) ) / P.poc(1.1f)[0];
    }
}
