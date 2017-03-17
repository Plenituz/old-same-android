package com.plenituz.same.obstacles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.plenituz.same.LevelGenerator;
import com.plenituz.same.MainActivity;
import com.plenituz.same.interfaces.Powerable;
import com.plenituz.same.interfaces.Tickable;
import com.plenituz.same.util.P;
import com.plenituz.same.util.TickableAnimator;

import java.util.ArrayList;

/**
 * Created by Plenituz on 06/08/2015 for Same.
 */
public class BonusItem extends Obstacle {

    private final int deadTime;
    private final long startTime;
    private Region clip;
    private Path path;
    private float originalRadius;
    private float radius;

    public static final int BOOM = Color.RED;
    public static final int SLOW = Color.BLUE;
    public static final int STOP_TIME = Color.GREEN;


    /**
     * When you pick up an item that gives you a one time usage power
     * @param color determines what power it is (must be, BOOM, SLOW...)
     * @param speed determines the pulsating animation speed
     * @param thickness not used
     * @param x posX of the item
     * @param y posY of the item
     * @param radius of the ball
     * @param time how long it should stay
     */
    public BonusItem(int color, float speed, int thickness, int x, int y, float radius, int time) {
        super(color, speed, thickness);
        this.x = x;
        this.y = y;
        this.radius = P.dp(radius);
        this.deadTime = time;
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void doTick() {
        path.rewind();
        path.addCircle(x, y, radius, Path.Direction.CW);
        clip.set((int) (x - radius), (int) (y - radius), (int) (x + radius), (int) (y + radius));
        region.setPath(path, clip);
        radius += speed;
        if(radius >= originalRadius *1.3f || radius <= originalRadius *0.8f){
            speed *= -1;
        }
    }

    @Override
    protected void setupPaint(int thickness, int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void setupRegion() {
        region = new Region();
        clip = new Region();
        path = new Path();
        originalRadius = radius;
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
    public float getProgress() {
        return System.currentTimeMillis() - startTime >= deadTime ? 1 : 0.5f;
    }

    @Override
    public void collide(Obstacle obstacle) {
        super.collide(obstacle);
        switch (color){
            case BOOM:
                ArrayList<Integer> del = new ArrayList<>();
                for(int i = 0; i < Tickable.tickables.size(); i++){
                    if(Tickable.tickables.get(i) instanceof Obstacle && !(Tickable.tickables.get(i) instanceof Player) && !(Tickable.tickables.get(i) instanceof LevelGenerator)
                            && !(Tickable.tickables.get(i) instanceof TickableAnimator)){
                        del.add(i);//if i delete the obstacle here it doesn't work
                        //since Tickable.tickables get shorter automatically and some are skipped
                    }
                }
                if(del.size() > 0){
                    for(int i = del.size()-1; i >= 0; i--){
                        Tickable.tickables.get(del.get(i)).delete();
                    }
                }

                paint.setAlpha(120);
                final int[] size = new int[]{0};
                final DrawProcess d = new DrawProcess() {
                    @Override
                    public void draw(Canvas canvas) {
                        canvas.drawCircle(MainActivity.activePlayer.getX(), MainActivity.activePlayer.getY(), size[0], paint);
                    }
                };
                drawProcesses.add(d);

                TickableAnimator animBoom = new TickableAnimator(0, P.poc(2.0f)[1], 800);
                animBoom.setInterpolator(new AccelerateDecelerateInterpolator());
                animBoom.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        TickableAnimator animBoomAlpha = new TickableAnimator(120, 0, 400);
                        animBoomAlpha.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                drawProcesses.remove(d);
                            }
                        });
                        animBoomAlpha.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
                            @Override
                            public void onUpdate(TickableAnimator animator) {
                                paint.setAlpha(animator.getValue());
                            }
                        });
                        animBoomAlpha.start();
                    }
                });
                animBoom.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
                    @Override
                    public void onUpdate(TickableAnimator animator) {
                        size[0] = animator.getValue();
                    }
                });
                animBoom.start();

                break;
            case SLOW:
                paint.setAlpha(50);
                final DrawProcess dd = new DrawProcess() {
                    @Override
                    public void draw(Canvas canvas) {
                        canvas.drawRect(0, 0, P.poc(1.0f)[0], P.poc(1.1f)[1], paint);
                    }
                };
                drawProcesses.add(dd);
                try{
                    for(int i = 0; i < Tickable.tickables.size(); i++){
                        if(Tickable.tickables.get(i) instanceof Powerable)
                            ((Powerable) Tickable.tickables.get(i)).slow(0.3f);
                    }
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
                TickableAnimator a = new TickableAnimator(0, 1, 2000);
                a.setInterpolator(new LinearInterpolator());
                a.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        Powerable.slowed[0] = false;
                        for (int i = 0; i < Tickable.tickables.size(); i++) {
                            if (Tickable.tickables.get(i) instanceof Powerable)
                                ((Powerable) Tickable.tickables.get(i)).unSlow();
                        }
                        drawProcesses.remove(dd);
                    }
                });
                a.start();
                break;
            case STOP_TIME:
                new Thread(){
                    @Override
                    public void run() {
                        for(int i = 0; i < Tickable.tickables.size(); i++){
                            if(Tickable.tickables.get(i) instanceof Powerable)
                                ((Powerable) Tickable.tickables.get(i)).stop();
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for(int i = 0; i < Tickable.tickables.size(); i++){
                            if(Tickable.tickables.get(i) instanceof Powerable)
                                ((Powerable) Tickable.tickables.get(i)).unStop();
                        }
                    }
                }.start();
                break;
        }
    }
}
