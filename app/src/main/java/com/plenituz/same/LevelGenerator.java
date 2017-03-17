package com.plenituz.same;

import android.graphics.Color;

import com.plenituz.same.interfaces.Powerable;
import com.plenituz.same.interfaces.Tickable;
import com.plenituz.same.obstacles.BonusItem;
import com.plenituz.same.obstacles.Circle;
import com.plenituz.same.obstacles.Line;
import com.plenituz.same.obstacles.Obstacle;
import com.plenituz.same.obstacles.Particle;
import com.plenituz.same.obstacles.RotatingLine;
import com.plenituz.same.util.P;

import java.util.ArrayList;

/**
 * Created by Plenituz on 15/08/2015 for Same.
 */
public class LevelGenerator implements Tickable, Powerable {

    public static final int PATTERN_COUNT = 14;
    public static final int MAX_SPEED = 7;

    long lastGen = 0;
    int i;
    int random;
    int maxSpeed = 2;
    long startTime;
    ArrayList<Pattern> activePatterns = new ArrayList<>();
    ArrayList<Integer> dontGen = new ArrayList<>();
    private boolean isStopped = false;


    public LevelGenerator(){
        Tickable.tickables.add(this);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        if(isStopped)
            return;
        if(((System.currentTimeMillis() - lastGen) >= 3000) || ((System.currentTimeMillis() - lastGen) % P.random(1000, 3000) == 0)){
            generate();
        }

        if((System.currentTimeMillis() - startTime) % (10000 + P.random(1000, 10000)) == 0){
            int r = P.random(0, 3);
            new BonusItem(r == 0 ? BonusItem.BOOM : r == 1 ? BonusItem.SLOW : BonusItem.STOP_TIME, 0.3f, 0, P.random(P.poc(0.2f)[0], P.poc(0.8f)[0]), P.random(P.poc(0.1f)[1], P.poc(0.8f)[1]), 10, 5000);
        }

        if(maxSpeed < MAX_SPEED && System.currentTimeMillis() - startTime >= 10000){
            maxSpeed++;
            startTime = System.currentTimeMillis();
        }

        for(i = 0; i < activePatterns.size(); i++){
            activePatterns.get(i).tick();
            if(activePatterns.get(i).isDone)
                activePatterns.remove(i--);
        }
    }

    void generate(){
        random = P.random(0, PATTERN_COUNT);
        if(dontGen.contains(random)){
            generate();
        }else{
            activePatterns.add(new Pattern(random));
            lastGen = System.currentTimeMillis();
        }

    }

    @Override
    public float getProgress() {
        return 0;
    }

    @Override
    public void delete() {
        Tickable.tickables.remove(this);
    }

    @Override
    public void slow(float slowFactor) {
        //not slowable
    }

    @Override
    public void unSlow() {
        //not slowable
    }

    @Override
    public void stop() {
        isStopped = true;
    }

    @Override
    public void unStop() {
        isStopped = false;
    }

    private class Pattern{
        private final int id;
        public boolean isDone = false;
        private int wait = 0;
        private long startWait;
        private int cursor = 0;
        private int i;
        private int color, speed, thickness, startAt, from = startAt = thickness = speed = color = 0;
        float fSpeed;

        public Pattern(int id) {
            this.id = id;
        }

