package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.GameView;
import com.example.test.R;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private Handler handler = new Handler();
    private Runnable moveLeftRunnable;
    private Runnable moveRightRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout gameContainer = findViewById(R.id.game_container);
        gameView = new GameView(this);
        gameContainer.addView(gameView);

        // Tạo hành động khi nhấn giữ nút trái
        moveLeftRunnable = new Runnable() {
            @Override
            public void run() {
                gameView.movePlayerLeft();
                handler.postDelayed(this, 16); // Lặp lại sau 16ms
            }
        };

        // Tạo hành động khi nhấn giữ nút phải
        moveRightRunnable = new Runnable() {
            @Override
            public void run() {
                gameView.movePlayerRight();
                handler.postDelayed(this, 16); // Lặp lại sau 16ms
            }
        };

        // Thiết lập sự kiện nhấn giữ cho nút trái
        findViewById(R.id.btn_left).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handler.post(moveRightRunnable);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacks(moveRightRunnable);
                }
                return true;
            }
        });

    }

}
