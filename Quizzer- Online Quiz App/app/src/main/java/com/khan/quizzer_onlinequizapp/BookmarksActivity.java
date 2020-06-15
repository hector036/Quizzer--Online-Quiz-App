package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;

    private List<QuestionModel> bookmarksList;
    private List<QuestionModel> tempList;
    private String category;
    private LinearLayout blankImageLinearLayout;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        blankImageLinearLayout = findViewById(R.id.black_image_linear_layout);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookmarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category = getIntent().getStringExtra("category");

        recyclerView = findViewById(R.id.rv_bookmarks);

        preferences = getSharedPreferences("Bookmarks", Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        BookmarksAdater adater = new BookmarksAdater(bookmarksList, 2);
        recyclerView.setAdapter(adater);
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getBookmarks() {
        String json = preferences.getString(""+mAuth.getCurrentUser().getUid(), "");
        Type type = new TypeToken<List<QuestionModel>>() {
        }.getType();
        bookmarksList = gson.fromJson(json, type);
        if (bookmarksList == null) {
            bookmarksList = new ArrayList<>();
            blankImageLinearLayout.setVisibility(View.VISIBLE);
        } else {
            if(bookmarksList.isEmpty()){
                blankImageLinearLayout.setVisibility(View.VISIBLE);
            }else {
                blankImageLinearLayout.setVisibility(View.GONE);
            }
            for (QuestionModel questionModel : bookmarksList) {
                questionModel.setQuestion(cutString(questionModel.getQuestion()));
            }
        }
    }

    private void storeBookmarks() {

        String json = gson.toJson(bookmarksList);

        editor.putString(""+mAuth.getCurrentUser().getUid(), json);

        editor.commit();

    }

    private String cutString(String str) {
        if (str.charAt(3) == '.') {
            return str.substring(3);
        } else if (str.charAt(4) == '.') {
            return str.substring(4);
        } else if (str.charAt(5) == '.') {
            return str.substring(5);
        } else
            return str;

    }


}
