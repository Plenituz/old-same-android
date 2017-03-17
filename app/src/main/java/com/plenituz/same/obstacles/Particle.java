package com.plenituz.same.obstacles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;

import com.plenituz.same.MainActivity;
import com.plenituz.same.util.P;

/**
 * Created by Plenituz on 01/08/2015 for Same.
 */
public class Particle extends Obstacle {

    private int endX;
    private int endY;
    private float shrinkSpeed;
    private float radius;
    private int offset;
    private Region clip;
    private Path path;
    private int type;

    private static double a;
    private static double[] inters;
    private static int[] convert = new int[4];
    private static int[] tmp = new int[2];
    private static double[] result = new double[4];

    public static final int TYPE_FOLLOWER = 1;
    public static final int TYPE_BOUNCER = 2;
    public static final int TYPE_STRAIGHT = 3;

    public Particle(int color, int speed, int thickness, int startX, int startY, int radius, float shrinkSpeed) {
        super(color, speed, thickness);
        this.x = startX;
        this.y = startY;
        this.radius = P.dp(radius);
        this.shrinkSpeed = P.dp(shrinkSpeed);
        type = TYPE_FOLLOWER;
    }

    public Particle(int color, int speed, int thickness, int startX, int startY, int endX, int endY, int radius){
        super(color, speed, thickness);
        this.x = startX;
        this.y = startY;
        this.endX = endX;
        this.endY = endY;
        this.radius = P.dp(radius);
        type = TYPE_STRAIGHT;
    }

    @Override
    protected void doTick() {
        switch (type){
            case TYPE_FOLLOWER:
                if(speed != 0){
                    tmp[0] = MainActivity.lastX.get();
                    tmp[1] = MainActivity.lastY.get();
                    tmp = getClosestPoint(getPointOnLineAtDistance(x, y, MainActivity.lastX.get(), MainActivity.lastY.get(), (int) speed), tmp);
                    x = tmp[0];
                    y = tmp[1];
                }
                radius -= shrinkSpeed;
                break;
            case TYPE_STRAIGHT:
                if(speed != 0){
                    tmp[0] = endX;
                    tmp[1] = endY;
                    tmp = getClosestPoint(getPointOnLineAtDistance(x, y, endX, endY, (int) speed), tmp);
                    x = tmp[0];
                    y = tmp[1];
                    if(P.getDistance(x, y, endX, endY) < 10){
                        radius = 0;
                    }
                }
                break;
        }

        offset = Math.round(radius + (thickness / 2));
        clip.set(x - offset, y - offset, x + offset, y + offset);
        path.rewind();
        path.addCircle(x, y, offset, Path.Direction.CW);//todo make it so the inside of the particle has no collision ?
        region.setPath(path, clip);
    }

    @Override
    protected void setupPaint(int thickness, int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
        paint.setColor(color);
    }

    @Override
    protected void setupRegion() {
        region = new Region();
        clip = new Region();
        path = new Path();
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
    public float getProgress() {
        return radius <= 0 ? 1:0.5f;
    }

//these methods use static fields because they are called in doTick(), therefore it's memory efficient to do so (avoid TONES of allocs)
    public static int[] getPointOnLineAtDistance(int x1, int y1, int x2, int y2, int r){
        a = P.getSlope(x1, y1, x2, y2);
        inters = getIntersectBetweenLineAndCircle(x1, y1, a, (int) (y1 - (a*x1)), r);
        convert[0] = (int) inters[0];
        convert[1] = (int) inters[1];
        convert[2] = (int) inters[2];
        convert[3] = (int) inters[3];
        return convert;
    }

    public static double[] getIntersectBetweenLineAndCircle(int x1, int y1, double a, int b, int r){
        result[0] = (-((2*a*b) - (2*x1) - (2*a*y1)) + Math.sqrt(Math.pow(((2 * a * b) - (2 * x1) - (2 * a * y1)), 2) - (4 * ((a * a) + 1) * ((b * b) + (x1 * x1) + (y1 * y1) - (2 * b * y1) - (r * r)))))/(2*((a*a) + 1));
        result[2] = (-((2*a*b) - (2*x1) - (2*a*y1)) - Math.sqrt(Math.pow(((2 * a * b) - (2 * x1) - (2 * a * y1)), 2) - (4 * ((a * a) + 1) * ((b * b) + (x1 * x1) + (y1 * y1) - (2 * b * y1) - (r * r)))))/(2*((a*a) + 1));

        result[1] = a*result[0] + b;
        result[3] = a*result[2] + b;
        return result;
    }

    public static int[] getClosestPoint(int[] pointsToDifferentiate, int[] anchor){
        if(P.getDistance(pointsToDifferentiate[0], pointsToDifferentiate[1], anchor[0], anchor[1]) >  P.getDistance(pointsToDifferentiate[2], pointsToDifferentiate[3], anchor[0], anchor[1])){
            tmp[0] = pointsToDifferentiate[2];
            tmp[1] = pointsToDifferentiate[3];
        }else{
            tmp[0] = pointsToDifferentiate[0];
            tmp[1] = pointsToDifferentiate[1];
        }
        return tmp;
    }
}
