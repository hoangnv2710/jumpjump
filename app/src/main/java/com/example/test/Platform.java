package com.example.test;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Platform {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int type;
    private int velocityX; // Velocity for horizontal movement
    private int minX, maxX; // Boundaries for horizontal movement
    private static int level;
    public Platform(Bitmap bitmap, int x, int y, int type) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public Platform(Bitmap bitmap,int x, int y, int type, int velocityX, int minX, int maxX) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.type = type;
        this.velocityX = velocityX;
        this.minX = minX;
        this.maxX = maxX;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public Rect getBounds() {
        return new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setType(int type) {
        this.type = type;
    }

//    public static int getLevel() {
//        return level;
//    }

    public static int getLevel(int platformPassed) {
        if (platformPassed < 15) {
            level = 1;
        } else if (platformPassed < 20) {
            level = 2;
        } else if (platformPassed < 25){
            level = 3;
        }
        else if (platformPassed < 30){
            level = 4;
        }
        else if (platformPassed < 35){
            level = 5;
        }
        else if (platformPassed < 40){
            level = 6;
        } else {
            level = 7;
        }
        return level;
    }

    // Method to update position for moving platforms
    public void updateP() {
        if (type == 2) {
            x += velocityX;
            if (x <= minX || x >= maxX) {
                velocityX = -velocityX; // Reverse direction
            }
        }
    }
}
