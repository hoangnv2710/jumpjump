package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public class Player {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int velocityY;
    private int maxY;
    private int minY;
    private int screenWidth;
    private int screenHeight;
    private int gravity = -2;
    private int jumpStrength = 30;
    private int speedX = 10;
    private int maxJumpX;
    private int maxJumpY;
    public Player(Bitmap bitmap, int screenWidth, int screenHeight, Context context) {
        this.bitmap = bitmap;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.x = (screenWidth - bitmap.getWidth()) / 2;

        // Convert 3cm to pixels
        float distanceFromBottomInCm = 3.0f;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int distanceFromBottomInPx = (int) (distanceFromBottomInCm * metrics.densityDpi / 2.54f);

        this.y = screenHeight - distanceFromBottomInPx;
        this.maxY = screenHeight - bitmap.getHeight();
        this.minY = 0;
        this.velocityY = jumpStrength;
        int g = (gravity > 0) ? gravity : -gravity;
        maxJumpX = jumpStrength/g * speedX;
        maxJumpY = (jumpStrength/g) * (jumpStrength/g) * 1/2 * g;
    }

    public boolean isStop() {
        if(y <= maxJumpY  + bitmap.getHeight() && velocityY >= 0) {
            return true;
        }
        return false;
    }

    public void update() {

        if (!isStop()) {
            y -= velocityY;  // Update player’s vertical position
        }

        velocityY += gravity;
//        if (y > maxY) {
//            y = maxY;
//            velocityY = jumpStrength;
//        }
//
//        if (y < minY) {
//            y = minY;
//            velocityY = jumpStrength;
//        }
    }


    public void moveLeft() {
        x -= speedX;
        if (x < 0) {
            x = screenWidth;
        }
    }

    public void moveRight() {
        x += speedX;
        if (x > screenWidth) {
            x = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public Rect getBounds() {
        return new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

    public int getMaxJumpX() {
        return maxJumpX;
    }

    public void setMaxJumpX(int maxJumpX) {
        this.maxJumpX = maxJumpX;
    }

    public int getMaxJumpY() {
        return maxJumpY;
    }

    public void setMaxJumpY(int maxJumpY) {
        this.maxJumpY = maxJumpY;
    }

    public int getY() {
        return y;
    }

    public int getJumpStrength() {
        return jumpStrength;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public int getGravity() {
        return this.gravity;
    }

    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }
}
