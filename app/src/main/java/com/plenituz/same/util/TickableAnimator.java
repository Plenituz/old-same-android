package com.plenituz.same.util;

import android.animation.TimeInterpolator;
import android.view.animation.AnticipateInterpolator;

import com.plenituz.same.interfaces.Tickable;

/**
 * Created by Plenituz on 26/09/2015 for Same.
 */
public class TickableAnimator implements Tickable {

    private long startTime = 0;
    private final int to;
    private final int from;
    private int duration;
    private int value;
    private OnAnimationEndListener mOnAnimationEndListener;
    private OnUpdateListener mOnUpdateListener;
    /**
     * default interpolator is AccelerateInterpolator
     */
    private TimeInterpolator interpolator;

    public TickableAnimator(int from, int to, int duration){
        Tickable.tickables.add(this);
        this.to = to;
        this.from = from;
        this.duration = duration;
        value = from;
        interpolator = new AnticipateInterpolator();
    }

    @Override
    public void tick() {
        if(startTime != 0){
            //here I can use android's interpolators since the system used is similar
            value = (int) (from + (to - from)*interpolator.getInterpolation((float) (System.currentTimeMillis() - startTime)/duration));
            if(mOnUpdateListener != null)
                mOnUpdateListener.onUpdate(this);
            if(System.currentTimeMillis() - startTime >= duration){
                value = to;
                if(mOnUpdateListener != null)
                    mOnUpdateListener.onUpdate(this);
                if(mOnAnimationEndListener != null)
                    mOnAnimationEndListener.onAnimationEnd();
                delete();
            }
        }
    }

    public void start(){
        startTime = System.currentTimeMillis();
    }

    public void setOnAnimationEndListener(OnAnimationEndListener l){
        mOnAnimationEndListener = l;
    }

    public void setOnUpdateListener(OnUpdateListener l){
        mOnUpdateListener = l;
    }

    /**
     * use the android interpolators
     * @param i time interpolator
     */
    public void setInterpolator(TimeInterpolator i){
        interpolator = i;
    }

    public int getValue(){
        return value;
    }

    @Override
    public float getProgress() {
        return 0;
    }

    @Override
    public void delete() {
        Tickable.tickables.remove(this);
    }

    public interface OnAnimationEndListener{
        void onAnimationEnd();
    }

    public interface OnUpdateListener{
        void onUpdate(TickableAnimator animator);
    }
}
