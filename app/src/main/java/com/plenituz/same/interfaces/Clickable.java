package com.plenituz.same.interfaces;

import android.graphics.Region;

import java.util.ArrayList;

/**
 * Created by Plenituz on 19/09/2015 for Same.
 */
public interface Clickable {
    ArrayList<Clickable> clickables = new ArrayList<>();
    void onClick();
    Region getRegion();

    interface OnClickListener{
        void onClick();
    }
}
