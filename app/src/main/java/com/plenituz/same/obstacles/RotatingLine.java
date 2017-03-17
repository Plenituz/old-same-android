package com.plenituz.same.obstacles;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.plenituz.same.interfaces.Powerable;
import com.plenituz.same.util.P;
import com.plenituz.same.util.TickableAnimator;

/**
 * Created by Plenituz on 27/07/2015 for Same.
 */
public class RotatingLine extends Obstacle {

    private Path path;
    private float[] originalPathPoints;
    private float[] pathPoints;
    private Matrix matrix;
    private Region clip;
    private float angle;
    private float startAngle;
    private int repeatCount;
    private int length;
    private int i;

    public RotatingLine(int color, float speed, int thickness, int x, int y, float startAngle, int length, int repeatCount, int startAt) {
        super(color, speed, thickness);
        this.x = x;
        this.y = y;
        this.repeatCount = repeatCount;
        this.startAngle = startAngle;
        this.length = (int) P.dp(length);
        angle = startAngle;
        if(startAt == START_AT_END) {
            this.speed *= -1;
            originalSpeed = speed;
        }
        addWarningBeforeAppear(1000);
    }

    @Override
    protected void doTick() {
        angle += speed;
        matrix.setRotate(angle, x, y);
        matrix.mapPoints(pathPoints, originalPathPoints);
        updatePath();
        region.setPath(path, clip);
        if(angle >= 360 + startAngle || angle < 0 - startAngle){
            angle = startAngle;
            repeatCount--;
        }
    }

    @Override
    protected void setupPaint(int thickness, int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setStrokeWidth(thickness / 4);
        paint.setColor(color);
        paint.setPathEffect(new DashPathEffect(new float[]{0, ((thickness*2) + (length*2))}, 0));
    }

    @Override
    protected void setupRegion() {
        path = new Path();
        matrix = new Matrix();
        region = new Region();
        clip = new Region(0, 0, P.poc(1.0f)[0], P.poc(1.1f)[1]);
        pathPoints = new float[8];
        originalPathPoints = new float[]{-((length /2) - x), y - (thickness / 2),
                x + (length /2), y - (thickness / 2),
                x + (length /2), y + (thickness / 2),
                -((length /2) - x), y + (thickness / 2)};
        matrix.setRotate(angle, x, y);
        matrix.mapPoints(pathPoints, originalPathPoints);
        updatePath();
    }

    @Override
    protected DrawProcess initDrawProcess() {
        return new DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawPath(path, paint);
            }
        };
    }

    @Override
    protected DrawProcess initWarningDrawProcess() {
        return new DrawProcess() {
            boolean b = true;
            @Override
            public void draw(Canvas canvas) {
                if(b){
                    b = false;
                    paint.setStyle(Paint.Style.STROKE);
                    TickableAnimator a  = new TickableAnimator(0, (thickness*2) + (length*2), warningDuration);
                    a.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
                        @Override
                        public void onUpdate(TickableAnimator animator) {
                            paint.setPathEffect(new DashPathEffect(new float[]{animator.getValue(), ((thickness*2) + (length*2)) - animator.getValue()}, 0));
                        }
                    });
                    a.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd() {
                            paint.setStyle(Paint.Style.FILL);
                            paint.setPathEffect(null);
                        }
                    });
                    a.setInterpolator(new AccelerateDecelerateInterpolator());
                    a.start();
                }
                canvas.drawPath(path, paint);

            }
        };
    }

    @Override
    public float getProgress() {
        return repeatCount == -1 ? 1.0f : 0;
    }

    @Override
    public void slow(float slowFactor) {
        if(!slowed[0]){
            slowed[0] = true;
            Powerable.slowFactor[0] = slowFactor;
        }
        speed *= slowFactor;
    }

    private void updatePath(){
        path.rewind();
        path.setLastPoint(pathPoints[0], pathPoints[1]);
        for(i = 2; i < pathPoints.length; i++){
            path.lineTo(pathPoints[i], pathPoints[++i]);
        }
        path.close();
    }
}
