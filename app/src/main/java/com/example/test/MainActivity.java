package com.example.test;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    public Handler handler = new Handler();  // Thay đổi quyền truy cập thành public
    public Runnable moveLeftRunnable;
    public Runnable moveRightRunnable;
    private boolean isInputDisabled = false;  // Biến để kiểm tra trạng thái input
    private boolean isGamePaused = false;
    private LinearLayout pauseMenu;  // Màn hình tạm dừng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout gameContainer = findViewById(R.id.game_container);
        gameView = new GameView(this);  // Khởi tạo GameView
        gameContainer.addView(gameView);  // Thêm GameView vào giao diện

        // Lấy tham chiếu đến menu tạm dừng
        pauseMenu = findViewById(R.id.pause_menu);

        // Tạo hành động khi nhấn giữ nút trái
        moveLeftRunnable = new Runnable() {
            @Override
            public void run() {
                gameView.movePlayerLeft();  // Di chuyển nhân vật sang trái
                handler.postDelayed(this, 16);  // Lặp lại sau 16ms (~60 FPS)
            }
        };

        // Tạo hành động khi nhấn giữ nút phải
        moveRightRunnable = new Runnable() {
            @Override
            public void run() {
                gameView.movePlayerRight();  // Di chuyển nhân vật sang phải
                handler.postDelayed(this, 16);  // Lặp lại sau 16ms (~60 FPS)
            }
        };

        // Thiết lập sự kiện nhấn giữ cho nút trái
        findViewById(R.id.btn_left).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isInputDisabled) return true;  // Nếu input bị khóa, bỏ qua sự kiện

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handler.post(moveLeftRunnable);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacks(moveLeftRunnable);
                }
                return true;
            }
        });

        // Thiết lập sự kiện nhấn giữ cho nút phải
        findViewById(R.id.btn_right).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isInputDisabled) return true;  // Nếu input bị khóa, bỏ qua sự kiện

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handler.post(moveRightRunnable);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacks(moveRightRunnable);
                }
                return true;
            }
        });

        // Sự kiện cho nút tạm dừng
        findViewById(R.id.btn_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGamePaused) {
                    resumeGame();  // Tiếp tục trò chơi nếu đang tạm dừng
                } else {
                    pauseGame();  // Tạm dừng trò chơi
                }
            }
        });

        // Sự kiện cho nút tiếp tục trong menu tạm dừng
        findViewById(R.id.btn_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeGame();
            }
        });

        // Sự kiện cho nút quay lại menu trong menu tạm dừng
        findViewById(R.id.btn_back_to_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý quay lại menu nếu cần thiết
                finish();  // Hoặc thực hiện hành động quay lại menu chính
            }
        });

    }

    // Phương thức để dừng hành động di chuyển trái
    public void stopMoveLeft() {
        handler.removeCallbacks(moveLeftRunnable);
    }

    // Phương thức để dừng hành động di chuyển phải
    public void stopMoveRight() {
        handler.removeCallbacks(moveRightRunnable);
    }
    private void pauseGame() {
        isGamePaused = true;
        isInputDisabled = true;  // Khóa input
        pauseMenu.setVisibility(View.VISIBLE);  // Hiển thị menu tạm dừng
        handler.removeCallbacks(moveLeftRunnable);  // Dừng hành động di chuyển trái
        handler.removeCallbacks(moveRightRunnable);  // Dừng hành động di chuyển phải
    }

    // Phương thức để tiếp tục trò chơi
    private void resumeGame() {
        isGamePaused = false;
        isInputDisabled = false;  // Mở khóa input
        pauseMenu.setVisibility(View.GONE);  // Ẩn menu tạm dừng
    }

    // Phương thức để khóa input và ngừng các sự kiện của Handler
    public void disableInput() {
        isInputDisabled = true;
        findViewById(R.id.btn_left).setEnabled(false);  // Vô hiệu hóa nút trái
        findViewById(R.id.btn_right).setEnabled(false); // Vô hiệu hóa nút phải
        stopMoveLeft();  // Ngừng hành động di chuyển trái
        stopMoveRight(); // Ngừng hành động di chuyển phải
    }

    // Phương thức để mở khóa input
    public void enableInput() {
        isInputDisabled = false;
        findViewById(R.id.btn_left).setEnabled(true);  // Bật lại nút trái
        findViewById(R.id.btn_right).setEnabled(true); // Bật lại nút phải
    }

    // Phương thức để lấy trạng thái Pause
    public boolean getPauseState() {
        return isGamePaused;
    }

    // Phương thức để cập nhật trạng thái Pause
    public void setGamePaused(boolean isPaused) {
        isGamePaused = isPaused;
    }
}
