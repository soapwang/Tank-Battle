package com.soapwang.surfacetest;

/**
 * Created by soapwang on 2018/5/4.
 */

public class Projectile {
    int x;
    int y;
    int direction;
    int speed;
    int pixelsPerBlock;

    public Projectile(int x, int y, int direction, int pixelsPerBlock) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        speed = pixelsPerBlock / 5;
    }

    public void move() {

    }
}
