package com.plenituz.same.interfaces;

import java.util.ArrayList;

/**
 * Created by Plenituz on 25/07/2015.
 */
public interface Tickable {
    ArrayList<Tickable> tickables = new ArrayList<>();

    /**
     * this is where you do all your actions, called ~63 times a second
     * <b>DON'T ALLOCATE ANYTHING HERE, NOT EVEN AN INT</b>
     */
    void tick();

    /**
     * tickables are killed if their progresse is >= 1.0f
     * @return
     */
    float getProgress();

    /**
     * method called to get ride of the tickable, you have to update {@link #tickables} yourself
     */
    void delete();
}
