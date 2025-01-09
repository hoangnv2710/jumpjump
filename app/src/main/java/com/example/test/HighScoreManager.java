package com.example.test;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;

public class HighScoreManager {
    private static final String PREF_NAME = "HighScoresPref";
    private static final String SCORES_KEY = "HighScores";
    private static final int MAX_SCORES = 10; // Số lượng điểm cao tối đa

    // Lưu điểm cao mới
    public static void saveHighScore(Context context, int score) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Lấy danh sách điểm cao hiện tại
        ArrayList<Integer> highScores = getHighScores(context);

        // Thêm điểm mới vào danh sách
        highScores.add(score);

        // Sắp xếp điểm từ cao đến thấp
        Collections.sort(highScores, Collections.reverseOrder());

        // Giữ lại tối đa MAX_SCORES điểm
        if (highScores.size() > MAX_SCORES) {
            highScores = new ArrayList<>(highScores.subList(0, MAX_SCORES));
        }

        // Lưu lại danh sách vào SharedPreferences
        StringBuilder scoresString = new StringBuilder();
        for (int s : highScores) {
            scoresString.append(s).append(",");
        }
        editor.putString(SCORES_KEY, scoresString.toString());
        editor.apply();
    }

    // Lấy danh sách điểm cao dạng số nguyên
    public static ArrayList<Integer> getHighScores(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String scoresString = prefs.getString(SCORES_KEY, "");

        ArrayList<Integer> highScores = new ArrayList<>();
        if (!scoresString.isEmpty()) {
            String[] scoresArray = scoresString.split(",");
            for (String score : scoresArray) {
                try {
                    highScores.add(Integer.parseInt(score));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return highScores;
    }

    // Phương thức mới: Lấy danh sách điểm cao dạng số nguyên (tương tự getHighScores)
    public static ArrayList<Integer> getRawHighScores(Context context) {
        return getHighScores(context);
    }

    // Lấy danh sách điểm cao dạng chuỗi định dạng "1. xxxx point"
    public static ArrayList<String> getFormattedHighScores(Context context) {
        ArrayList<Integer> highScores = getHighScores(context);
        ArrayList<String> formattedScores = new ArrayList<>();

        for (int i = 0; i < MAX_SCORES; i++) {
            int score = i < highScores.size() ? highScores.get(i) : 0;
            formattedScores.add((i + 1) + ". " + score + " point");
        }

        return formattedScores;
    }
}
