package com.plenituz.same.obstacles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.plenituz.same.MainActivity;
import com.plenituz.same.ui.StillBall;
import com.plenituz.same.util.P;

/**
 * Created by Plenituz on 26/07/2015 for Same.
 */
public class Player extends Obstacle {

    private Path path;
    private Region clip;
    private boolean animateTail;
    public boolean isDead = false;

    public Player(int color, int speed, int thickness, boolean animateTail) {
        super(color, speed, thickness);
        this.thickness = (int) P.dp(thickness);
        this.animateTail = animateTail;
        MainActivity.lastX.set(P.poc(0.5f)[0]);
        MainActivity.lastY.set(P.poc(0.8f)[1]);
        MainActivity.changePos.set(true);
    }

    @Override
    protected void setupPaint(int thickness, int color ) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void setupRegion() {
        region = new Region();
        path = new Path();
        clip = new Region();
    }

    @Override
    protected DrawProcess initDrawProcess() {
        return new DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawCircle(x, y, thickness, paint);
            }
        };
    }

    @Override
    public void doTick() {
        if(!MainActivity.changePos.get())
            return;
        x = MainActivity.lastX.get();
        y = MainActivity.lastY.get();
        if(MainActivity.changeColor){
            paint.setColor(paint.getColor() == Color.BLACK ? Color.WHITE : Color.BLACK);
            color = paint.getColor();
            MainActivity.changeColor = false;
        }
        path.rewind();
        path.addCircle(x, y, thickness, Path.Direction.CW);
        clip.set(x - thickness, y - thickness, x + thickness, y + thickness);
        region.setPath(path, clip);
        MainActivity.changePos.set(false);

        if(animateTail)
            new StillBall(x, y, 400, color, new AccelerateDecelerateInterpolator());
        //Todo arraylist of path

    }

    @Override
    public void collide(Obstacle obstacle) {
        if(!(obstacle instanceof BonusItem)){
            delete();
            isDead = true;
            MainActivity.menu();
        }
    }

    @Override
    public int getScore() {
        return -1;
    }

    @Override
    public float getProgress() {
        return -1;
    }

}
