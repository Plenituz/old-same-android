package com.plenituz.same.obstacles;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.plenituz.same.util.P;
import com.plenituz.same.util.TickableAnimator;

/**
 * Created by Plenituz on 25/07/2015.
 */
public class Circle extends Obstacle{

    private final int startAt;
    private float radius = 0;
    private Path path;
    private Region clip;

    public Circle(int color, float speed, int thickness, int startAt, int x, int y) {
        super(color, speed, thickness);
        this.x = x;
        this.y = y;
        this.startAt = startAt;
        if(startAt == START_AT_BEGIN);
            addWarningBeforeAppear(1000);
    }

    @Override
    public void doTick() {
        radius += speed;
        path.rewind();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.addCircle(x, y, radius + (thickness/2), Path.Direction.CW);
        path.addCircle(x, y, radius - (thickness/2), Path.Direction.CW);
        clip.set((int) (x - radius), (int) (y - radius), (int) (x + radius), (int) (y + radius));
        region.setPath(path, clip);
    }

    @Override
    protected void setupPaint(int thickness, int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{0, (int) (2*Math.PI*radius)}, 0));
        paint.setStrokeWidth(thickness);
    }

    @Override
    protected void setupRegion() {
        if(startAt == START_AT_END) {
            radius = getFarestCornerDistance();
            this.speed *= -1;
        }
        path = new Path();
        region = new Region();
        clip = new Region();
    }

    @Override
    protected DrawProcess initDrawProcess() {
        return new DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawCircle(x, y, radius, paint);
            }
        };
    }

    @Override
    protected DrawProcess initWarningDrawProcess() {
        return new DrawProcess() {
            boolean b = true;
            Path p;
            @Override
            public void draw(Canvas canvas) {
                if(b){
                    b = false;
                    p = new Path();
                    p.addCircle(x, y, thickness * 3, Path.Direction.CCW);
                    TickableAnimator a = new TickableAnimator(0, (int) (Math.PI*radius/2), warningDuration);
                    a.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
                        @Override
                        public void onUpdate(TickableAnimator animator) {
                            paint.setPathEffect(new DashPathEffect(new float[]{animator.getValue(), (float) ((Math.PI*radius/2) - animator.getValue())}, 0));
                        }
                    });
                    a.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd() {
                            paint.setPathEffect(null);
                            p = null;
                        }
                    });
                    a.setInterpolator(new AccelerateDecelerateInterpolator());
                    a.start();
                }
                canvas.drawPath(p, paint);
            }
        };
    }

    @Override
    public float getProgress() {
        if(startAt == START_AT_BEGIN)
            return Math.abs(radius / getFarestCornerDistance());
        else
            return (getFarestCornerDistance() - radius) / getFarestCornerDistance();
    }

    private int getFarestCornerDistance() {
        return Math.max(Math.max(P.getDistance(x, y, 0, 0), P.getDistance(x, y, P.poc(1.0f)[0], 0)), Math.max(P.getDistance(x, y, 0, P.poc(1.0f)[1]), P.getDistance(x, y, P.poc(1.0f)[0], P.poc(1.0f)[1])));
    }
}
