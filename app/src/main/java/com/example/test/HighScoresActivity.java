package com.example.test;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HighScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        // Tìm TextView hiển thị danh sách điểm cao
        TextView highScoresList = findViewById(R.id.highScoresList);

        // Lấy danh sách điểm cao đã lưu
        ArrayList<String> highScores = HighScoreManager.getFormattedHighScores(this);

        // Hiển thị danh sách điểm cao
        StringBuilder display = new StringBuilder();
        for (String score : highScores) {
            display.append(score).append("\n");
        }

        highScoresList.setText(display.toString());
    }
}
