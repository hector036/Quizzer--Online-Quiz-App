package com.example.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookmarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rv_bookmarks);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<QuestionModel> list = new ArrayList<>();

        list.add(new QuestionModel("What is your Name?","","","","","Jhon Doe",0));
        list.add(new QuestionModel("What is your Name?","","","","","Jhon Doe",0));
        list.add(new QuestionModel("What is your Name?","","","","","Jhon Doe",0));
        list.add(new QuestionModel("What is your Name?","","","","","Jhon Doe",0));
        list.add(new QuestionModel("What is your Name?","","","","","Jhon Doe",0));
        list.add(new QuestionModel("What is your Name?","","","","","Jhon Doe",0));
        list.add(new QuestionModel("What is your Name?","","","","","Jhon Doe",0));


        BookmarksAdater adater = new BookmarksAdater(list);
        recyclerView.setAdapter(adater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