        void tick(){
            if(wait > 0){
                if(System.currentTimeMillis() - startWait >= wait)
                    wait = 0;
                return;
            }
            switch (id){
                case 0:
                    new Line(getNotSoRandomColor(), P.random(1, maxSpeed), P.random(5, 46), P.random(3, 5), P.random(1, 3));
                    isDone = true;
                    break;
                case 1:
                    color = getNotSoRandomColor();
                    speed = P.random(1, maxSpeed);
                    thickness = P.random(5, 46);
                    startAt = P.random(3, 5);
                    new Line(color, speed, thickness, startAt, Line.FROM_LEFT);
                    new Line(color, speed, thickness, startAt, Line.FROM_TOP);
                    isDone = true;
                    break;
                case 2:
                    new RotatingLine(getNotSoRandomColor(), P.fRandom(0.1f, 1),
                            P.random(3, 20), P.random(P.poc(0.2f)[0], P.poc(0.8f)[0]), P.random(P.poc(0.1f)[1],
                            P.poc(0.9f)[1]), 90, P.random(100, 501), P.random(0, 3), P.random(3, 5));
                    isDone = true;
                    break;
                case 3:
                    new Circle(getNotSoRandomColor(), P.fRandom(0.1f, 5),
                            P.random(5, 20), P.random(3, 5), P.random(P.poc(0.2f)[0], P.poc(0.8f)[0]),
                            P.random(P.poc(0.1f)[1], P.poc(0.9f)[1]));
                    isDone = true;
                    break;
                case 4:
                    switch (cursor){
                        case 0:
                            new Circle(Color.WHITE, 0.1f, 15, Obstacle.START_AT_BEGIN, 0, P.poc(0.5f)[1]);
                            new Circle(Color.BLACK, 0.1f, 15, Obstacle.START_AT_BEGIN, P.poc(1.0f)[0], P.poc(0.5f)[1]);
                            waitAndIncrementCursor(12000);
                            preventGenerationOf(4);
                            break;
                        case 1:
                            allowGenerationOf(4);
                            isDone = true;
                            break;
                    }
                    break;
                case 5:
                    switch (cursor){
                        case 0:
                            color = getNotSoRandomColor();
                            startAt = P.random(3, 5);
                            speed = P.random(1, maxSpeed);
                            thickness = P.random(5, 46);
                            from = P.random(1, 3);
                            new Line(color, speed, thickness, startAt, from);
                            waitAndIncrementCursor(2500);
                            break;
                        case 1:
                            new Line(color, speed, thickness, startAt, from);
                            isDone = true;
                            break;
                    }
                    break;
                case 6:
                    speed = P.random(1, maxSpeed);
                    thickness = P.random(5, 16);
                    color = P.random(15, 41);//re-using color as radius to not have a bajillion variable
                    new Particle(getNotSoRandomColor(), speed, thickness, 0, 0, P.poc(0.5f)[0], P.poc(0.5f)[1], color);
                    new Particle(getNotSoRandomColor(), speed, thickness, P.poc(1.0f)[0], 0, P.poc(0.5f)[0], P.poc(0.5f)[1], color);
                    new Particle(getNotSoRandomColor(), speed, thickness, P.poc(1.0f)[0], P.poc(1.0f)[1], P.poc(0.5f)[0], P.poc(0.5f)[1], color);
                    new Particle(getNotSoRandomColor(), speed, thickness, 0, P.poc(1.0f)[1], P.poc(0.5f)[0], P.poc(0.5f)[1], color);
                    isDone = true;
                    break;
                case 7:
                    speed = P.random(1, maxSpeed);
                    thickness = P.random(5, 16);
                    color = P.random(15, 41);//re-using color as radius to not have a bajillion variable
                    new Particle(getNotSoRandomColor(), speed, thickness, P.poc(0.5f)[0], 0, P.poc(0.5f)[0], P.poc(0.5f)[1], color);
                    new Particle(getNotSoRandomColor(), speed, thickness, P.poc(1.0f)[0], P.poc(0.5f)[1], P.poc(0.5f)[0], P.poc(0.5f)[1], color);
                    new Particle(getNotSoRandomColor(), speed, thickness, P.poc(0.5f)[0], P.poc(1.0f)[1], P.poc(0.5f)[0], P.poc(0.5f)[1], color);
                    new Particle(getNotSoRandomColor(), speed, thickness, 0, P.poc(0.5f)[1], P.poc(0.5f)[0], P.poc(0.5f)[1], color);
                    isDone = true;
                    break;
                case 8:
                    switch (cursor){
                        case 0:
                            new Particle(getNotSoRandomColor(), P.random(1, maxSpeed), P.random(5, 16), -100, -100, P.random(15, 41), P.fRandom(0, 0.1f));
                            waitAndIncrementCursor(20000);
                            preventGenerationOf(8);
                            break;
                        case 1:
                            allowGenerationOf(8);
                            isDone = true;
                            break;
                    }
                    break;
                case 9:
                    switch (cursor){
                        case 0:
                            new Particle(getNotSoRandomColor(), P.random(1, maxSpeed), P.random(5, 16), P.poc(1.1f)[0], P.poc(1.1f)[1], P.random(15, 41), P.fRandom(0, 0.1f));
                            waitAndIncrementCursor(20000);
                            preventGenerationOf(9);
                            break;
                        case 1:
                            allowGenerationOf(9);
                            isDone = true;
                            break;
                    }

                    break;
                case 10:
                    new Particle(getNotSoRandomColor(), P.random(1, maxSpeed), P.random(5, 16), -100, -100, P.random(15, 41), P.fRandom(0, 0.1f));
                    new Particle(getNotSoRandomColor(), P.random(1, maxSpeed), P.random(5, 16), P.poc(1.1f)[0], P.poc(1.1f)[1], P.random(15, 41), P.fRandom(0, 0.1f));
                    isDone = true;
                    break;
                case 11:
                    speed = P.random(1, maxSpeed);
                    thickness = P.random(5, 20);
                    new Circle(getNotSoRandomColor(), speed, thickness, P.random(3, 5), P.poc(0.2f)[0], P.poc(0.2f)[1]);
                    new Circle(getNotSoRandomColor(), speed, thickness, P.random(3, 5), P.poc(0.8f)[0], P.poc(0.8f)[1]);
                    isDone = true;
                    break;
                case 12:
                    speed = P.random(1, maxSpeed);
                    thickness = P.random(5, 20);
                    new Circle(getNotSoRandomColor(), speed, thickness, P.random(3, 5), P.poc(0.8f)[0], P.poc(0.2f)[1]);
                    new Circle(getNotSoRandomColor(), speed, thickness, P.random(3, 5), P.poc(0.2f)[0], P.poc(0.8f)[1]);
                    isDone = true;
                    break;
                case 13:
                    fSpeed = P.fRandom(0.1f, 1);
                    thickness = P.random(3, 20);
                    color = P.random(100, 501);
                    from = P.random(0, 6);
                    new RotatingLine(getNotSoRandomColor(), fSpeed, thickness, P.poc(0.2f)[0], P.poc(0.5f)[1], 90, color, from, P.random(3, 5));
                    new RotatingLine(getNotSoRandomColor(), fSpeed, thickness, P.poc(0.8f)[0], P.poc(0.5f)[1], 90, color, from, P.random(3, 5));
                    isDone = true;
                    break;
            }
        }

        void preventGenerationOf(int id){
            if(!dontGen.contains(id))
                dontGen.add(id);
        }

        void allowGenerationOf(int id){
            for(i = 0; i < dontGen.size(); i++){
                if(dontGen.get(i) == id){
                    dontGen.remove(i);
                    break;
                }
            }
        }

        void waitAndIncrementCursor(int time){
            startWait = System.currentTimeMillis();
            cursor++;
            wait = time;
        }

        /**
         * 0 means BLACK 1 means WHITE
         * ex : 010 means the last 3 colors were : black white black
         * that way there is never more than 3 times the same color in a row
         */
        int last3 = 0b010;
        int r;

        int getNotSoRandomColor(){
            if(last3 == 0){
                r = Color.WHITE;
            }else if(last3 == 0b111){
                r = Color.BLACK;
            }else{
                r = P.random(0, 2) == 0 ? Color.WHITE : Color.BLACK;
            }
            last3 <<= 1;
            last3 |= r == Color.WHITE ? 0b1 : 0b0;
            last3 &= 0b111;
            return r;
        }
    }
}
