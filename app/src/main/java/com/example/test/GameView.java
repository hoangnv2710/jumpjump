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
    private Platform lastPlatform; // Track the last platform the player landed on
    private Handler handler = new Handler();
    private Runnable gameLoop;

    private Bitmap backgroundImage;
    private Bitmap platformBitmap;
    private Bitmap platformBitmapType2;
    private float scrollOffset = 0;
    private float scrollL; // Scroll distance set to 40% of the screen height
    private float scrollSpeedMultiplier = 0.5f; // Variable to control scroll speed
    private static final float PLATFORM_SPACING = 0.2f;
    private Random random;
    private int screenWidth, screenHeight;
    private int platformWidth, platformHeight;
    private int maxJumpX;
    private int maxJumpY;
    private int level = 1;
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

        int playerWidth = screenWidth / 7;
        int playerHeight = screenHeight / 10;
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
                handler.postDelayed(this, 16);  // approximately 60 FPS
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
                    platform.getY() + platform.getBounds().height()*1/4 < player.getY() + player.getBounds().height()
            && platform.getX() <= player.getX() + player.getBounds().width()/2 &&
                    platform.getX() + platform.getBounds().width() >= player.getX() + player.getBounds().width()/2){
//                if (player.isStop()) {
//                    for (Platform plat : platforms) {
//                        plat.setY(plat.getY() + player.getBounds().height() );
//                    }
//                } else player.setY(platform.getY() - player.getBounds().height());
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




                // Only update if the platform is higher than the last one
//                if (lastPlatform == null || platform.getY() < lastPlatform.getY()) {
//                    scrollOffset = scrollL * scrollSpeedMultiplier; // Use scrollSpeedMultiplier
//                    lastPlatform = platform;
//                }
//                platform.setY(platform.getY() + player.getVelocityY());
//                // Adjust the player's position and reset the velocity
//                player.setY(platform.getY() - player.getBounds().height());
//                player.setVelocityY(player.getJumpStrength());
//                break;
//            }
//        }

//        // Apply scrollOffset to the platforms and player position
////        if (scrollOffset > 0) {
////            for (Platform platform : platforms) {
////                platform.setY(platform.getY() + (int) scrollOffset);
////            }
////            player.setY(player.getY() + (int) scrollOffset);
////            scrollOffset = 0; // Reset scrollOffset after applying
////        }
//
//        // Create platforms as needed
//        createPlatform();
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
        lastPlatform = firstPlatform; // Initialize the last platform as the first platform
        player.setY(platformY - player.getBounds().height());
        createPlatform();
        platformWidth = platformBitmap.getWidth();
        platformHeight = platformBitmap.getHeight();
    }

    private void createPlatform() {
        while (platforms.get(platforms.size() - 1).getY() >= maxJumpY / 2  ) {
            int lastPlatformX = platforms.get(platforms.size() - 1).getX();
            int lastPlatformY = platforms.get(platforms.size() - 1).getY();
            int platformX;
            int platformY;

            int rad= random.nextInt( maxJumpX);
            if ( lastPlatformX <= platformWidth) {
                platformX = lastPlatformX + platformWidth + rad;
            } else if (lastPlatformX +  platformWidth >= screenWidth) {
                platformX = lastPlatformX - platformWidth - rad;
            } else {
                int mul = random.nextInt(2);
                rad = (mul == 0) ? - (rad + platformWidth)  : rad + platformWidth;
                platformX = lastPlatformX + rad;
                platformX = platformX < 0 ? 0 : platformX;
                platformX = platformX > screenWidth - platformBitmap.getWidth() ? screenWidth - platformBitmap.getWidth() : platformX;
            }
            platformY= lastPlatformY - maxJumpY;
//

            platforms.add(new Platform(platformBitmap, platformX, platformY,1));
            Log.i("new plat y: ",""+platformY);
        }


//            if (platformY + scrollOffset >= -platformBitmap.getHeight()) {
//                int type = 1;
//                if (type == 1) {
//                    platforms.add(new Platform(platformBitmap, platformX, platformY, type));
//                } else {
//                    int velocityX = random.nextInt(5) + 1;
//                    int minX = 0;
//                    int maxX = screenWidth - platformBitmapType2.getWidth();
//                    platforms.add(new Platform(platformBitmapType2, platformX, platformY, type, velocityX, minX, maxX));
//                }
//            }

    }

    private void adjustBitmapSize(Bitmap platformBitmapOriginal, Bitmap platformBitmapType2Original) {
        if (screenWidth > 0 && screenHeight > 0) {
            int newWidth = screenWidth / 4;
            int newHeight = screenHeight / 20;
            platformBitmap = Bitmap.createScaledBitmap(platformBitmapOriginal, newWidth, newHeight, true);
            platformBitmapType2 = Bitmap.createScaledBitmap(platformBitmapType2Original, newWidth, newHeight, true);
        }
    }
}
