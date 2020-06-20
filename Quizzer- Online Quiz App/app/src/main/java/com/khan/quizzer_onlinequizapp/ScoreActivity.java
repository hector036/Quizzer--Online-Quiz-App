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
    private int exmScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score = findViewById(R.id.score);
        total = findViewById(R.id.total);
        doneBtn = findViewById(R.id.done_button);

        exmScore = getIntent().getIntExtra("score", 0);

        if (exmScore == 0) {
            TextCounter.newBuilder().setTextView(score).setType(TextCounter.LONG).from(1).to(exmScore).setDuration(500).setMode(TextCounter.ACCELERATION_FROM_ALPHA_MODE).setFPS(100).build().start();

        } else {
            TextCounter.newBuilder().setTextView(score).setType(TextCounter.LONG).from(0).to(exmScore).setDuration(500).setMode(TextCounter.ACCELERATION_FROM_ALPHA_MODE).setFPS(100).build().start();
        }

        total.setText(String.valueOf(getIntent().getIntExtra("total", 0)));
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                if (mInterstitialAd.isLoaded()){
//                    mInterstitialAd.show();
//                    return;
//                }

                Intent intent = new Intent(ScoreActivity.this, AnsSheetActivity.class);
                intent.putExtra("isScoreBoard", 0);
                startActivity(intent);
                finish();
            }
        });

    }

//    private void loadAds() {
//
//        AdView mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitialAd_id));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//
//        mInterstitialAd.setAdListener(new AdListener(){
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//
//                Intent intent = new Intent(ScoreActivity.this,AnsSheetActivity.class);
//                startActivity(intent);
//                finish();
//                return;
//
//            }
//        });
//    }
}
