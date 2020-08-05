package com.khan.quizzer_onlinequizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import ru.bullyboo.text_animation.TextCounter;

public class ScoreActivity extends AppCompatActivity {

    private TextView score;
    private TextView total;
    private Button doneBtn;
    private InterstitialAd mInterstitialAd;
    private double exmScore;
    private int type;
    private int round = 0, totalQues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score = findViewById(R.id.score);
        total = findViewById(R.id.total);
        doneBtn = findViewById(R.id.done_button);

        exmScore = getIntent().getDoubleExtra("score", 0.0);
        type = getIntent().getIntExtra("type", 0);
        totalQues = getIntent().getIntExtra("total", 0);
        if (type == 1) {
            round = 2;
            resizeTextView();
        }
        if (exmScore == 0.0) {
            TextCounter.newBuilder().setTextView(score).setType(TextCounter.DOUBLE).from(1d).to(exmScore).setDuration(500).setMode(TextCounter.ACCELERATION_FROM_ALPHA_MODE).setFPS(100).setRound(round).build().start();
        } else {
            TextCounter.newBuilder().setTextView(score).setType(TextCounter.DOUBLE).from(0d).to(exmScore).setDuration(500).setMode(TextCounter.ACCELERATION_FROM_ALPHA_MODE).setFPS(100).setRound(round).build().start();
        }

        total.setText(String.valueOf(totalQues));
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ScoreActivity.this, AnsSheetActivity.class);
                intent.putExtra("isScoreBoard", 0);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }
        });

    }

    private void resizeTextView() {
        if (exmScore < 10) {
            score.setTextSize(40);
        } else if (exmScore < 100 && exmScore > 9) {
            score.setTextSize(35);
        } else {
            score.setTextSize(30);
        }

        if (totalQues < 10) {
            total.setTextSize(56);
        } else if (totalQues < 100 && totalQues > 9) {
            total.setTextSize(45);
        } else {
            total.setTextSize(35);
        }

    }

}
