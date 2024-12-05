package com.example.test;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public class Player {
    private Bitmap bitmap; // bitmap hiện tại của player
    private Bitmap bitmapOnPlatform; // bitmap khi player đứng trên bệ
    private Bitmap bitmapInAir; // bitmap khi player đang nhảy trên không
    private Bitmap[] Player_die;
    private long lastStateChangeTime; // Thời gian của lần thay đổi trạng thái cuối cùng
    private static final long STATE_DURATION = 200; // Thời gian tồn tại của mỗi trạng thái (0.1s)
    private int[] stateSequence = {0, 1}; // Thứ tự các trạng thái
    private int sequenceIndex = 0; // Chỉ số của trạng thái hiện tại trong stateSequence
    private int x;
    private int y;
    private int velocityY;

    private long lastOnPlatformTime = 0; // Lưu thời điểm cuối cùng khi chuyển sang OnPlatform
    private static final long ON_PLATFORM_DURATION = 100; // Thời gian giữ trạng thái OnPlatform (ms)
    private boolean isFacingRight = true; // true: hướng phải, false: hướng trái
    private boolean isDie = false;
    private int state; // 0: on board, 1: in air

    private int screenWidth;
    private int screenHeight;
    private int gravity = -1;
    private int jumpStrength = 20;
    private int speedX = 10;
    private int maxJumpX;
    private int maxJumpY;

    public Player(Bitmap bitmapOnPlatform, Bitmap bitmapInAir, int screenWidth, int screenHeight, Context context) {
        this.bitmapOnPlatform = bitmapOnPlatform;
        this.bitmapInAir = bitmapInAir;
        this.bitmap = bitmapOnPlatform; // player đứng trên bệ khi bắt đầu trò chơi
        this.state = 0;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = (screenWidth - bitmap.getWidth()) / 2;
        // Convert 3cm to pixels
        float distanceFromBottomInCm = 3.0f;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int distanceFromBottomInPx = (int) (distanceFromBottomInCm * metrics.densityDpi / 2.54f);
        this.y = screenHeight - distanceFromBottomInPx;
        this.velocityY = jumpStrength;
        int g = (gravity > 0) ? gravity : -gravity;
        maxJumpX = jumpStrength / g * speedX;
        maxJumpY = (jumpStrength / g) * (jumpStrength / g) * 1 / 2 * g;

        Bitmap Player_die1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        Bitmap Player_die2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.player1);
        int playerWidth = screenWidth / 8;
        int playerHeight = screenHeight / 12;
        Player_die1 = Bitmap.createScaledBitmap(Player_die1, playerWidth, playerHeight, false);
        Player_die2 = Bitmap.createScaledBitmap(Player_die2, playerWidth, playerHeight, false);
        Player_die = new Bitmap[]{Player_die1, Player_die2};
    }

    public boolean isStop() {
        if (y <= maxJumpY + bitmap.getHeight() && velocityY >= 0) {
            return true;
        }
        return false;
    }

    private Bitmap flipBitmap(Bitmap original, boolean horizontal) {
        Matrix matrix = new Matrix();
        if (horizontal) {
            matrix.setScale(-1, 1); // Lật ngang
            matrix.postTranslate(original.getWidth(), 0); // Dịch chuyển để không bị mất hình ảnh
        }
        return Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
    }

    public void update() {
        if (velocityY > 0) {
            setState(1);
        } else {
            setState(0);
        }

        if (!isStop()) {
            y -= velocityY;
        }
        velocityY += gravity;
    }

    public void moveLeft() {
        x -= speedX;
        if (isFacingRight) { // Nếu đang hướng phải, lật sang trái
            bitmap = flipBitmap(bitmap, true);
            isFacingRight = false;
        }
        if (x < -bitmap.getWidth() / 2) {
            x = screenWidth - bitmap.getWidth() / 2;
        }
    }

    public void moveRight() {
        x += speedX;
        if (!isFacingRight) { // Nếu đang hướng trái, lật sang phải
            bitmap = flipBitmap(bitmap, true);
            isFacingRight = true;
        }
        if (x > screenWidth - bitmap.getWidth() / 2) {
            x = -bitmap.getWidth() / 2;
        }
    }

    public void draw(Canvas canvas) {
        if (isDie) {
            // Xử lý animation chết
            long currentTime = SystemClock.elapsedRealtime();
            if (currentTime - lastStateChangeTime > STATE_DURATION) {
                sequenceIndex = (sequenceIndex + 1) % Player_die.length;
                lastStateChangeTime = currentTime;
            }
            canvas.drawBitmap(Player_die[sequenceIndex], x, y, null);
        } else {
            // Vẽ player bình thường
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    public void setState(int newState) {
        if (this.state == newState) {
            return; // Không thay đổi trạng thái nếu giống nhau
        }
        this.state = newState;
        if (state == 0) { // Trạng thái đứng trên bệ
            bitmap = isFacingRight ? bitmapOnPlatform : flipBitmap(bitmapOnPlatform, true);
        } else { // Trạng thái ở trên không
            bitmap = isFacingRight ? bitmapInAir : flipBitmap(bitmapInAir, true);
        }
    }

    public int getState() {
        return state;
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
    public void setJumpStrength(int jumpStrength) {
        this.jumpStrength = jumpStrength;
    }

    public void setJumpStrength(int jumpStrength) {
        this.jumpStrength = jumpStrength;
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
    public void setX(int x) {
        this.x = x;
    }

    public void setX(int x) {
        this.x = x;
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

    public void reset(int screenWidth, int screenHeight) {
        this.x = (screenWidth - bitmap.getWidth()) / 2;
        this.y = screenHeight - (int) (3.0f * screenHeight / 2.54f / 2.54f);
        this.velocityY = jumpStrength;
    }

    public void setIsDie(boolean isDie) {
        this.isDie = isDie;
        if (isDie) {
            sequenceIndex = 0; // Bắt đầu từ frame đầu tiên của animation
            lastStateChangeTime = SystemClock.elapsedRealtime(); // Reset thời gian
        }
    }
}
