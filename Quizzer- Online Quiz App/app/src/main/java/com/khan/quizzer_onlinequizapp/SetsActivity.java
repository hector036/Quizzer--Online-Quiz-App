package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;


public class SetsActivity extends AppCompatActivity {

    private GridView gridView;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean isDarkMode = getSharedPreferences("Settings:"+"Dark Mode", MODE_PRIVATE).getBoolean(FirebaseAuth.getInstance().getCurrentUser().getUid(), false);

        if(isDarkMode){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            //toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Dark);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            // toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Light);
        }

        loadAds();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        gridView = findViewById(R.id.gridview);

        GridAdapter adapter = new GridAdapter(CategoriesActivity.list.get(getIntent().getIntExtra("position",0)).getSets(),getIntent().getStringExtra("title"),mInterstitialAd);
        gridView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();

        }

        return super.onOptionsItemSelected(item);
    }


    private void loadAds() {

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitialAd_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }
}
