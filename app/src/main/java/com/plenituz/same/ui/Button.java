package com.plenituz.same.ui;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;

import com.plenituz.same.interfaces.Clickable;
import com.plenituz.same.interfaces.Tickable;
import com.plenituz.same.obstacles.Obstacle;
import com.plenituz.same.util.P;
import com.plenituz.same.util.TickableAnimator;

/**
 * Created by Plenituz on 31/08/2015 for Same.
 */
public class Button implements Tickable, Clickable{

    private final String text;
    private final Obstacle.DrawProcess drawProcess;
    private int width;
    private final int offsetTextX;
    private final int offsetTextY;
    private final Paint paintText;
    private final Paint paintBg;
    private Region region;
    private Region clip;
    private Path path;
    private int x;
    private int y;
    private float offsetRadius;
    private OnClickListener onClickListener;
    private boolean isHidden = false;

    private float slope = 2f;//todo angle arche pas en fati
    private float speed = 3;


    public Button(int bgColor, int textColor, String text, int x, int y, int width, int offsetTextX, int offsetTextY, int textSize){
        this.text = text;
        this.width = width;
        this.x = x;
        this.y = y;
        this.offsetTextX = offsetTextX;
        this.offsetTextY = offsetTextY;
        offsetRadius = P.dp(10);
        drawProcess = getDrawProcess();
        region = setupRegion();

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setColor(textColor);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(textSize);

        paintBg = new Paint(paintText);
        paintBg.setColor(bgColor);

        Obstacle.drawProcesses.add(drawProcess);
        Tickable.tickables.add(this);
        Clickable.clickables.add(this);

    }

    @Override
    public void onClick(){
        if(onClickListener != null)
            onClickListener.onClick(this);
    }

    @Override
    public void tick() {
        path.rewind();
        path.addCircle(x, y, width / 2, Path.Direction.CW);
        clip.set(x - (width / 2), y - (width / 2), x + (width / 2), y + (width / 2));
        region.setPath(path, clip);
    }

    /**
     * Animate the position and size of the button, the default interpolator is {@code android.view.animation.OvershootInterpolator}
     * @param fromX original position X
     * @param toX target position X
     * @param fromY original position Y
     * @param toY target position Y
     * @param fromWidth original size
     * @param toWidth targer size
     * @param duration duration of the animation
     * @param l callback triggered when the animation ends
     */
    public void animateFromTo(int fromX, int toX, int fromY, int toY, @IntRange(from = 0)int fromWidth,
                              @IntRange(from = 0)int toWidth, @IntRange(from = 0)int duration,
                              @Nullable final TickableAnimator.OnAnimationEndListener l){
        animateFromTo(fromX, toX, fromY, toY, fromWidth, toWidth, duration, l, new AnticipateOvershootInterpolator());
    }

    public void animateFromTo(int fromX, int toX, int fromY, int toY, @IntRange(from = 0)int fromWidth,
                              @IntRange(from = 0)int toWidth, @IntRange(from = 0)int duration,
                              @Nullable final TickableAnimator.OnAnimationEndListener l, TimeInterpolator interpolator){
        TickableAnimator animX = new TickableAnimator(fromX, toX, duration);
        animX.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
            @Override
            public void onUpdate(TickableAnimator animator) {
                x = animator.getValue();
            }
        });
        animX.setOnAnimationEndListener(new TickableAnimator.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                if(l != null)
                    l.onAnimationEnd();
            }
        });
        animX.setInterpolator(interpolator);
        animX.start();

        TickableAnimator animY = new TickableAnimator(fromY, toY, duration);
        animY.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
            @Override
            public void onUpdate(TickableAnimator animator) {
                y = animator.getValue();
            }
        });
        animY.setInterpolator(interpolator);
        animY.start();


        TickableAnimator animWidth = new TickableAnimator(fromWidth, toWidth, duration);
        animWidth.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
            @Override
            public void onUpdate(TickableAnimator animator) {
                width = animator.getValue();
            }
        });
        animWidth.setInterpolator(interpolator);
        animWidth.start();
    }

    /**
     * Animate the opacity of the text diplayed on the button, value must be [0..255]
     * @param fromAlpha original alpha
     * @param toAlpha
     * @param duration
     */
    public void animateTextAlpha(@IntRange(from = 0, to = 255)int fromAlpha,
                                 @IntRange(from = 0, to = 255)int toAlpha, @IntRange(from = 0)int duration){
        TickableAnimator animAlpha = new TickableAnimator(fromAlpha, toAlpha, duration);
        animAlpha.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
            @Override
            public void onUpdate(TickableAnimator animator) {
                paintText.setAlpha(animator.getValue());
            }
        });
        animAlpha.setInterpolator(new AccelerateDecelerateInterpolator());
        animAlpha.start();
    }

    public void hide(){
        isHidden = true;
        delete();
    }

    public void show(){
        if(isHidden){
            Obstacle.drawProcesses.add(drawProcess);
            Tickable.tickables.add(this);
            Clickable.clickables.add(this);
            isHidden = false;
        }
    }

    @Override
    public float getProgress() {
        return 0;
    }

    @Override
    public void delete(){
        Obstacle.drawProcesses.remove(drawProcess);
        Tickable.tickables.remove(this);
        Clickable.clickables.remove(this);
    }

    private Obstacle.DrawProcess getDrawProcess(){
        return new Obstacle.DrawProcess() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawCircle(x, y, width / 2, paintBg);
                canvas.drawText(text, x + offsetTextX, y + offsetTextY, paintText);
            }
        };
    }

    private Region setupRegion(){
        path = new Path();
        clip = new Region();
        return new Region();
    }

    public Region getRegion() {
        return region;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public float getOffsetRadius() {
        return offsetRadius;
    }

    public void setBgColor(int color){
        paintBg.setColor(color);
    }

    public int getBgColor(){
        return paintBg.getColor();
    }

    public void setTextColor(int color){
        paintText.setColor(color);
    }

    public void setOnClickListener(OnClickListener l){
        onClickListener = l;
    }

    public interface OnClickListener{
        void onClick(Button button);
    }
}
