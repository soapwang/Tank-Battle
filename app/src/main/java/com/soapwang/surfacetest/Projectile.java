package com.soapwang.surfacetest;

/**
 * Created by soapwang on 2018/5/4.
 */

public class Projectile extends Unit {

    public Projectile(int owner, int x, int y, int direction, int pixelsPerBlock) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.owner = owner;
        speed = (int)(pixelsPerBlock / 10 * 2.5);
        isMoving = true;
    }

}
