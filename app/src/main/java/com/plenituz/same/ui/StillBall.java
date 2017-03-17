package com.plenituz.same.ui;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.plenituz.same.obstacles.Obstacle;
import com.plenituz.same.util.P;
import com.plenituz.same.util.TickableAnimator;

/**
 * Created by Plenituz on 27/09/2015 for Same.
 */
public class StillBall{

    private int radius;
    final Obstacle.DrawProcess drawProcess;

    public StillBall(final int x, final int y, int duration, int color, TimeInterpolator interpolator) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        drawProcess = new Obstacle.DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawCircle(x, y, radius, paint);
            }
        };
        Obstacle.drawProcesses.add(drawProcess);
        TickableAnimator anim = new TickableAnimator((int) P.dp(22), 0, duration);
        anim.setInterpolator(interpolator);
        anim.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
            @Override
            public void onUpdate(TickableAnimator animator) {
                radius = animator.getValue();
            }
        });
        anim.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                delete();
            }
        });
        anim.start();
    }

    public void delete() {
        Obstacle.drawProcesses.remove(drawProcess);
    }
}
