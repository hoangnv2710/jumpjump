package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    Button btnStart, btnHighScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Khởi tạo nút START
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                GameView.setScore(0);
                GameView.setLife(3);
                startActivity(intent);
            }
        });

        // Khởi tạo nút HIGH SCORES
        btnHighScores = findViewById(R.id.btnHighScores);
        btnHighScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển sang màn hình hiển thị High Scores
                Intent intent = new Intent(SplashActivity.this, HighScoresActivity.class);
                startActivity(intent);
            }
        });
    }
}
