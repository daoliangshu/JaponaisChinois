package com.daoliangshu.japonaischinois.sentencereordering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.daoliangshu.japonaischinois.core.db.DBHelper;
import com.daoliangshu.japonaischinois.sentencereordering.manager.DrawableManager;

import java.sql.SQLException;
import java.util.Random;

/**
 * Created by daoliangshu on 6/8/17.
 */

public class SenceReorderingView extends SurfaceView implements SurfaceHolder.Callback{
    private DBHelper myDBHelper;
    private int scrW;
    private int scrH;
    private Rect boardRect;
    private boolean isPushing = false;
    private boolean needPauseGame = false;

    /*References*/
    private SentenceReorderingActivity rootActivity;
    private final SentenceReorderingThread thread;
    private Context myContext;
    private final Random rand;

    private DrawableManager dm;
    private Wave waveTest;


    /*------------------------------------*/
    /*--------CONSTRUCTION : VIEW---------*/
    /*------------------------------------*/
    public SenceReorderingView (Context context, AttributeSet attrs) {
        super(context, attrs);
        try{
            myDBHelper = new DBHelper(context);
        }catch(SQLException ioe){
            ioe.printStackTrace();
        }

        rand = new Random();
        this.myContext = context;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new SentenceReorderingThread(holder, context,
                new Handler() {
                    @Override
                    public void handleMessage(Message m) {
                    }
                });
        setFocusable(true);
    }

    /*
    * Thread
    */
    class SentenceReorderingThread extends Thread {
        private static final int FRAME_DELAY = 15;
        private long mLastTime;
        private SurfaceHolder mySurfaceHolder;
        private Canvas canvas;
        private boolean running = true;
        private boolean thread_running = true;

        /*---------------------------------*/
        /*----THREAD_CONSTRUCTOR-----------*/
        /*---------------------------------*/
        public SentenceReorderingThread(SurfaceHolder surfaceHolder,
                                 Context context,
                                 @SuppressWarnings("UnusedParameters") Handler handler) {
            mySurfaceHolder = surfaceHolder;
            myContext = context;
        }

        /*----------------------------*/
        /*-----THREAD_RUN-------------*/
        /*----------------------------*/
        @Override
        public void run() {
            while (thread_running) {
                //Delay control
                long now = System.currentTimeMillis();
                long delay = FRAME_DELAY + mLastTime - now;
                if (delay > 0) try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mLastTime = now;
                canvas = null;
                try {
                    if (mySurfaceHolder.getSurface().isValid() && running) {
                        canvas = mySurfaceHolder.lockCanvas(null);
                        if (canvas != null && running) {
                            //noinspection SynchronizeOnNonFinalField
                            synchronized (mySurfaceHolder) {
                                if (running) {
                                    draw(canvas);
                                    update();
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (canvas != null) {
                        try {
                            mySurfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (IllegalStateException ise) {
                            Log.e("ERR", " failed to unlock canvas");
                        }
                    }
                }
                if (needPauseGame) {
                    running = false;
                    needPauseGame = false;
                    rootActivity.pauseGame();
                }
            }
        }




        /*------------------------------*/
        /*-----THREAD_DRAW & UPDATES----*/
        /*------------------------------*/
        private void draw(Canvas canvas) {
            //Do drawings here
            waveTest.onDraw(canvas);
        }

        /*----------------------------*/
        /*-----THREAD_SETTERS---------*/
        /*----------------------------*/
        public void setSurfaceSize(int width, int height) {
            //noinspection SynchronizeOnNonFinalField
            synchronized (mySurfaceHolder) {
                scrH = height;
                scrW = width;
                //Init resources that need size here
                dm = new DrawableManager(getContext(), new Rect(0,0,scrW, scrH));
                waveTest = new Wave(scrW, scrH, dm);
            }
            thread.setRunning(true, false);
            if (thread.getState() == State.NEW) {
                thread.start();
            }
        }

        public void stopThread() {
            thread_running = false;
        }


        public void setRunning(boolean b, boolean goToMenu) {
            if (!b && goToMenu) needPauseGame = true;
            else if(!b){
                needPauseGame = false;
                running = false;
            }
            else {
                needPauseGame = false;
                running = true;
            }
        }
    } // END OF THREAD CLASS

    /*--------------------------------*/
    /*---------DRAW & UPDATES---------*/
    /*--------------------------------*/
    private void update() {
    }




    /*--------------------------------*/
    /*---------EVENTS-----------------*/
    /*--------------------------------*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            float moveX = event.getX(i);
            float moveY = event.getY(i);
            int motionAction = event.getAction();
            switch (motionAction & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                default:
            }
        }
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (myContext == null) {
            Log.e("ERR", "myContext is null in view ...");
        }
        getThread().mySurfaceHolder = holder;
        if (thread != null)
            thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false, false);
        thread.interrupt();

    }


    public void leaveThread() {
        thread.stopThread();
        thread.interrupt();
    }


    public void setRunning(boolean state) {
        thread.setRunning(state, false);
        //this.thread.setRunning(state);
    }
    public SentenceReorderingThread getThread() {
        return this.thread;
    }


    public void setActivity(SentenceReorderingActivity root){
        this.rootActivity = root;
    }
}


