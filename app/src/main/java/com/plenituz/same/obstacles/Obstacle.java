package com.plenituz.same.obstacles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.util.Pair;

import com.plenituz.same.interfaces.Powerable;
import com.plenituz.same.interfaces.Tickable;
import com.plenituz.same.util.P;
import com.plenituz.same.util.TickableAnimator;

import java.util.ArrayList;

/**
 * Created by Plenituz on 25/07/2015.
 */
public abstract class Obstacle implements Tickable, Powerable{

    public static final int START_AT_BEGIN = 3;
    public static final int START_AT_END = 4;

    /**
     * in dp/tick
     */
    protected float speed;
    protected float originalSpeed;
    /**
     * in dp
     */
    protected int thickness;
    /**
     * Gets set in {@link #setupPaint(int, int)}
     */
    Paint paint;
    /**
     * Gets set in {@link #setupRegion()} and has to be updated in {@link #doTick()}
     */
    protected Region region;
    protected int color;
    protected int x = 0;
    protected int y = 0;
    protected boolean warning = false;
    protected int warningDuration;
    protected DrawProcess drawProcess;
    protected Pair<Integer, Integer> colorAndScore;
    /**
     * This list gets updated when you create an Obstacle of any kind, the rendering thread
     * directly reads it from here. Meaning you can add anything to this list and it will get drawn
     */
    public static ArrayList<DrawProcess> drawProcesses = new ArrayList<>();
    /**
     * This list gets updated when you create an Obstacle of any kind, the collision thread
     * directly reads it from here
     */
    public static ArrayList<Region> regions = new ArrayList<>();
    /**
     * This list gets updated when you create an Obstacle of any kind, the collision thread
     * directly reads it from here
     * Used by the collision thread to make sure you only get killed by the obstacles of another color
     */
    public static ArrayList<Pair<Integer, Integer>> colorsAndScores = new ArrayList<>();

    /**
     * prepare any Obstacle by adding the different variables to the different lists
     * <b>DON'T INIT REGION OR ANYTHING HERE, JUST SAVE VALUES AND INIT IN {@link #setupRegion()}</b>
     * @see #drawProcesses
     * @see #regions
     * @see #colorsAndScores
     * @param color the color of the obstacle, <b>used for display AND collision</b>
     * @param speed the speed (px/tick) of the Obstacle, if it doesn't have one pass 0
     * @param thickness Thickness or any value that could help setup the paint drawing this Obstacle
     *                  since it's directly passed to {@link #setupPaint(int, int)} after being
     *                  converted to DIP unit
     */
    public Obstacle(int color, float speed, int thickness){
        this.originalSpeed = P.dp(speed);
        this.speed = stopped[0] ? 0 : slowed[0] ? this.originalSpeed*slowFactor[0] : this.originalSpeed;
        this.thickness = (int) P.dp(thickness);
        this.color = color;
        colorAndScore = new Pair<>(color, getScore());
        colorsAndScores.add(colorAndScore);
        Tickable.tickables.add(this);
        setupPaint(this.thickness, color);
        drawProcess = initDrawProcess();
        drawProcesses.add(drawProcess);
    }


    /**
     * Actual tick method for child classes, the {@link #tick()} method is implemented by Obstacle
     * Don't forget to :
     *  - Update the region
     *  - Update rendering variables
     */
    protected abstract void doTick();

    /**
     * Method called by the ticker thread, a lot of time per seconds
     */
    @Override
    public void tick() {
        //region as to be set on first tick since generally you need thickness or other params given in the constructor
        if(region == null) {
            setupRegion();
            regions.add(region);
        }
        if(warning){
            warning = false;
            drawProcesses.remove(drawProcess);
            drawProcess = initWarningDrawProcess();
            drawProcesses.add(drawProcess);
            tickables.remove(this);
            TickableAnimator waiter = new TickableAnimator(0, 1, warningDuration);
            waiter.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd() {
                    drawProcesses.remove(drawProcess);
                    drawProcess = initDrawProcess();
                    drawProcesses.add(drawProcess);
                    tickables.add(Obstacle.this);
                }
            });
            waiter.start();
            return;
        }
        //then relay to child classes
        doTick();
    }

    /**
     * Setup the paint to draw this Obstacle, called on constructor
     * @param thickness thickness given by the constructor
     * @param color color given by the constructor
     */
    protected abstract void setupPaint(int thickness, int color);

    /**
     * Setup the region for collision, called on first tick. Which means not on the main thread
     */
    protected abstract void setupRegion();

    /**
     * Setup the draw process of this Obstacle, called on constructor
     * @return The said draw process
     */
    protected abstract DrawProcess initDrawProcess();

    protected void addWarningBeforeAppear(int duration){
        warning = true;
        warningDuration = duration;
    }

    protected DrawProcess initWarningDrawProcess(){
        return null;
    }

    /**
     * since score is not stored in Obstacle, score calculation happens here
     * Override that to modify the default value
     * @return the score gained/lost on Obstacle end/collision
     */
    public int getScore() {
        return (int) (thickness*speed);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getThickness() {
        return thickness;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor(){
        return color;
    }

    public Region getRegion(){
        return region;
    }

    public void collide(Obstacle obstacle){
        //do something on collide
        delete();
    }

    /**
     * Call that to get ride of the obstacle cleanly, part of Tickable interface
     */
    @Override
    public void delete(){
        Tickable.tickables.remove(this);
        drawProcesses.remove(drawProcess);
        colorsAndScores.remove(colorAndScore);
        regions.remove(region);
    }

    public interface DrawProcess{
        void draw(Canvas canvas);
    }

    @Override
    public void slow(float slowFactor) {
        if(!slowed[0]){
            slowed[0] = true;
            Powerable.slowFactor[0] = slowFactor;
        }
        speed *= (speed * slowFactor < 1 ? 1 : slowFactor);
    }

    @Override
    public void unSlow() {
        if(slowed[0])
            slowed[0] = false;
        speed = originalSpeed;
    }

    @Override
    public void stop() {
        if(!stopped[0])
            stopped[0] = true;
        speed = 0;
    }

    @Override
    public void unStop() {
        if(stopped[0])
            stopped[0] = false;
        speed = originalSpeed;
    }
}
