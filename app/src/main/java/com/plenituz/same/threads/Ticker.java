package com.plenituz.same.threads;

import com.plenituz.same.interfaces.Tickable;

/**
 * Created by Plenituz on 31/07/2015 for Same.
 */
public class Ticker extends Thread {
    Thread t;
    int i;
    boolean isRunning = true;
    public static int FPS = 60;

    long startTime = 0;
    int count;
    boolean reset = true;

    long start;
    long wait;
    long diff;

    @Override
    public void run() {
        start = System.currentTimeMillis();
        wait = 1000/FPS;
        while(isRunning){
            try{
                for(i = 0; i < Tickable.tickables.size(); i++){
                    Tickable.tickables.get(i).tick();
                    if(Tickable.tickables.get(i).getProgress() >= 1.0f){
                        Tickable.tickables.get(i).delete();
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

            if(reset){
                startTime = System.currentTimeMillis();
                count = 0;
                reset = false;
            }
            count++;
            if(System.currentTimeMillis() - startTime >= 1000){
                BigSurface.lastestTickerFrameRate = count;
                reset = true;
            }
        }
    }

    @Override
    public synchronized void start() {
        if(t == null){
            t = new Thread(this, "Ticker");
            t.start();
        }
    }
}
