package com.plenituz.same;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.FloatRange;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.plenituz.same.interfaces.Clickable;
import com.plenituz.same.interfaces.Tickable;
import com.plenituz.same.obstacles.Obstacle;
import com.plenituz.same.obstacles.Player;
import com.plenituz.same.threads.Collider;
import com.plenituz.same.threads.Ticker;
import com.plenituz.same.ui.Button;
import com.plenituz.same.ui.Image;
import com.plenituz.same.ui.Tutorial;
import com.plenituz.same.util.P;
import com.plenituz.same.util.TickableAnimator;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends Activity {
    public static AtomicInteger lastX = new AtomicInteger(0);
    public static AtomicInteger lastY = new AtomicInteger(0);
    public static View.OnTouchListener levelTouchListener;
    public static View.OnTouchListener menuTouchListener;
    public static View.OnTouchListener activeTouchListener;
    public static boolean changeColor = false;
    public static AtomicBoolean changePos = new AtomicBoolean(false);
    public static Player activePlayer;
    public static Button playBut;
    public static Button optionBut;

    public static Image logoView;
    public static SharedPreferences setting;

    private int i;

    Collider collider;
    Ticker ticker;
    static LevelGenerator levelGenerator;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    float partialBezierPoint(float t, float a, float b, float c, float d)
    {
        float C1 = ( d - (3.0f * c) + (3.0f * b) - a );
        float C2 = ( (3.0f * c) - (6.0f * b) + (3.0f * a) );
        float C3 = ( (3.0f * b) - (3.0f * a) );
        float C4 = ( a );
        return ( C1*t*t*t + C2*t*t + C3*t + C4  );
    }

    /**
     *
     * @param t from 0 to 1
     * @param p1 origin point
     * @param p2 first handle
     * @param p3 second handle
     * @param p4 arriving point
     * @return the point at t% of the curve defined by the points
     */
    Point bezierPoint2d(@FloatRange(from = 0f, to = 1f)float t, Point p1, Point p2, Point p3, Point p4){
        Point p = new Point();
        p.x = (int) partialBezierPoint(t, p1.x, p2.x, p3.x, p4.x);
        p.y = (int) partialBezierPoint(t, p1.y, p2.y, p3.y, p4.y);
        return p;
    }

    /**
     *
     * @param t from 0 to 1
     * @param p1 origin point int[]{x, y, z}
     * @param p2 first handle int[]{x, y, z}
     * @param p3 second handle int[]{x, y, z}
     * @param p4 arriving point int[]{x, y, z}
     * @return the point at t% of the curve defined by the points
     */
    int[] bezierPoint3d(@FloatRange(from = 0f, to = 1f)float t, int[] p1, int[] p2, int[] p3, int[] p4){
        int[] p = new int[3];
        p[0] = (int) partialBezierPoint(t, p1[0], p2[0], p3[0], p4[0]);
        p[1] = (int) partialBezierPoint(t, p1[1], p2[1], p3[1], p4[1]);
        p[2] = (int) partialBezierPoint(t, p1[2], p2[2], p3[2], p4[2]);
        return p;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        hideUI();


        /*
        power : invincibilité, plus tu manges d'obstacles plus t'as de points
        faire en sorte que quand on appuie sur l'ecran sans rien ca change de couleur
         */
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static String getPath() {
        return Environment.getExternalStorageDirectory() + "/Same";
    }

    private void init() {
        P.init(this);

        new File(getPath()).mkdir();//create ALL THE DIRECTORIES
        new File(getPath() + "/crashs").mkdir();

        setting = getSharedPreferences("options", MODE_PRIVATE);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                    getPath() + "/crashs", null));
        }

        createTouchListeners();

        ticker = new Ticker();
        ticker.start();
        collider = new Collider();
        collider.start();

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_same_512);
        Rect originalRect = new Rect(0, 0, 512 * 4, 512 * 4);
        Rect sizeRect = new Rect(P.poc(0.5f)[0] - P.poc(0.2f)[0], P.poc(0.2f)[1] - P.poc(0.2f)[0], P.poc(0.5f)[0] + P.poc(0.2f)[0], P.poc(0.2f)[1] + P.poc(0.2f)[0]);
        logoView = new Image(logo, originalRect, sizeRect);
        logoView.setOnClickListener(new Clickable.OnClickListener() {
            int touch = 0;
            long lastTouch = 0;

            @Override
            public void onClick() {
                if (System.currentTimeMillis() - lastTouch < 200) {
                    touch++;
                } else {
                    touch = 0;
                }
                if (touch >= 3) {
                    //todo magic thing here
                    Toast.makeText(getBaseContext(), "YEY", Toast.LENGTH_SHORT).show();
                    touch = 0;
                }
                lastTouch = System.currentTimeMillis();
            }
        });


        playBut = new Button(Color.BLACK, Color.CYAN, getString(R.string.play), P.poc(0.5f)[0], P.poc(0.5f)[1], P.poc(0.35f)[0], 0, P.poc(0.02f)[1], P.poc(0.1f)[0]);
        optionBut = new Button(Color.BLACK, Color.CYAN, getString(R.string.options), P.poc(0.5f)[0], P.poc(0.8f)[1], P.poc(0.35f)[0], 0, P.poc(0.013f)[1], P.poc(0.08f)[0]);

        playBut.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(Button button) {
                playBut.animateTextAlpha(255, 0, 400);
                playBut.animateFromTo(playBut.getX(), P.poc(0.5f)[0], playBut.getY(), P.poc(0.8f)[1], playBut.getWidth(), (int) P.dp(22) * 2, 800, new TickableAnimator.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        playBut.hide();
                        optionBut.hide();
                        startGame();
                    }
                });
                optionBut.animateTextAlpha(255, 0, 400);
                optionBut.animateFromTo(optionBut.getX(), P.poc(0.5f)[0], optionBut.getY(), P.poc(0.8f)[1], optionBut.getWidth(), (int) P.dp(22) * 2, 800, null);
            }
        });
        optionBut.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(Button button) {
//                optionBut.animateFromTo(optionBut.getX(), optionBut.getX(), optionBut.getY(),
//                        optionBut.getY(), optionBut.getWidth(), P.poc(1.8f)[1], 800, new TickableAnimator.OnAnimationEndListener() {
//                    @Override
//                    public void onAnimationEnd() {
//
//                    }
//                }, new AccelerateInterpolator());
            }
        });

        menu();
        startTuto(); //todo finir le tuto
        new DecelerateInterpolator()
        if (setting.getBoolean("tuto", true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.tutoMsg));
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startTuto();
                }
            });
            builder.setNeutralButton(R.string.no, null);
            builder.setNegativeButton(R.string.never, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setting.edit().putBoolean("tuto", false).apply();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void startTuto() {
        setActiveTouchListener(levelTouchListener);
        Tutorial t = new Tutorial();
        t.start(this);
    }

    public static void startGame() {
        TickableAnimator anim = new TickableAnimator(0, P.poc(0.32f)[1], 500);
        anim.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
            @Override
            public void onUpdate(TickableAnimator animator) {
                logoView.dstRect.offsetTo(P.poc(0.5f)[0] - P.poc(0.2f)[0], (P.poc(0.2f)[1] - P.poc(0.2f)[0]) - animator.getValue());
            }
        });
        anim.start();
        if (levelGenerator != null)
            levelGenerator.delete();
        levelGenerator = new LevelGenerator();
        if (activePlayer != null)
            activePlayer.delete();
        activePlayer = new Player(playBut.getBgColor(), 0, 22, setting.getBoolean("tail", true));
        setActiveTouchListener(levelTouchListener);
    }

    public static void menu() {
        ArrayList<Integer> del = new ArrayList<>();
        for (int i = 0; i < Tickable.tickables.size(); i++) {
            if (Tickable.tickables.get(i) instanceof Obstacle) {
                del.add(i);//if i delete the obstacle here it doesn't work
                //since Tickable.tickables get shorter automatically and some are skipped
            }
        }
        if (del.size() > 0) {
            for (int i = del.size() - 1; i >= 0; i--) {
                Tickable.tickables.get(del.get(i)).delete();
            }
        }

        setActiveTouchListener(menuTouchListener);

        if (levelGenerator != null)
            levelGenerator.delete();
        if (activePlayer != null) {
            playBut.animateTextAlpha(0, 0, 0);
            playBut.show();
            playBut.setBgColor(activePlayer.getColor());
            playBut.setTextColor(activePlayer.getColor() == Color.BLACK ? Color.WHITE : Color.BLACK);

            optionBut.animateTextAlpha(0, 0, 0);
            optionBut.show();
            optionBut.setBgColor(activePlayer.getColor());
            optionBut.setTextColor(activePlayer.getColor() == Color.BLACK ? Color.WHITE : Color.BLACK);

            playBut.animateFromTo(MainActivity.lastX.get(), P.poc(0.5f)[0], MainActivity.lastY.get(), P.poc(0.5f)[1], (int) P.dp(22) * 2, P.poc(0.35f)[0], 800, new TickableAnimator.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd() {
                    playBut.animateTextAlpha(0, 255, 400);
                    optionBut.animateTextAlpha(0, 255, 400);
                }
            });
            optionBut.animateFromTo(activePlayer.getX(), P.poc(0.5f)[0], activePlayer.getY(), P.poc(0.8f)[1], (int) P.dp(22) * 2, P.poc(0.35f)[0], 800, null);

        }

        TickableAnimator anim = new TickableAnimator(P.poc(0.32f)[1], 0, 800);
        anim.setOnUpdateListener(new TickableAnimator.OnUpdateListener() {
            @Override
            public void onUpdate(TickableAnimator animator) {
                logoView.dstRect.offsetTo(P.poc(0.5f)[0] - P.poc(0.2f)[0], (P.poc(0.2f)[1] - P.poc(0.2f)[0]) - animator.getValue());
            }
        });
        anim.setInterpolator(new BounceInterpolator());
        anim.start();
        System.gc();
    }


    private void createTouchListeners() {
        levelTouchListener = new View.OnTouchListener() {
            int oldCount = 0;
            int count;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (P.getDistance((int) event.getRawX(), (int) event.getRawY(), lastX.get(), lastY.get()) > P.dp(70))
                    return false;

                lastY.set((int) event.getRawY());
                lastX.set((int) event.getRawX());
                changePos.set(true);

                //change color handling
                count = event.getPointerCount();
                if (count != oldCount) {
                    oldCount = count;
                    if (count > 1) {
                        changeColor = true;
                        //ticker then see that it needs to change color and does it
                    }
                }
                return false;
            }
        };

        menuTouchListener = new View.OnTouchListener() {
            int[] downAt = new int[]{0, 0};

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastY.set((int) event.getRawY());
                lastX.set((int) event.getRawX());
                //todo do real clicks not any touchs

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downAt[0] = (int) event.getRawX();
                        downAt[1] = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        for (i = 0; i < Clickable.clickables.size(); i++) {
                            if (Clickable.clickables.get(i).getRegion().contains((int) event.getRawX(), (int) event.getRawY())
                                    && Clickable.clickables.get(i).getRegion().contains(downAt[0], downAt[1]))
                                Clickable.clickables.get(i).onClick();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        downAt[0] = -1;
                        downAt[1] = -1;
                        break;
                }
                return false;
            }
        };
    }

    public static void setActiveTouchListener(View.OnTouchListener activeTouchListener) {
        MainActivity.activeTouchListener = activeTouchListener;
    }

    private void hideUI() {
        try {
            getActionBar().hide();
        } catch (NullPointerException e) {
            //no idea
        }

        final View decorView = getWindow().getDecorView();
        int uiOptions;
        if (Build.VERSION.SDK_INT > 18) {
            uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        } else {
            uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return activeTouchListener.onTouch(null, event);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("null"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.plenituz.same/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.plenituz.same/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
