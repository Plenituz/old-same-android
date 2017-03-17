package com.plenituz.same.util;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public abstract class P {
	/**
	 * 
	 */
	private static int maxWidth;
	private static int maxHeight;
	public static Context context;
	
	static public void init(Context contextt){
		context = contextt;

		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		maxWidth = size.x;
		maxHeight = size.y;
	}
	
	static public float dp(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
	/**
	 * Give the lenght in pixel in relation to the screen 
	 * 
	 * poc()[0] is relative to the width of the screen
	 * poc()[1] is relative to the height of the screen
	 * /!\ 1.0f is not counting the on screen buttons, 1.1f does
	 */
    static int[] p = new int[2];
	static public int[] poc(float percent){
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            p[0] = (int) (maxWidth*percent);
            p[1] = (int) (maxHeight*percent);
        }else{
            p[1] = (int) (maxWidth*percent);
            p[0] = (int) (maxHeight*percent);
        }
		return p;
	}
	
	static public int oppositeColor(int color){
        return Color.rgb(255 - Color.red(color),
                255 - Color.green(color),
                255 - Color.blue(color));
	}

	static public int getDistance(int x1, int y1, int x2, int y2){
		return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
    static public double getDistanceDouble(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public static void sendMsg(int what, Object obj, Handler handler){
		Message cMsg = handler.obtainMessage();
		cMsg.what = what;
		cMsg.obj = obj;
		handler.sendMessage(cMsg);
	}
    private static Random random = null;

    /**
     *
     * @param low the lowest possible number (compris)
     * @param high the highest possible number (non compris)
     * @return a random number beetween such as {@code low <= x < high}
     */
    public static int random(int low, int high){
        if(random == null)
            random = new Random();
        return random.nextInt(high-low) + low;
    }

    public static float fRandom(float low, float high){
        if(random == null)
            random = new Random();
        return (random.nextFloat()*(high - low))+low;
    }
	
	public static RelativeLayout.LayoutParams lp(View v){
		return (RelativeLayout.LayoutParams) v.getLayoutParams();
	}

	public static void removeSelf(View v){
		((ViewGroup) v.getParent()).removeView(v);
	}
	
	/**
	 * 
	 * @param text text to display
	 * @param size the font size in SP 
	 * @return textView so you don't have to type all the things
	 */
	public static TextView basicText(String text, int size){
		TextView txt = new TextView(context);
		txt.setText(text);
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		txt.setTextColor(Color.BLACK);
        //txt.setTypeface(MainActivity.type);
		return txt;
	}
	public static EditText basicEdit(String hint, String text, int size, int inputType){
		EditText edit = new EditText(context);
		edit.setHint(hint);
		edit.setText(text);
		edit.setWidth(size);
		edit.setInputType(inputType);
		return edit;
	}
	public static Button basicBut(String text){
		Button but = new Button(context);
		but.setTextColor(Color.BLACK);
		but.setText(text);
		return but;
	}
	public static Button basicBut(String text, int bgId){
		Button but = basicBut(text);
		but.setBackground(context.getResources().getDrawable(bgId));
		return but;
	}
	public static RelativeLayout.LayoutParams basicLp(int height, int width){
		return new RelativeLayout.LayoutParams(width, height);
	}
	public static RelativeLayout.LayoutParams basicLp(String height, String width){
		int h = RelativeLayout.LayoutParams.MATCH_PARENT;
		int w = RelativeLayout.LayoutParams.MATCH_PARENT;
		if(height.equals("w")){
			h = RelativeLayout.LayoutParams.WRAP_CONTENT;
		}
		if(width.equals("w")){
			w = RelativeLayout.LayoutParams.WRAP_CONTENT;
		}
		return new RelativeLayout.LayoutParams(w, h);
	}
    public static void animateBgColor(String from, String to, final View v, int duration){
        animateBgColor(from, to, v, duration, null);
    }
	
	public static void animateBgColor(String from, String to, final View v, int duration, final onAnimationEndListener mOnAnimationEndListener){
		final float[] fr = new float[3],
				t =   new float[3];
		
		Color.colorToHSV(Color.parseColor("#" + from), fr);   // from white
		Color.colorToHSV(Color.parseColor("#" + to), t);     // to red

		ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
		anim.setDuration(duration);                              // for 300 ms

		final float[] hsv  = new float[3];                  // transition color
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
			@Override
            public void onAnimationUpdate(ValueAnimator animation) {
				// Transition along each axis of HSV (hue, saturation, value)
				hsv[0] = fr[0] + (t[0] - fr[0])*animation.getAnimatedFraction();
				hsv[1] = fr[1] + (t[1] - fr[1])*animation.getAnimatedFraction();
				hsv[2] = fr[2] + (t[2] - fr[2])*animation.getAnimatedFraction();

				v.getBackground().setColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.MULTIPLY);
                if(mOnAnimationEndListener != null && animation.getAnimatedFraction() == 1.0f)
                    mOnAnimationEndListener.onAnimationEnd(v);
			}
		});
		anim.start();
	}
    public interface onAnimationEndListener{
        void onAnimationEnd(View v);
    }

    public static Button fancyButton(String text, int bgId, int color1, int color2){
        return fancyButton(text, bgId, String.format("#%06X", 0xFFFFFF & color1).substring(1), String.format("#%06X", 0xFFFFFF & color2).substring(1));
    }
	
	public static Button fancyButton(String text, int bgId, final String color1, final String color2){
		Button but = P.basicBut(text, bgId);
       // but.setTypeface(MainActivity.type);
		but.getBackground().setColorFilter(Color.parseColor("#" + color1), PorterDuff.Mode.MULTIPLY);
		but.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(final View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_UP:
					//v.performClick();
					P.animateBgColor(color2, color1, v, 300);
					break;
				case MotionEvent.ACTION_DOWN:
					P.animateBgColor(color1, color2, v, 300);
					break;
				}
				return false;
			}
		});
		return but;
	}
    public static boolean inViewBounds(View view, int x, int y){
        Rect outRect = new Rect();
        view.getDrawingRect(outRect);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    /**
     *
     * @param xa anchor
     * @param ya anchor
     * @param angle
     * @param xPoint point to check
     * @param yPoint point ot check
     * @return
     */
    public static float getDistanceFromLine(int xa, int ya, float angle, int xPoint, int yPoint){
        double tan = Math.tan(Math.toRadians(angle));
        return (float) (Math.abs((-(tan) * xPoint) + yPoint + (tan * xa) - ya) / Math.sqrt((-tan) * (-tan) + 1));
    }

    /**
     *
     * @param x anchor
     * @param y anchor
     * @param x1 moving point
     * @param y1 moving point
     * @return the angle between the two point and the horizontal, Au dessus de l'anchor les degrees vont de 0 (a droite de l'anchor) a 180 (a gauche de l'anchor), de meme avec les points sous l'anchor mais ils sont negatifs (de -0 a -180)
     */
    public static double getAngle(double x, double y, double x1, double y1){
        return y1 > y ?
                ((Math.toDegrees(Math.acos((x1 - x) / Math.sqrt( Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2) ))))*-1)+360 :
                Math.toDegrees(Math.acos((x1 - x) / Math.sqrt( Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2) )));
    }

    public static double getAngle(int xAnchor, int yAnchor, int xNormal, int yNormal, int x, int y){
        //theoreme d'al kashi : https://fr.wikipedia.org/wiki/Loi_des_cosinus#Le_th.C3.A9or.C3.A8me_et_ses_applications
        // angle = acos( (a^2 + b^2 - c^2)/2*a*b)
        //avec  a : dist anchor - point
        //      b : dist anchor - normal
        //      c : dist normal - point
        return -1*Math.signum((xNormal - xAnchor)*(y - yAnchor) - (yNormal - yAnchor)*(x - xAnchor)) *
                Math.toDegrees( Math.acos( (Math.pow(getDistanceDouble(xAnchor, yAnchor, x, y), 2) +
                        Math.pow(getDistanceDouble(xAnchor, yAnchor, xNormal, yNormal), 2) -
                        Math.pow(getDistanceDouble(xNormal, yNormal, x, y), 2))/(2*getDistanceDouble(xAnchor, yAnchor, x, y) * getDistanceDouble(xAnchor, yAnchor, xNormal, yNormal)) ) );
    }

    // SI Y AUGMENTE  DE h ALORS X AUGMENTE DE h/A (A = SLOPE)
    public static double getSlope(int xa, int ya, int xb, int yb){
        double r = (double) (yb - ya)/(xb - xa);
        if(r > 30){//prevent infinity cases where xa == xb, would fuck everything up
            return 30;
        }else if(r < -30){
            return -30;
        }else{
            return r;
        }
    }

    /**
     *
     * @param p1
     * @param p2
     * @param p3
     * @return the angle formed by the 3 points in rad
     */
    public static double getTripointAngle(float[] p1, float[] p2, float[] p3){
        double P12 = P.getDistanceDouble(p1[0], p1[1], p2[0], p2[1]);
        double P13 = P.getDistanceDouble(p1[0], p1[1], p3[0], p3[1]);
        double P23 = P.getDistanceDouble(p2[0], p2[1], p3[0], p3[1]);
        double f = Math.acos(((P12 * P12) + (P23 * P23) - (P13 * P13)) / (2 * P12 * P23));
        return f;
    }

    public static void writeString(String path, String toWrite) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
        objectOutputStream.writeObject(toWrite);
        objectOutputStream.close();
    }

    public static String readString(String path) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)));
        String str = (String)objectInputStream.readObject();
        objectInputStream.close();
        return str;
    }

    public static void animateViewSize(final View v, int fromWidth, int toWidth, int fromHeight, int toHeight, long duration){
        ValueAnimator animWidth = ValueAnimator.ofInt(fromWidth, toWidth);
        animWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams lp = v.getLayoutParams();
                lp.width = (int) animation.getAnimatedValue();
                v.setLayoutParams(lp);
            }
        });
        animWidth.setDuration(duration);
        animWidth.setInterpolator(new DecelerateInterpolator());
        animWidth.start();

        ValueAnimator animHeight = ValueAnimator.ofInt(fromHeight, toHeight);
        animHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams lp = v.getLayoutParams();
                lp.height = (int) animation.getAnimatedValue();
                v.setLayoutParams(lp);
            }
        });
        animHeight.setDuration(duration);
        animHeight.setInterpolator(new DecelerateInterpolator());
        animHeight.start();

    }
    public static void copyToClipBoard(String str){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(str);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Message",str);
            clipboard.setPrimaryClip(clip);
        }
    }
    public static String pasteFromClipBoard(){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            return (String) clipboard.getText();
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            try{
                return clipboard.getPrimaryClip().getItemAt(0).getText().toString();
            }catch (NullPointerException e){
                return "";
            }
        }
    }

    public static void animatePos(int fromX, int toX, int fromY, final int toY, long duration,
                           final WindowManager.LayoutParams toAnimate, final RelativeLayout layout, TimeInterpolator interpolator,
                           long startOffset, final WindowManager windowManager, final onAnimationEndListener mOnAnimationEndListener){
        ValueAnimator anim1 = ValueAnimator.ofInt(fromX, toX);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                toAnimate.x = (Integer) animation.getAnimatedValue();
                windowManager.updateViewLayout(layout, toAnimate);
            }
        });
        anim1.setDuration(duration);
        anim1.setInterpolator(interpolator);
        anim1.setStartDelay(startOffset);

        ValueAnimator anim2 = ValueAnimator.ofInt(fromY, toY);
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                toAnimate.y = (Integer) animation.getAnimatedValue();
                windowManager.updateViewLayout(layout, toAnimate);
                if((int) animation.getAnimatedValue() == toY && mOnAnimationEndListener != null){
                    mOnAnimationEndListener.onAnimationEnd(layout);
                }

            }
        });
        anim2.setDuration(duration);
        anim2.setInterpolator(interpolator);
        anim2.setStartDelay(startOffset);

        anim1.start();
        anim2.start();
    }

    public static void setScrollbarThumbVertical(FrameLayout scroll, int drawableId){
        //MIND FREAKING BLOWN
        try
        {
            Field mScrollCacheField = View.class.getDeclaredField("mScrollCache");
            mScrollCacheField.setAccessible(true);
            Object mScrollCache = mScrollCacheField.get(scroll); // scr is your Scroll View

            Field scrollBarField = mScrollCache.getClass().getDeclaredField("scrollBar");
            scrollBarField.setAccessible(true);
            Object scrollBar = scrollBarField.get(mScrollCache);

            Method method = scrollBar.getClass().getDeclaredMethod("setVerticalThumbDrawable", Drawable.class);
            method.setAccessible(true);

            // Set your drawable here.
            method.invoke(scrollBar, context.getResources().getDrawable(drawableId));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void setScrollbarThumbHorizontal(FrameLayout scroll, int drawableId){
        //MIND FREAKING BLOWN
        try
        {
            Field mScrollCacheField = View.class.getDeclaredField("mScrollCache");
            mScrollCacheField.setAccessible(true);
            Object mScrollCache = mScrollCacheField.get(scroll); // scr is your Scroll View

            Field scrollBarField = mScrollCache.getClass().getDeclaredField("scrollBar");
            scrollBarField.setAccessible(true);
            Object scrollBar = scrollBarField.get(mScrollCache);

            Method method = scrollBar.getClass().getDeclaredMethod("setHorizontalThumbDrawable", Drawable.class);
            method.setAccessible(true);

            // Set your drawable here.
            method.invoke(scrollBar, context.getResources().getDrawable(drawableId));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param //rawName the name of the raw file you need (if R.raw.music then enter "music")
     * @return the actual value of the R.raw.music
     */
//    public static int getRawFromName(String rawName){
//        Field[] raws = R.raw.class.getFields();
//        for(Field raw:raws){
//            if(raw.getName().equals(rawName))
//                try {
//                    return (int) raw.get(null);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                    //this happens if the field is not static which never happens
//                }
//        }
//        return Integer.parseInt(null);
//    }

    public static float[] getCoorOnPathAtPercent(float percent, Path path){
        PathMeasure pm = new PathMeasure(path, false);
        //coordinates will be here
        float aCoordinates[] = {0f, 0f};

        //get coordinates of the point
        pm.getPosTan(pm.getLength() * percent, aCoordinates, null);
        return aCoordinates;
    }

    public static String getPhoneID() {
        try {
            String s = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *
     * @param x1 point 1
     * @param y1 point 1
     * @param x2 point 2
     * @param y2 point 2
     * @param r distance
     * @return les deux points sur la droite a cette distance
     */
    public static int[] getPointOnLineAtDistance(int x1, int y1, int x2, int y2, int r){
        double a = P.getSlope(x1, y1, x2, y2);
        double[] inters = getIntersectBetweenLineAndCircle(x1, y1, a, (int) (y1 - (a*x1)), r);

        return new int[]{(int) inters[0], (int) inters[1], (int) inters[2], (int) inters[3]};
    }

    public static int[] getPointOnLineAtDistance(int x, int y, float a, float r){
        double[] inters = getIntersectBetweenLineAndCircle(x, y, a, (int) (y - (a * x)), r);
        return new int[]{(int) inters[0], (int) inters[1], (int) inters[2], (int) inters[3]};
    }

    /**
     *
     * @param x1 centre du cercle
     * @param y1 centre du cercle
     * @param a pente de la droite
     * @param b
     * @param r rayon du cercle
     * @return les points d'intersections [0,1] = x1, y1 [2, 3] = x2, y2
     */
    public static double[] getIntersectBetweenLineAndCircle(int x1, int y1, double a, int b, float r){
        double X1 = (-((2*a*b) - (2*x1) - (2*a*y1)) + Math.sqrt(Math.pow(((2 * a * b) - (2 * x1) - (2 * a * y1)), 2) - (4 * ((a * a) + 1) * ((b * b) + (x1 * x1) + (y1 * y1) - (2 * b * y1) - (r * r)))))/(2*((a*a) + 1));
        double X2 = (-((2*a*b) - (2*x1) - (2*a*y1)) - Math.sqrt(Math.pow(((2 * a * b) - (2 * x1) - (2 * a * y1)), 2) - (4 * ((a * a) + 1) * ((b * b) + (x1 * x1) + (y1 * y1) - (2 * b * y1) - (r * r)))))/(2*((a*a) + 1));

        double Y1 = a*X1 + b;
        double Y2 = a*X2 + b;
        return new double[]{ X1,  Y1,  X2,  Y2};
    }

    public static int[] getClosestPoint(int[] pointsToDifferentiate, int[] anchor){
        return P.getDistance(pointsToDifferentiate[0], pointsToDifferentiate[1], anchor[0], anchor[1]) >  P.getDistance(pointsToDifferentiate[2], pointsToDifferentiate[3], anchor[0], anchor[1]) ?
                new int[]{pointsToDifferentiate[2], pointsToDifferentiate[3]}: new int[]{pointsToDifferentiate[0], pointsToDifferentiate[1]};
    }

    public static int[] getIntersectBetweenLines(int l1x1, int l1y1, int l1x2, int l1y2, int l2x1, int l2y1, int l2x2, int l2y2){
        double a = getSlope(l1x1, l1y1, l1x2, l1y2);
        int b = (int) (l1y1 - (a*l1x1));
        double c = getSlope(l2x1, l2y1, l2x2, l2y2);
        int d = (int) (l2y1 - (a*l2x1));

        int[] r = new int[2];
        r[0] = (int) ((b - d)/(a - c));
        r[1] = (int) (a*r[1] + b);
        return r;
    }
}
