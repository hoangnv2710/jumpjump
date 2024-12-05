package com.example.test;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private Handler handler = new Handler();
    private Runnable moveLeftRunnable;
    private Runnable moveRightRunnable;
    private boolean isInputDisabled = false;  // Biến để kiểm tra trạng thái input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout gameContainer = findViewById(R.id.game_container);
        gameView = new GameView(this);  // Khởi tạo GameView
        gameContainer.addView(gameView);  // Thêm GameView vào giao diện

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
    }

    // Phương thức để khóa input
    public void disableInput() {
        isInputDisabled = true;
        findViewById(R.id.btn_left).setEnabled(false);  // Vô hiệu hóa nút trái
        findViewById(R.id.btn_right).setEnabled(false); // Vô hiệu hóa nút phải
    }

    // Phương thức để mở khóa input
    public void enableInput() {
        isInputDisabled = false;
        findViewById(R.id.btn_left).setEnabled(true);  // Bật lại nút trái
        findViewById(R.id.btn_right).setEnabled(true); // Bật lại nút phải
    }
}
