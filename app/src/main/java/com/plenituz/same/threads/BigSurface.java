package com.plenituz.same.threads;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.plenituz.same.interfaces.Tickable;
import com.plenituz.same.obstacles.Obstacle;
import com.plenituz.same.util.P;

/**
 * Created by Plenituz on 25/07/2015.
 */
public class BigSurface extends SurfaceView implements SurfaceHolder.Callback {
    int i;

    private SurfaceHolder mSurfaceHolder;
    private DrawingThread mDrawingThread;


    private long startTime;
    private boolean reset = true;
    private int count;
    private Paint debugPaint;
    private boolean doDebug;
    public static String debugString;
    public static int lastestDrawFrameRate;
    public static int lastestTickerFrameRate;
    public static int lastestColliderFrameRate;


    public BigSurface(Context context) {
        super(context);
        init();
    }

    public BigSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BigSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init(){
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mDrawingThread = new DrawingThread();
        setBackgroundColor(Color.parseColor("#89AAE6"));
        doDebug = getContext().getSharedPreferences("options", Context.MODE_PRIVATE).getBoolean("debug", false);
        //89AAE6
        //899878
        //A93F55

        debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        debugPaint.setColor(Color.BLACK);
        debugPaint.setStyle(Paint.Style.FILL);
        debugPaint.setTextSize(25);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(i = 0; i < Obstacle.drawProcesses.size(); i++){
            try{
                Obstacle.drawProcesses.get(i).draw(canvas);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        if(true || doDebug){
            debugString = "Ticker:" + lastestTickerFrameRate + " | Draw:" + lastestDrawFrameRate + " | Colli:" + lastestColliderFrameRate + " | tickables :" + Tickable.tickables.size();
            canvas.drawText(debugString, P.poc(0.05f)[0], P.poc(0.1f)[1], debugPaint);
            try {
                for (i = 0; i < Tickable.tickables.size(); i++) {
                    canvas.drawText(Tickable.tickables.get(i).getClass().toString(), P.poc(0.05f)[0], P.poc(0.12f)[1] + (P.poc(0.01f)[1] * i), debugPaint);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawingThread.isRunning = true;
        mDrawingThread.start();
        setWillNotDraw(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mDrawingThread.isRunning = false;
        mDrawingThread.t = null;
        boolean joined = false;
        while(!joined){
            try {
                mDrawingThread.join();
                joined = true;
            }catch (InterruptedException ignored){}
        }
    }


    private class DrawingThread extends Thread{
        private boolean isRunning = true;
        Thread t;

        @Override
        public void run() {
            while (isRunning){
                Canvas canvas = null;
                try{
                    canvas = mSurfaceHolder.lockCanvas();
                    synchronized (mSurfaceHolder){
                        postInvalidate();
                    }
                }catch (Exception ignored){

                }finally {
                    if(canvas != null)
                        try{
                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                        }catch (IllegalStateException e){}
                }
                if(reset){
                    startTime = System.currentTimeMillis();
                    count = 0;
                    reset = false;
                }
                count++;
                if(System.currentTimeMillis() - startTime >= 1000){
                    lastestDrawFrameRate = count;
                    reset = true;
                }
            }
        }

        @Override
        public synchronized void start() {
            if( t == null){
                t = new Thread(this, "gameRenderer");
                t.start();
            }
        }
    }
}
