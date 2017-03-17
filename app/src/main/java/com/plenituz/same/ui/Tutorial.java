package com.plenituz.same.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.plenituz.same.MainActivity;
import com.plenituz.same.R;
import com.plenituz.same.interfaces.Tickable;
import com.plenituz.same.obstacles.Circle;
import com.plenituz.same.obstacles.Obstacle;
import com.plenituz.same.obstacles.PlayerCallback;
import com.plenituz.same.util.P;
import com.plenituz.same.util.TickableAnimator;

import java.util.ArrayList;

/**
 * Created by Plenituz on 14/11/2015 for Same.
 */
public class Tutorial {

    private ArrayList<Obstacle.DrawProcess> backupDraw = new ArrayList<>();
    private ArrayList<Tickable> backupTickable = new ArrayList<>();
    private Context context;
    private Paint paintText;
    private PlayerCallback testPlayer;

    private ArrayList<Obstacle.DrawProcess> clearMe = new ArrayList<>();

    public void start(final Context context){
        backupDrawProcesses();
        backupTickables();
        Tickable.tickables.clear();
        Obstacle.drawProcesses.clear();
        this.context = context;

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(P.poc(0.05f)[0]);

        testPlayer = new PlayerCallback(Color.BLACK, 0, 22, MainActivity.setting.getBoolean("tail", true));
        moveTuto();
        new BounceInterpolator()


//        restoreDrawProcesses();todo dont forget that
//        restoreTickables();


    }

