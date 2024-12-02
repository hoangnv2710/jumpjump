package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {
    Button btnReplay;
    TextView txtScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_over);

        // Lấy điểm số từ Intent
        Intent intent = getIntent();
        int score = intent.getIntExtra("SCORE", 0);  // Mặc định là 0 nếu không có điểm số

        // Khởi tạo các View
        btnReplay = findViewById(R.id.button2);
        txtScore = findViewById(R.id.your_score);

        score = GameView.getScore();
        // Hiển thị điểm số
        txtScore.setText("Your Score: " + score*50);

        // Đặt sự kiện cho nút Replay
        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Quay lại MainActivity
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
