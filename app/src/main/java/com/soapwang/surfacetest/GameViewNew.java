package com.soapwang.surfacetest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Random;


public class GameViewNew extends SurfaceView implements SurfaceHolder.Callback{
    public static final String RESUMED = "com.soapwang.surfacetest.resumed";
    public final static boolean STOP = false;
    public final static boolean MOVING = true;
    public final static int UP = 0;
    public final static int RIGHT = 1;
    public final static int DOWN = 2;
    public final static int LEFT = 3;

    private String fpsText = "0.0";
    private Context mContext;
    private SurfaceHolder holder;
    private LoopThread loopThread;
    private Canvas c;
    private Paint paint;

    //game states
    private boolean playing = false;
    private boolean finished = false;

    // data of the play area
    private int playAreaBlockInPixel; // 50 for 1440P
    private int playAreaBlockWidth = 32;
    private int playAreaBlockHight = 24; // a 32*24 map

    //screen data
    private int screenRatio = 16; //16=16:9, 18=18:9  etc.
    private int screenWidth;
    private int screenHeight;
    private int pixelPerBlock; //block size in pixels, 160 for 1440P

    private int playAreaLeft;
    private int playAreaRight;
    private int playAreaTop;
    private int playAreaBottom;


    // components and units
    Rect leftUIRect;
    Rect rightUIRect;
    Tank player;
    ArrayList<Tank> tankList = new ArrayList<Tank>();
    ArrayList<Projectile> ProjectileList = new ArrayList<Projectile>();
    //resources

    Bitmap[] tank1;
    int[] tankDrawable1 = {
            R.drawable.tank1_up,  R.drawable.tank1_right,  R.drawable.tank1_down, R.drawable.tank1_left};

    public GameViewNew(Context context) {
        super(context);
        mContext = context;
        holder = this.getHolder();
        holder.addCallback(this);
        paint = new Paint();

        // loading resources
        tank1 = new Bitmap[4];
        for(int i=0; i<4; i++) {
            Bitmap pic = BitmapFactory.decodeResource(getResources(), tankDrawable1[i]);
            tank1[i]  = pic;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(canvas != null) {
            super.draw(canvas);
            drawBackground(canvas, paint);
            drawTanks(canvas, paint);
            drawFps(canvas, paint);

        }
    }

    public void setFps(double fps) {
        fpsText = new Formatter().format("%.1f", fps).toString();
    }

    //a sort of draw methods
    private void drawFps(Canvas c, Paint p) {
        p.setColor(Color.GREEN);
        p.setTextSize(48);
        c.drawText("FPS: " + fpsText, 10, 40, p);
    }

    private void drawBackground(Canvas c, Paint p) {
        //first the main background
        p.setColor(Color.GRAY);
        c.drawRect(leftUIRect, p);
        c.drawRect(rightUIRect, p);
        // leave the play area at this moment, cuz it's black
        // we don't need to draw black
    }

    private void drawTanks(Canvas c, Paint p) {
        for(Tank t : tankList) {
            if(t.getHitPoint() > 0) {
                int direction = t.getDirection();
                int left = t.getX() - playAreaBlockInPixel;
                int right = t.getX() + playAreaBlockInPixel;
                int top = t.getY() - playAreaBlockInPixel;
                int bottom = t.getY() + playAreaBlockInPixel;
                Rect tankRect = new Rect(left, top, right, bottom);
                c.drawBitmap(tank1[direction], null, tankRect, null);
            }
        }
    }
    //draw methods end

    public void collisionDetect() {

    }

    public int boundaryDetect(Tank t) {
        if(t.getX() < playAreaLeft + playAreaBlockInPixel) {
            t.setOffset(1, 0);
            return 1; // reach left
        }

        else if(t.getX() > playAreaRight - playAreaBlockInPixel) {
            t.setOffset(-1, 0);
            return 2; // reach right
        }
        else if(t.getY() < playAreaTop + playAreaBlockInPixel) {
            t.setOffset(0, 1);
            return 3; // reach top
        }
        else if(t.getY() > playAreaBottom - playAreaBlockInPixel) {
            t.setOffset(1, -1);
            return 4; // reach bottom
        }
        else
            return 0;
    }

    // game logic updates here
    public void updateStates() {
        Tank player = tankList.get(0);
        if(player.isMoving && boundaryDetect(player) == 0)
            player.move();
    }

    // methods for controls
    public void movePlayer(int dir, boolean isMoving) {
        Tank player = tankList.get(0);
        // int currentDirection = player.getDirection();
        player.setDirection(dir);
        player.setMovingState(isMoving);
    }

    public void fire() {
        Log.d("fire", "fire!");
    }

    public void pause(){
        playing = false;
        loopThread.setRunning(false);
    }

    public void notifyMainActivity(String action) {
        Intent i = new Intent(action);
        mContext.sendBroadcast(i);
    }

    public void resume() {
        playing = true;
        loopThread = new LoopThread(this);
        loopThread.setRunning(true);
        loopThread.start();
        notifyMainActivity(RESUMED);
    }

    public void restart() {
        playing = true;
        finished = false;
        player = new Tank(1, screenWidth/2, screenHeight/2, 0, playAreaBlockInPixel);
        tankList.add(player);
        loopThread = new LoopThread(this);
        loopThread.setRunning(true);
        loopThread.start();

    }

    // get screen width & height here
    public void surfaceCreated(SurfaceHolder holder) {
        Rect surfaceFrame = holder.getSurfaceFrame();
        screenWidth = surfaceFrame.width();
        screenHeight = surfaceFrame.height();
        Log.d("W and H", screenWidth + ", " + screenHeight);
        pixelPerBlock = screenWidth / screenRatio;
        Log.d("pixelPerBlock:", ""+pixelPerBlock);
        int sideSpace = (screenRatio - 12) / 2; //sideSpace in blocks
        playAreaLeft = sideSpace * pixelPerBlock; // we have a 4:3 game area
        playAreaRight = screenWidth - sideSpace*pixelPerBlock;
        playAreaTop = 0;
        playAreaBottom = screenHeight;

        leftUIRect = new Rect(0, 0, pixelPerBlock*sideSpace, screenHeight);
        rightUIRect = new Rect(screenWidth - pixelPerBlock*sideSpace, 0, screenWidth, screenHeight);
        playAreaBlockInPixel = (playAreaRight - playAreaLeft) / playAreaBlockWidth;
        Log.d("playAreaBlockInPixel", "" + playAreaBlockInPixel);
        restart();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int screenWidth,
                               int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        playing = false;
        loopThread.setRunning(false);
        while (retry) {
            try {
                loopThread.sleep(100);
                loopThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
