package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GameOverActivity extends AppCompatActivity {
    Button btnReplay;
    TextView txtScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_over);

        // Lấy điểm số từ Intent (nếu có)
        Intent intent = getIntent();
        int score = intent.getIntExtra("SCORE", 0);  // Mặc định là 0 nếu không có điểm số

        // Khởi tạo các View
        btnReplay = findViewById(R.id.button2);
        txtScore = findViewById(R.id.your_score);

        // Lấy điểm số từ GameView
        score = GameView.getScore();

        // Lấy danh sách điểm cao dạng số nguyên
        ArrayList<Integer> highScores = HighScoreManager.getRawHighScores(this);

        // Tìm rank của người chơi trong danh sách điểm cao
        int rank = -1;
        for (int i = 0; i < highScores.size(); i++) {
            if (highScores.get(i) == 50 * score) { // So sánh chính xác giá trị điểm
                rank = i + 1; // Vị trí trong danh sách (bắt đầu từ 1)
                break;
            }
        }

        // Hiển thị điểm số và rank nếu có
        if (rank != -1 && score != 0) {
            txtScore.setText("Your Score: " + (score * 50) + "\nRank " + rank + " in High Scores");
        } else {
            txtScore.setText("Your Score: " + (score * 50));
        }

        // Đặt sự kiện cho nút Replay
        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Quay lại MainActivity
                GameView.setScore(0);
                GameView.setLife(3);
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
