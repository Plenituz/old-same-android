package com.plenituz.same.interfaces;

/**
 * Created by Plenituz on 07/08/2015 for Same.
 */

/**
 * To be powerable you have to be tickable (AND be in the Tickable.tickables list) otherwise the slow and stop methods are never called
 */
public interface Powerable {
    boolean[] slowed = new boolean[]{false};//todo why is that an array ?
    float[] slowFactor = new float[]{1.0f};
    void slow(float slowFactor);
    void unSlow();

    boolean[] stopped = new boolean[]{false};
    void stop();
    void unStop();
}
