package com.plenituz.same.threads;

import android.graphics.Region;

import com.plenituz.same.MainActivity;
import com.plenituz.same.interfaces.Tickable;
import com.plenituz.same.obstacles.Obstacle;

/**
 * Created by Plenituz on 31/07/2015 for Same.
 */
public class Collider extends Thread {
    private static final int FPS = 60;
    Thread t;
    int i;
    boolean isRunning = true;
    private boolean reset = true;
    private long startTime;
    private int count;
    private long wait;
    private long diff;
    private long start;

    @Override
    public void run() {
        start = System.currentTimeMillis();
        wait = 1000/FPS;
        while (isRunning){
            try{
                for (i = 0; i < Obstacle.regions.size(); i++){
                    //player score is always -1 and other obstacles can't have -1
                    if(!(Obstacle.colorsAndScores.get(i).second == -1) && Obstacle.colorsAndScores.get(i).first != MainActivity.activePlayer.getColor() && Obstacle.regions.get(i).op(MainActivity.activePlayer.getRegion(), Region.Op.INTERSECT)) {
                        Obstacle o = findObstacleWithThatRegion(Obstacle.regions.get(i));
                        if(o != null && !MainActivity.activePlayer.isDead) {
                            o.collide(MainActivity.activePlayer);
                            MainActivity.activePlayer.collide(o);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            diff = System.currentTimeMillis() - start;
            if(diff < wait){
                try {
                    Thread.sleep(wait - diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            start = System.currentTimeMillis();
            //todo optimise variable usage here and in ticker and big surface
            if(reset){
                startTime = System.currentTimeMillis();
                count = 0;
                reset = false;
            }
            count++;
            if(System.currentTimeMillis() - startTime >= 1000){
                //Log.v("collider", count + "");
                BigSurface.lastestColliderFrameRate = count;
                reset = true;
            }
        }
    }

    private Obstacle findObstacleWithThatRegion(Region region) {
        for(int i = 0; i < Tickable.tickables.size(); i++){
            if(Tickable.tickables.get(i) instanceof Obstacle){
                if(((Obstacle) Tickable.tickables.get(i)).getRegion() == region)
                    return (Obstacle) Tickable.tickables.get(i);
            }
        }
        return null;
    }

    @Override
    public synchronized void start() {
        if(t == null){
            t = new Thread(this, "Collision");
            t.start();
        }
    }
}
