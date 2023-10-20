package com.developer_abdul_halim.spoken;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {
    TextView txtscore,questionSize;
    TextView txtStatus;
    MediaPlayer audio;
    ImageView imgBack;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);

        imgBack = findViewById(R.id.imgBack);
        txtscore = findViewById(R.id.txtscore);
        txtStatus = findViewById(R.id.txtStatus);
        questionSize = findViewById(R.id.questionSize);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("total", 0);
        questionSize.setText(String.format("%d/%d", score, totalQuestions));
        txtscore.setText(String.valueOf(score));
        txtStatus.setText(setStatus(score));
        audio.start();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent home = new Intent(ScoreActivity.this, MainActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(home);

                finish();
            }
        });

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedScore", score + " points");


        // Also mark the category as completed and unlock the next category
        if (QuestionCollection.JSONurl.equals("https://developerabdulhalim.xyz/quiz_app/quiz.json")) {
            editor.putBoolean("Category0Completed", true);
            editor.putBoolean("Category1Completed", true);
        } else if (QuestionCollection.JSONurl.equals("https://developerabdulhalim.xyz/quiz_app/quiztwo.json")) {
            editor.putBoolean("Category1Completed", true);
            // ... add further checks for other URLs ...
        }


        editor.apply();
    }

    private String setStatus(int score){
        if(score >= 8){
            audio = MediaPlayer.create(this, R.raw.high_score);
            return "অভিনন্দন!! খুব ভালো করেছেন";
        }

        if (score >= 5){
            audio = MediaPlayer.create(this,  R.raw.medium_score);
            return "ভালো হয়েছে। আবার চেষ্টা করুন";
        }

        audio = MediaPlayer.create(this,  R.raw.low_score);
        return "আরো ভালো করতে হবে :) ";
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(ScoreActivity.this, MainActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
        finish();
    }
}
