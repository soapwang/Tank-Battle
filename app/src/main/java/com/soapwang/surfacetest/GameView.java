//package com.soapwang.surfacetest;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.Vibrator;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import java.util.Formatter;
//import java.util.Random;
//
//
///**
// * Created by Acer on 2016/9/16.
// * Add a FPS method.
// */
//public class GameView extends SurfaceView implements SurfaceHolder.Callback{
//    public static final String RESUMED = "com.soapwang.surfacetest.resumed";
//
//    private String fpsText = "0.0";
//    private boolean playing = false;
//    private Context mContext;
//    private SurfaceHolder holder;
//    private LoopThread loopThread;
//    private Canvas c;
//    private Bitmap floor1;
//    private Bitmap bmp;
//    private Rect floorRect;
//    private int x = 0;
//    private int y = 0;
//    private int screenWidth;
//    private int screenHeight;
//    private int displayXSpeed = -100;
//    private int skyBlue; //Color
//    private int brickYSize = 0;
//    private int brickXSize = 50;
//    private int brickX;
//    private int brickY;
//    private int score = -1;
//    private Paint paint;
//    long lastUpdateTime = 0;
//    private CountDownTimer timer;
//    private int remainTime = 60;
//    private boolean finished = false;
//    private int gravityFactor = 3;
//    //When it goes fast, the gap between two obstacle is increased.
//    private int obstacleResetOffset = 0;
//    //Avatar animation.
//    private int bobX;
//    private int bobY;
//    private int bobYSpeed = 0;
//    private int frameWidth = 200;
//    private int frameHeight = 200;
//    private int frameCount = 5;
//    private int currentFrame = 0;
//    private long lastFrameChangeTime = 0;
//    private int frameLengthInMillisec = 60;
//    private int frameSpeedUpCount = 0;
//    private Bitmap bitmapBob;
//    private Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
//    private Rect whereToDraw = new Rect(bobX, 0, bobX+frameWidth, frameHeight);
//    private boolean onGround = true;
//    private int collisionOffset = 60;
//    private boolean hitState = false;
//
//
//    //Scoring.
//    private boolean jumpOverABrick = false;
//    //Handling long click jump.
//    private boolean highJumped = false;
//    private final Handler handler = new Handler();
//    private Runnable mLongPressed = new Runnable() {
//        public void run() {
//            //bobHighJump();
//            highJumped = true;
//        }
//    };
//
//
//    public GameView(Context context) {
//        super(context);
//        mContext = context;
//        holder = this.getHolder();
//        holder.addCallback(this);
//        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.grass_floor);
//        skyBlue = ContextCompat.getColor(context, R.color.skyblue);
//        paint = new Paint();
//        bitmapBob = BitmapFactory.decodeResource(getResources(), R.drawable.bob);
//        bitmapBob = Bitmap.createScaledBitmap(bitmapBob, frameWidth*frameCount,frameHeight,false);
//    }
//
//    protected void onDraw(Canvas canvas) {
//        if(canvas != null) {
//            long time = System.currentTimeMillis();
//            if(time - lastUpdateTime >= 100) {
//                increaseGameSpeed();
//                lastUpdateTime = time;
//            }
//            paint.setColor(skyBlue);
//            paint.setStyle(Paint.Style.FILL);
//            //Draw background.
//            canvas.drawPaint(paint);
//            //Draw fps.
//            drawFps(canvas, paint);
//            //Draw level.
//            int xSpeed = displayXSpeed / 10;
//            if (x <= 0 - floor1.getWidth()/2)
//                x = 0;
//            x = x + xSpeed;
//            if(brickX >= 0 - brickXSize)
//                brickX += xSpeed;
//            else
//                resetObstacle();
//            canvas.drawBitmap(floor1, x, y, null);
//            setBobY();
//            drawObstacle(canvas, paint);
//            drawAvatar(canvas, paint);
//            drawScore(canvas, paint);
//            drawSpeedBoost(canvas, paint);
//            drawTime(canvas, paint);
//            hitDetect();
//            if(hitState && !finished) {
//                displayXSpeed = -100;
//                resetObstacle();
//            }
//            else if(finished) {
//                drawTime(canvas,paint);
//                pause();
//            }
//            else {
//                gainScore();
//            }
//        }
//    }
//
//    public void setFps(double fps) {
//        fpsText = new Formatter().format("%.1f", fps).toString();
//    }
//
//    public void setTime(long time) {
//        remainTime = (int) time;
//    }
//
//    private void resetObstacle() {
//        brickYSize = new Random().nextInt(200 + 1)  + 125;
//        brickX = screenWidth +  obstacleResetOffset;
//        brickY = y - brickYSize;
//        jumpOverABrick = false;
//    }
//
//    private void gainScore() {
//        if(bobX > (brickX+brickXSize) && !jumpOverABrick) {
//            score++;
//            jumpOverABrick = true;
//        }
//    }
//
//    private void increaseGameSpeed() {
//        if(displayXSpeed > -300) {
//            displayXSpeed -= 1;
//            obstacleResetOffset += new Random().nextInt(5);
//        }
//        frameSpeedUpCount++;
//        if((frameLengthInMillisec >= 30) && (frameSpeedUpCount >= 10)) {
//            frameLengthInMillisec -= 3;
//            frameSpeedUpCount = 0;
//        }
//    }
//
//    private void vibrate() {
//        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
//        v.vibrate(150);
//    }
//
//    private void hitDetect() {
//        hitState = false;
//        //handler.removeCallbacks(mLongPressed);
//        if(((whereToDraw.right - collisionOffset) >= brickX) && ((
//                whereToDraw.left + collisionOffset) <= (brickX + brickXSize))) {
//            if (whereToDraw.bottom-collisionOffset >= brickY) {
//                hitState = true;
//                vibrate();
//                displayXSpeed = -100;
//                frameLengthInMillisec = 60;
//            }
//        }
//    }
//    //Control Bob's jump.
//    private void setBobY() {
//        bobYSpeed += gravityFactor;
//        if(bobY < y - frameHeight) {
//            bobY += bobYSpeed;
//        } else {
//            bobY = y - frameHeight;
//            onGround = true;
//            bobYSpeed = 0;
//        }
//    }
//
//    private void drawFps(Canvas c, Paint p) {
//        p.setColor(Color.BLACK);
//        p.setTextSize(48);
//        c.drawText("FPS: " + fpsText, 10, 40, p);
//    }
//
//    private void drawAvatar(Canvas c, Paint p) {
//        whereToDraw.set(bobX, bobY, bobX + frameWidth, bobY + frameHeight);
//        getCurrentFrame();
//        c.drawBitmap(bitmapBob, frameToDraw, whereToDraw, p);
//    }
//
//    private void drawObstacle(Canvas c, Paint p) {
//        int colorInt = ContextCompat.getColor(mContext, R.color.brick);
//        p.setColor(colorInt);
//        Rect r = new Rect(brickX,  brickY, brickX+ brickXSize, this.y);
//        c.drawRect(r,p);
//    }
//
//    private void drawScore(Canvas c, Paint p) {
//        p.setColor(Color.BLACK);
//        p.setTextSize(72);
//        c.drawText("SCORE: " + score, screenWidth/2 - 216, 300, p);
//    }
//
//    private void drawSpeedBoost (Canvas c, Paint p) {
//        p.setColor(Color.BLACK);
//        p.setTextSize(72);
//        c.drawText("SPEED: +" + (-1*displayXSpeed - 100), screenWidth/2 - 216, 400, p);
//    }
//
//    private void drawTime (Canvas c, Paint p) {
//        p.setColor(Color.BLACK);
//        p.setTextSize(72);
//        c.drawText("TIME: "+remainTime, screenWidth/2 - 216, 550, p);
//    }
//
//    public void getCurrentFrame() {
//        long time = System.currentTimeMillis();
//        if(time > lastFrameChangeTime + frameLengthInMillisec) {
//            lastFrameChangeTime = time;
//            if(onGround)
//                currentFrame++;
//            else
//                currentFrame = 4;
//            if(currentFrame >= frameCount)
//                currentFrame = 0;
//        }
//
//        frameToDraw.left = currentFrame*frameWidth;
//        frameToDraw.right = frameToDraw.left + frameWidth;
//    }
//
//    public void jump() {
//        if(onGround) {
//            onGround = false;
//            bobY -= 5;
//            bobYSpeed = -48;
//        }
//    }
//
//    public void pause(){
//        playing = false;
//        loopThread.setRunning(false);
//        Log.d("soapwang.gameview", "bobY="+bobY);
//    }
//
//    public void notifyMainActivity(String action) {
//        Intent i = new Intent(action);
//        mContext.sendBroadcast(i);
//    }
//
//    public void resume() {
//        playing = true;
//        loopThread = new LoopThread(this);
//        loopThread.setRunning(true);
//        loopThread.start();
//        notifyMainActivity(RESUMED);
//    }
//
//    public void restart() {
//        playing = true;
//        timer = new CountDownTimer(30000, 1000) {
//            @Override
//            public void onTick(long l) {
//                setTime(l / 1000);
//            }
//
//            @Override
//            public void onFinish() {
//                setTime(0);
//                finished = true;
//                //pause();
//            }
//        }.start();
//        score = 0;
//        displayXSpeed = -100;
//        frameLengthInMillisec = 60;
//        resetObstacle();
//        finished = false;
//        loopThread = new LoopThread(this);
//        loopThread.setRunning(true);
//        loopThread.start();
//    }
//
//    public boolean onTouchEvent(MotionEvent motionEvent) {
//
//        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//
//            // Player has touched the screen
//            case MotionEvent.ACTION_DOWN:
//                if(playing) {
//                    jump();
//                }
//                else {
//                    if(finished) {
//                        restart();
//                    }
//                    else {
//                        resume();
//                    }
//                }
//                break;
//
//            // Player has removed finger from screen
//            case MotionEvent.ACTION_UP:
//                handler.removeCallbacks(mLongPressed);
//                if(!highJumped)
//                    ;
//                break;
//        }
//        return true;
//    }
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        Rect surfaceFrame = holder.getSurfaceFrame();
//        screenWidth = surfaceFrame.width();
//        screenHeight = surfaceFrame.height();
//        floor1 = Bitmap.createScaledBitmap(bmp,2*screenWidth, 2*screenWidth/12, false);
//        y = (screenHeight-floor1.getHeight()-100);
//        bobX = screenWidth/2 -(frameWidth/2);
//        bobY = y - frameHeight;
//        restart();
//
//    }
//
//    public void surfaceChanged(SurfaceHolder holder, int format, int screenWidth,
//                               int height) {
//
//    }
//
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        boolean retry = true;
//        loopThread.setRunning(false);
//        while (retry) {
//            try {
//                loopThread.sleep(100);
//                loopThread.join();
//                retry = false;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
