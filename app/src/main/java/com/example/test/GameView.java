package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private Player player;
    private List<Platform> platforms;
    private Handler handler = new Handler();
    private Runnable gameLoop;
    private Bitmap backgroundImage;
    private Bitmap platformBitmap;
    private Bitmap platformBitmapType2;
    private Random random;
    private int screenWidth, screenHeight;
    private int platformWidth, platformHeight;
    private int maxJumpX;
    private int maxJumpY;
    private int passed = 0;
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // Set scrollL to 40% of the screen height

        Bitmap playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        Bitmap platformBitmapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.lognot);
        Bitmap platformBitmapType2Original = BitmapFactory.decodeResource(getResources(), R.drawable.lognot);
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.back_ing);
        backgroundImage = Bitmap.createScaledBitmap(backgroundImage, screenWidth, screenHeight, true);

        int playerWidth = screenWidth / 8;
        int playerHeight = screenHeight / 12;

        playerBitmap = Bitmap.createScaledBitmap(playerBitmap, playerWidth, playerHeight, false);
        adjustBitmapSize(platformBitmapOriginal, platformBitmapType2Original);

        player = new Player(playerBitmap, screenWidth, screenHeight, context);
        maxJumpX = player.getMaxJumpX();
        maxJumpY = player.getMaxJumpY();
        Log.i("MAXY",""+maxJumpY);
        platforms = new ArrayList<>();
        random = new Random();

        createFirstPlatform();

        gameLoop = new Runnable() {
            @Override
            public void run() {
                update();
                drawGame();
                handler.postDelayed(this, 10);  // approximately 60 FPS
            }
        };
    }

    public void update() {
        player.update();

        // Check collision with platforms
        int count = 0;
        while (count < platforms.size()) {
            Platform platform = platforms.get(count);
            if (player.getBounds().intersect(platform.getBounds()) && player.getVelocityY() <= 0 &&
                    platform.getY() + platformHeight > player.getY() + player.getBounds().height()
            && platform.getX() <= player.getX() + player.getBounds().width()/2 &&
                    platform.getX() + platform.getBounds().width() >= player.getX() + player.getBounds().width()/2){
                player.setY(platform.getY() - player.getBounds().height());
                player.setVelocityY(player.getJumpStrength());
            }
            if (player.isStop()) {
                platform.setY(platform.getY() + player.getVelocityY());
            }
            if (platform.getY() >= screenHeight - platform.getBounds().height()) {
                platforms.remove(count);
                count --;
                createPlatform();
            }
            count ++;
        }
    }

    public void drawGame() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawBitmap(backgroundImage, 0, 0, null);

            // Draw all platforms
            for (Platform platform : platforms) {
                platform.draw(canvas);
            }

            // Draw the player
            player.draw(canvas);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void movePlayerLeft() {
        player.moveLeft();
    }

    public void movePlayerRight() {
        player.moveRight();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        handler.post(gameLoop);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        handler.removeCallbacks(gameLoop);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    private void createFirstPlatform() {
        int platformX = screenWidth / 2 - platformBitmap.getWidth() / 2;
        int platformY = screenHeight - 300;
        Platform firstPlatform = new Platform(platformBitmap, platformX, platformY, 1);
        platforms.add(firstPlatform);
        player.setY(platformY - player.getBounds().height());
        platformWidth = platformBitmap.getWidth();
        platformHeight = platformBitmap.getHeight();
        createPlatform();
    }

    private void createPlatform() {
        while (platforms.get(platforms.size() - 1).getY() >= maxJumpY/3) {
            int lastPlatformX = platforms.get(platforms.size() - 1).getX();
            int lastPlatformY = platforms.get(platforms.size() - 1).getY();
            int platformX;
            int platformY;
            int level = Platform.getLevel(passed);
            
            int rad = random.nextInt(2 * maxJumpX);
            if ( lastPlatformX <= platformWidth) {
                platformX = lastPlatformX + platformWidth + rad;
            } else if (lastPlatformX +  2 * platformWidth >= screenWidth) {
                platformX = lastPlatformX - platformWidth - rad;
            } else {
                int mul = random.nextInt(2);
                rad = (mul == 0) ? - (rad + platformWidth)  : rad + platformWidth;
                platformX = lastPlatformX + rad;
            }
            platformX = platformX < 0 ? 0 : platformX;
            platformX = platformX + platformWidth > screenWidth ? screenWidth - platformWidth : platformX;

            int diffX = platformX > lastPlatformX ? platformX - lastPlatformX : lastPlatformX - platformX;
            diffX -= platformWidth;
            int maxY = diffX <= maxJumpX ? maxJumpY :
                    player.getJumpStrength() * (diffX / player.getSpeedX()) + player.getGravity() * (diffX / player.getSpeedX()) * (diffX / player.getSpeedX()) / 2;
            rad =  maxY /(random.nextInt( 8 - level ) + 1) ;
            platformY = lastPlatformY - rad - platformHeight ;
            // platforms.add(new Platform(platformBitmap, platformX, platformY,2,10,0,screenWidth - platformWidth));
            platforms.add(new Platform(platformBitmap, platformX, platformY,1));

            Log.i("rad",""+rad + "," + (platformY - lastPlatformY) + "," + maxJumpY + "," + platformHeight);
            passed ++;
//            for(int i = 0; i < platforms.size(); i++){
//                Log.i("plat x,y: ",i + ": " + platforms.get(i).getX() +" , "+ platforms.get(i).getY());
//            }

        }

    }

    private void adjustBitmapSize(Bitmap platformBitmapOriginal, Bitmap platformBitmapType2Original) {
        if (screenWidth > 0 && screenHeight > 0) {
            int newWidth = screenWidth / 6;
            int newHeight = screenHeight / 30;
            platformBitmap = Bitmap.createScaledBitmap(platformBitmapOriginal, newWidth, newHeight, true);
            platformBitmapType2 = Bitmap.createScaledBitmap(platformBitmapType2Original, newWidth, newHeight, true);
        }
    }
}
