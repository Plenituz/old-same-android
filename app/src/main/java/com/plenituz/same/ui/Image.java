package com.plenituz.same.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;

import com.plenituz.same.interfaces.Clickable;
import com.plenituz.same.obstacles.Obstacle;

/**
 * Created by Plenituz on 27/09/2015 for Same.
 */
public class Image implements Clickable {

    private Bitmap bitmap;
    private Region region;
    private Rect originalRect;
    public Rect dstRect;
    private Paint paint;
    private OnClickListener onClickListener;
    private Obstacle.DrawProcess drawProcess;

    public Image(Bitmap bm, Rect originalRect, Rect dstRect){
        bitmap = bm;
        this.originalRect = originalRect;
        this.dstRect = dstRect;
        region = new Region(dstRect);
        drawProcess = getDrawProcess();
        Obstacle.drawProcesses.add(drawProcess);
        Clickable.clickables.add(this);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
    }

    public Obstacle.DrawProcess getDrawProcess(){
        return new Obstacle.DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawBitmap(bitmap, originalRect, dstRect, paint);
            }
        };
    }

    @Override
    public void onClick() {
        if(onClickListener != null)
            onClickListener.onClick();
    }

    @Override
    public Region getRegion() {
        return region;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void delete(){
        Obstacle.drawProcesses.remove(drawProcess);
        Clickable.clickables.remove(this);
    }
}
