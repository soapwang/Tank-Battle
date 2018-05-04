package com.soapwang.surfacetest;

import android.util.Log;

/**
 * Created by soapwang on 2018/5/2.
 */

public class Tank {
    int type;
    int speed; // pixels moved per tick. one sec. = 60 tick
    int hitPoint;
    int direction; //0=up, 1=right, 2=down, 3=left
    // x,y are the center coordinates of model
    int x;
    int y;

    boolean isMoving;

    /*
     * pixelPerBlock: to calculate how many pixels it should move
     */
    public Tank(int type, int x, int y, int direction, int pixelPerBlock) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = direction;
        hitPoint = 1;
        // pixelPerBlock / 10 is a proper base speed. Change speed by multiplying a scalar.
        speed = (int)((pixelPerBlock / 10) * 1.25);
        Log.d("speed of tank type" + this.type, "" + speed);
        isMoving = false;
    }

    // only move towards current direction
    public void move() {
        switch (direction) {
            case 0:
                y -= speed;
                break;
            case 1:
                x += speed;
                break;
            case 2:
                y += speed;
                break;
            case 3:
                x -= speed;
                break;
        }

    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return  x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setOffset(int x, int y) {
        this.x += x;
        this.y += y;
    }


    public int getY() {
        return y;
    }

    public int getHitPoint() {
        return hitPoint;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setMovingState(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public boolean getMovingState() {
        return isMoving;
    }

}