    private void moveTuto(){
        Obstacle.DrawProcess textMove = new Obstacle.DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawText(context.getString(R.string.moveTuto), P.poc(0.1f)[0], P.poc(0.5f)[1], paintText);
                canvas.drawText(context.getString(R.string.moveTuto2), P.poc(0.1f)[0], P.poc(0.54f)[1], paintText);
            }
        };
        Obstacle.drawProcesses.add(textMove);
        clearMe.add(textMove);
        TickableAnimator wait = new TickableAnimator(0, 1, 100);
        wait.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                testPlayer.setOnMove(new Runnable() {
                    @Override
                    public void run() {
                        testPlayer.setOnMove(null);
                        changeColorTuto();
                    }
                });
            }
        });
        wait.start();
    }

    private void changeColorTuto() {
        for(Obstacle.DrawProcess d : clearMe){
            Obstacle.drawProcesses.remove(d);
        }
        clearMe.clear();
        final int[] count = new int[]{0};
        Obstacle.DrawProcess textColor = new Obstacle.DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawText(context.getString(R.string.colorTuto), P.poc(0.1f)[0], P.poc(0.4f)[1], paintText);
                canvas.drawText(context.getString(R.string.colorTuto2), P.poc(0.1f)[0], P.poc(0.44f)[1], paintText);
                canvas.drawText(context.getString(R.string.colorTuto3), P.poc(0.1f)[0], P.poc(0.48f)[1], paintText);
                canvas.drawText(context.getString(R.string.colorTuto4).replaceAll(";", " "), P.poc(0.1f)[0], P.poc(0.52f)[1], paintText);
                canvas.drawText(context.getString(R.string.colorTuto5), P.poc(0.1f)[0], P.poc(0.56f)[1], paintText);
                canvas.drawText(context.getString(R.string.colorTuto6).replaceAll("x", String.valueOf(5 - count[0])), P.poc(0.1f)[0], P.poc(0.60f)[1], paintText);
            }
        };
        Obstacle.drawProcesses.add(textColor);
        clearMe.add(textColor);
        testPlayer.setOnColorChange(new Runnable() {
            @Override
            public void run() {
                if (count[0] < 4)
                    count[0]++;
                else {
                    testPlayer.setOnColorChange(null);
                    obstacleTuto();
                }
            }
        });
    }

    private void obstacleTuto() {
        MainActivity.activePlayer = testPlayer;
        for(Obstacle.DrawProcess d : clearMe){
            Obstacle.drawProcesses.remove(d);
        }
        clearMe.clear();
        final Obstacle[] o = new Obstacle[2];
        o[0] = new Circle(Color.BLACK, 1, 10, Obstacle.START_AT_BEGIN, P.poc(0.1f)[0], P.poc(0.1f)[1]);
        o[1] = new Circle(Color.WHITE, 1, 10, Obstacle.START_AT_BEGIN, P.poc(0.9f)[0], P.poc(0.9f)[1]);
        final Obstacle.DrawProcess textObstacle = new Obstacle.DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawText(context.getString(R.string.obstTuto).replaceAll(";", " "), P.poc(0.1f)[0], P.poc(0.5f)[1], paintText);
                canvas.drawText(context.getString(R.string.obstTuto2), P.poc(0.1f)[0], P.poc(0.54f)[1], paintText);
                canvas.drawText(context.getString(R.string.obstTuto3).replaceAll(";", " "), P.poc(0.1f)[0], P.poc(0.58f)[1], paintText);
                canvas.drawText(context.getString(R.string.obstTuto4), P.poc(0.1f)[0], P.poc(0.62f)[1], paintText);
            }
        };
        Obstacle.drawProcesses.add(textObstacle);
        clearMe.add(textObstacle);

        testPlayer.setOnCollide(new Runnable() {
            @Override
            public void run() {
                testPlayer.setOnCollide(null);
                testPlayer.delete();
                o[0].delete();
                o[1].delete();
                testPlayer = new PlayerCallback(Color.BLACK, 0, 22, MainActivity.setting.getBoolean("tail", true));
                final Obstacle.DrawProcess[] d = new Obstacle.DrawProcess[1];
                d[0] = new Obstacle.DrawProcess() {
                    int alpha = 255;

                    @Override
                    public void draw(Canvas canvas) {
                        if (alpha == 255) {
                            alpha = 254;
                            TickableAnimator a = new TickableAnimator(255, 0, 2000);
                            a.setInterpolator(new LinearInterpolator());
                            a.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                    Obstacle.drawProcesses.remove(d[0]);
                                }
                            });
                            a.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
                                @Override
                                public void onUpdate(TickableAnimator animator) {
                                    alpha = animator.getValue();
                                }
                            });
                            a.start();
                        }

                        paintText.setColor(Color.BLACK);
                        paintText.setAlpha(alpha);
                        canvas.drawRect(P.poc(0.1f)[0], P.poc(0.85f)[1], P.poc(0.9f)[0], P.poc(0.9f)[1], paintText);
                        paintText.setColor(Color.WHITE);
                        paintText.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(context.getString(R.string.tutoDie), P.poc(0.5f)[0], P.poc(0.885f)[1], paintText);
                        paintText.setTextAlign(Paint.Align.LEFT);
                        paintText.setAlpha(255);
                    }
                };
                Obstacle.drawProcesses.add(d[0]);
                obstacleTuto();
            }
        });
        Tickable.tickables.add(new Tickable() {
            int progress = 0;
            @Override
            public void tick() {
                if(o[0].getProgress() >= 1.0f){
                    progress = 1;
                    finishTuto();
                }
            }

            @Override
            public float getProgress() {
                return progress;
            }

            @Override
            public void delete() {
                Tickable.tickables.remove(this);
            }
        });
    }

    private void finishTuto(){
        restoreDrawProcesses();
        restoreTickables();
        MainActivity.menu();
    }

    private void restoreTickables() {
        Tickable.tickables.clear();
        for(Tickable t : backupTickable){
            Tickable.tickables.add(t);
        }
    }

    private void restoreDrawProcesses() {
        Obstacle.drawProcesses.clear();
        for(Obstacle.DrawProcess d : backupDraw){
            Obstacle.drawProcesses.add(d);
        }
    }

    private void backupTickables() {
        for(Tickable t : Tickable.tickables){
            backupTickable.add(t);
        }
    }

    private void backupDrawProcesses() {
        for(Obstacle.DrawProcess d : Obstacle.drawProcesses){
            backupDraw.add(d);
        }
    }
}
