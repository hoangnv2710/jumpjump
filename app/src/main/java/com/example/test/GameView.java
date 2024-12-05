package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.MediaPlayer;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private Player player;
    private List<Platform> platforms;
    private List<Monster> monsters;
    private Handler handler = new Handler();
    private Runnable gameLoop;
    private Bitmap backgroundImage;
    private Bitmap platformBitmap;
    private Bitmap platformBitmapType2;
    private Bitmap heartBitmap;
    private Random random;
    private int screenWidth, screenHeight;
    private int platformWidth, platformHeight;
    private int maxJumpX;
    private int maxJumpY;
    private int passed = 0;
    private int level = 0;
    private static int score = 0;
    private int maxLevel = 5;
    private MediaPlayer backgroundMusic;
    boolean isOver = false;
    private Platform lastTouchedPlatform = null;
    private Platform lastTouchedPlatform_score = null;
    private Platform lastTouchedPlatform_type1 = null;
    private int life = 3; // Biến đếm số mạng của người chơi
    private Paint lifePaint = new Paint();
    private Paint scorePaint = new Paint();
    private MainActivity mainActivity;

    public GameView(Context context) {
        super(context);
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;  // Lưu tham chiếu đến MainActivity
        }
        getHolder().addCallback(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        initBackgroundMusic(context);
        monsters = new ArrayList<>();

        Bitmap playerBitmapOnPlatform = BitmapFactory.decodeResource(getResources(), R.drawable.player_on_platform);
        Bitmap playerBitmapInAir = BitmapFactory.decodeResource(getResources(), R.drawable.player_in_air);

        Bitmap platformBitmapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.blue_cloud);
        Bitmap platformBitmapType2Original = BitmapFactory.decodeResource(getResources(), R.drawable.blue_cloud);
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.back_ing_1);
        backgroundImage = Bitmap.createScaledBitmap(backgroundImage, screenWidth, screenHeight, true);

        // Sử dụng font từ thư mục res/font
        Typeface customFont = ResourcesCompat.getFont(context, R.font.font_life);

        if (customFont != null) {
            lifePaint.setTypeface(customFont);
        } else {
            Log.e("GameView", "Custom font could not be loaded!");
        }

        lifePaint.setColor(Color.BLACK);
        lifePaint.setTextSize(screenWidth/10);
        lifePaint.setAntiAlias(true);

        Typeface customFontScore = ResourcesCompat.getFont(context, R.font.font_score);

        if (customFontScore != null) {
            scorePaint.setTypeface(customFontScore);
        } else {
            Log.e("GameView", "Custom font for scorePaint could not be loaded!");
        }

        scorePaint.setColor(Color.BLACK); // Đổi màu theo ý muốn
        scorePaint.setTextSize(screenWidth / 10); // Kích thước chữ cho điểm số
        scorePaint.setAntiAlias(true);


        int playerWidth = screenWidth / 8;
        int playerHeight = screenHeight / 12;
        playerBitmapOnPlatform = Bitmap.createScaledBitmap(playerBitmapOnPlatform, playerWidth, playerHeight, false);
        playerBitmapInAir = Bitmap.createScaledBitmap(playerBitmapInAir, playerWidth, playerHeight, false);

        //Platform bitmap scale
        int platform1Width = screenWidth / 6;
        int platform1Height = screenHeight / 30;
        platformBitmap = Bitmap.createScaledBitmap(platformBitmapOriginal, platform1Width, platform1Height, true);
        platformBitmapType2 = Bitmap.createScaledBitmap(platformBitmapType2Original, platform1Width, platform1Height, true);

        player = new Player(playerBitmapOnPlatform, playerBitmapInAir, screenWidth, screenHeight, context);
        maxJumpX = player.getMaxJumpX();
        maxJumpY = player.getMaxJumpY();
        Log.i("MAXY", "" + maxJumpY);
        platforms = new ArrayList<>();
        random = new Random();
        createFirstPlatform();

        // Tải hình heart và làm nhỏ kích thước 10 lần
        Bitmap heartBitmapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
        int heartWidth = heartBitmapOriginal.getWidth() / 10;
        int heartHeight = heartBitmapOriginal.getHeight() / 10;
        heartBitmap = Bitmap.createScaledBitmap(heartBitmapOriginal, heartWidth, heartHeight, true);

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
        if (!isOver) {
            if (player.getVelocityY() <= -platformHeight) {
                player.setVelocityY(-platformHeight + 1);
            }
            player.update();

            int count = 0;
            while (count < platforms.size()) {
                Platform platform = platforms.get(count);

                // Update moving platforms
                if (platform.getType() == 2) {
                    platform.updateP(); // Move the platform horizontally
                }

                if (player.getBounds().intersect(platform.getBounds()) && player.getVelocityY() <= 0 &&
                        platform.getY() + platformHeight > player.getY() + player.getBounds().height() &&
                        platform.getX() <= player.getX() + player.getBounds().width() / 2 &&
                        platform.getX() + platform.getBounds().width() >= player.getX() + player.getBounds().width() / 2) {
                    if (platform != lastTouchedPlatform) {
                        if(platform.getY() < lastTouchedPlatform_score.getY()) {
                            lastTouchedPlatform_score = platform;
                            score++; // Tăng điểm
                        }
                        if(platform.getType() == 1) {
                            lastTouchedPlatform_type1 = platform;
                        }
                        lastTouchedPlatform = platform; // Cập nhật platform cuối cùng
                        // Tạo Monster mới với xác suất 10%
                        if (random.nextInt(5) == 0) { // 10% xác suất
                            Monster newMonster = new Monster(getContext(), screenWidth, screenHeight);
                            newMonster.createMonster();
                            monsters.add(newMonster);
                        }
                    }
                    player.setY(platform.getY() - player.getBounds().height());
                    player.setVelocityY(player.getJumpStrength());
                }

                if (player.isStop()) {
                    platform.setY(platform.getY() + player.getVelocityY());
                }

                if (platform.getY() >= screenHeight - platform.getBounds().height()) {
                    platforms.remove(count);
                    count--;
                    createPlatform(); // Create a new platform
                }
                count++;
            }

            // Cập nhật vị trí các quái vật và kiểm tra va chạm
            for (int i = 0; i < monsters.size(); i++) {
                Monster monster = monsters.get(i);
                if (monster.updateM()) {
                    monsters.remove(i); // Xóa Monster khỏi danh sách nếu nó ra khỏi màn hình
                    i--; // Điều chỉnh chỉ số để tránh bỏ qua phần tử tiếp theo
                } else if (player.getBounds().intersect(monster.getBounds())) {
                    life--; // Giảm mạng sống khi va chạm với quái vật
                    if (life > 0) {
                        resetGame();
                    } else {
                        gameOver();
                        isOver = true;
                    }
                }
            }

            if (player.getY() > screenHeight) {
                life--; // Giảm mạng sống khi người chơi rơi xuống dưới màn hình
                if (life > 0) {
                    resetGame();
                } else {
                    gameOver();
                    isOver = true;
                }
            }
        }
    }


    public void drawGame() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawBitmap(backgroundImage, 0, 0, null);

            // Vẽ các platform
            for (Platform platform : platforms) {
                platform.draw(canvas);
            }

            // Vẽ player
            player.draw(canvas);

            // Vẽ các quái vật
            for (Monster monster : monsters) {
                monster.draw(canvas); // Giả sử Monster có phương thức draw(Canvas)
            }

            // Vẽ hình heart đã làm nhỏ ở góc trên cùng bên trái
            int heartX = 20; // Cách lề trái 20px
            int heartY = 20; // Cách lề trên 20px
            canvas.drawBitmap(heartBitmap, heartX, heartY, null);
            canvas.drawText("x" + life, heartX + heartBitmap.getWidth(), heartY + heartBitmap.getHeight(), lifePaint);
            canvas.drawText("score: " + score * 50, screenWidth / 2, heartY + heartBitmap.getHeight(), lifePaint);

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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    private void createFirstPlatform() {
        int platformX = screenWidth / 2 - platformBitmap.getWidth() / 2;
        int platformY = screenHeight - 300;
        Platform firstPlatform = new Platform(platformBitmap, platformX, platformY, 1);
        platforms.add(firstPlatform);
        lastTouchedPlatform = firstPlatform;
        lastTouchedPlatform_score = firstPlatform;
        lastTouchedPlatform_type1 = firstPlatform;
        player.setY(platformY - player.getBounds().height());
        platformWidth = platformBitmap.getWidth();
        platformHeight = platformBitmap.getHeight();
        createPlatform();
    }

    private void createPlatform() {
        while (platforms.get(platforms.size() - 1).getY() >= maxJumpY / 3) {
            int lastPlatformX = platforms.get(platforms.size() - 1).getX();
            int lastPlatformY = platforms.get(platforms.size() - 1).getY();
            int platformX;
            int platformY;
            int level = getLevel(passed);

            // Generate a random distance within the maximum jump distance
            int rad = random.nextInt(2 * maxJumpX);
            int direction = random.nextBoolean() ? 1 : -1; // Randomly choose direction (left or right)

            // Adjust platformX based on direction and ensure it's within screen bounds
            platformX = lastPlatformX + direction * (rad + platformWidth);
            platformX = Math.max(0, Math.min(screenWidth - platformWidth, platformX)); // Ensure within bounds
            if(platformX == 0){
                platformX = random.nextInt(5)*50;
            } else if (platformX == screenWidth - platformWidth) {
                platformX = screenWidth - platformWidth - random.nextInt(5) * 50;
            }
            // Calculate the vertical distance
            int diffX = Math.abs(platformX - lastPlatformX);
            diffX -= platformWidth;
            int maxY = diffX <= maxJumpX ? maxJumpY :
                    player.getJumpStrength() * (diffX / player.getSpeedX()) + player.getGravity() * (diffX / player.getSpeedX()) * (diffX / player.getSpeedX()) / 2;
            rad = random.nextInt(maxY / maxLevel) + maxY * (maxLevel - 1) / (maxLevel * (maxLevel + 1 - level));
            platformY = lastPlatformY - rad - platformHeight;

            // Randomize platform type (1: static, 2: moving horizontally)
            int platformType = random.nextInt(10) < 2 ? 2 : 1; // 20% chance for type 2

            if (platformType == 2) {
                // Create moving platform
                int velocityX = random.nextInt(4) + 2; // Random velocity between 2 and 5
                int minX = 0;
                int maxX = screenWidth - platformWidth;
                platforms.add(new Platform(platformBitmap, platformX, platformY, platformType, velocityX, minX, maxX));
            } else {
                // Create static platform
                platforms.add(new Platform(platformBitmap, platformX, platformY, platformType));
            }

            passed++;
        }
    }


    public int getLevel(int platformPassed) {
        if (level < maxLevel) {
            level = platformPassed / 10;
        }
        return level < maxLevel ? level : maxLevel;
    }
    private void initBackgroundMusic(Context context) {
        backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
        backgroundMusic.setLooping(true); // Lặp lại nhạc nền
        backgroundMusic.setVolume(5.0f, 5.0f); // Đặt âm lượng (giảm nhỏ nếu cần)
        backgroundMusic.start();
    }

    private void resetGame() {
        if (lastTouchedPlatform_type1 != null) {
            int playerCenterX = lastTouchedPlatform_type1.getX() +
                    (lastTouchedPlatform_type1.getBounds().width() - player.getBounds().width()) / 2;
            int playerY = lastTouchedPlatform_type1.getY() - player.getBounds().height();

            player.setX(playerCenterX);
            player.setY(playerY);

            int original_JumpStrength = player.getJumpStrength();
            player.setJumpStrength(0);  // Ngăn không cho nhảy trong khi reset

            // Khóa input khi bắt đầu reset game
            if (mainActivity != null) {
                mainActivity.disableInput();
            }

            // Khôi phục trạng thái sau 3 giây và mở lại input
            new android.os.Handler().postDelayed(() -> {
                player.setJumpStrength(original_JumpStrength);
                if (mainActivity != null) {
                    mainActivity.enableInput();  // Mở lại input sau khi reset xong
                }
            }, 1000);  // Chờ 3 giây
        } else {
            createFirstPlatform();  // Nếu không có platform nào, tạo platform đầu tiên
        }

        monsters.clear();  // Xóa danh sách quái vật
    }

    public void gameOver() {
        Context context = getContext();
        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            Intent intent = new Intent(activity, GameOverActivity.class);

            activity.startActivity(intent);
            activity.finish(); // Kết thúc Activity hiện tại (nếu cần)
        }
    }
    public static int getScore() {
        return score;
    }
    public static void setScore(int newScore) {
        score = newScore;
    }

}
