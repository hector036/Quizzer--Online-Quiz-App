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

public class ScoreActivity extends AppCompatActivity {

    private TextView score;
    private TextView total;
    private Button doneBtn;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        loadAds();

        score = findViewById(R.id.score);
        total = findViewById(R.id.total);
        doneBtn = findViewById(R.id.done_button);

        score.setText(String.valueOf(getIntent().getIntExtra("score",1)));
        total.setText("OUT OF "+String.valueOf(getIntent().getIntExtra("total",0)));
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                    return;
                }

                Intent intent = new Intent(ScoreActivity.this,AnsSheetActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void loadAds() {

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitialAd_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                Intent intent = new Intent(ScoreActivity.this,AnsSheetActivity.class);
                startActivity(intent);
                finish();
                return;

            }
        });
    }
}
