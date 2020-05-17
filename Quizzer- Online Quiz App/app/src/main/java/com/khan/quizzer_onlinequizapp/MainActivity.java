package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.khan.quizzer_onlinequizapp.QuestionsActivity.FILE_NAME;
import static com.khan.quizzer_onlinequizapp.QuestionsActivity.KEY_NAME;

public class MainActivity extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener {

    private static final String TOPIC_WEEKLY_TEST_NOTIFICATION = "WEEKLYTEST";
    private static final int FIRST_TIME_LOAD = 0;
    private static final int SWIPE_REFRESH_LOAD = 1;

    public static String url = "";
    public static byte[] decodedBytes;
    public static String firstName = "", lastName = "", institute = "", phone = "";

    private FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private String json;

    private MainPageAdapter adapter;
    private List<MainPageModel> mainPageModelList = new ArrayList<>();
    private List<HomeModel> listGrid = new ArrayList<>();

    private String setId, title, date, startTime, description;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
        json = preferences.getString(KEY_NAME, "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Smart Quizzer");
        toolbar.setOverflowIcon(getDrawable(R.drawable.action));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));

        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();

        postNotification();

        progressBar = findViewById(R.id.mainpage_progress);
        refreshLayout = findViewById(R.id.mainpage_swipe_refresh);
        refreshLayout.setColorSchemeColors(Color.BLACK, Color.BLACK, Color.BLACK);

        RecyclerView mainRecyclerView = findViewById(R.id.main_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mainRecyclerView.setLayoutManager(linearLayoutManager);

        listGrid.add(new HomeModel(R.drawable.mp1, "Subject-wise Exam"));
        listGrid.add(new HomeModel(R.drawable.mp2, "Weekly Test"));
        listGrid.add(new HomeModel(R.drawable.mp3, "Bookmarks"));

        adapter = new MainPageAdapter(mainPageModelList);
        mainRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        progressBar.setVisibility(View.VISIBLE);
        getUserDetailsAndMainPageData(FIRST_TIME_LOAD);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                getUserDetailsAndMainPageData(SWIPE_REFRESH_LOAD);
            }
        });

    }

    private void loadMainPage() {
        myRef.child("tests").limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    setId = snapshot.getKey();
                    title = snapshot.child("name").getValue().toString();
                    date = snapshot.child("date").getValue().toString();
                    startTime = snapshot.child("startTime").getValue().toString();
                    description = snapshot.child("description").getValue().toString();
                }

                mainPageModelList.add(new MainPageModel(2,
                        "Recent Weekly Test", date, title, startTime, description, setId, R.drawable.mp2));

                myRef.child("Banner").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("important_banner").exists()) {
                            DataSnapshot snapshot = dataSnapshot.child("important_banner").child("1");
                            String bannerImg = snapshot.child("bannerImg").getValue().toString();
                            String bannerText = snapshot.child("bannerText").getValue().toString();
                            String bannerUrl = snapshot.child("bannerUrl").getValue().toString();
                            mainPageModelList.add(2, new MainPageModel(3,
                                    bannerImg, bannerText, bannerUrl, true));
                        }

                        for (DataSnapshot snapshot : dataSnapshot.child("feature_banner").getChildren()) {
                            String bannerImg = snapshot.child("bannerImg").getValue().toString();
                            String bannerText = snapshot.child("bannerText").getValue().toString();
                            String bannerUrl = snapshot.child("bannerUrl").getValue().toString();
                            mainPageModelList.add(new MainPageModel(3,
                                    bannerImg, bannerText, bannerUrl, true));
                        }
                        progressBar.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postNotification() {

        FirebaseMessaging.getInstance().subscribeToTopic("" + TOPIC_WEEKLY_TEST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will receive Notification";
                        if (!task.isSuccessful()) {
                            msg = "Subcription Faild";
                        }
                        // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUserDetailsAndMainPageData(final int type) {
        myRef.child("Users").child(Objects.requireNonNull(auth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firstName = dataSnapshot.child("firstname").getValue().toString();
                lastName = dataSnapshot.child("lastName").getValue().toString();
                institute = dataSnapshot.child("instituteName").getValue().toString();
                phone = dataSnapshot.child("phone").getValue().toString();
                url = dataSnapshot.child("url").getValue().toString();
                if (json.equals("") && type == FIRST_TIME_LOAD) {
                    if (dataSnapshot.child("bookmarks").exists()) {
                        json = dataSnapshot.child("bookmarks").getValue().toString();
                        storeBookmarks(json);
                    }
                }

                mainPageModelList.clear();

                mainPageModelList.add(0, new MainPageModel(0));
                mainPageModelList.add(1, new MainPageModel(1, listGrid));

                loadMainPage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeBookmarks(String jsonStr) {
        editor.putString(KEY_NAME, jsonStr);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_app_home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.logout) {

            new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog)
                    .setTitle("Logout")
                    .setMessage("Are you sure, you want to logout?")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            uploadBookmarksAndSignOut();
//                            FirebaseAuth.getInstance().signOut();
//                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();

                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadBookmarksAndSignOut() {

        String json = preferences.getString(KEY_NAME, "");
        database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).child("bookmarks").setValue(json).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    storeBookmarks("");
                    firstName = "";
                    lastName = "";
                    institute = "";
                    phone = "";
                    url = "";
                    decodedBytes = null;
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    @Override
    public void onUpdateCheckListener(final String urlApp) {

        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.dialogStyle)
                .setTitle("jQuizzer Update")
                .setMessage("There is a new version available. Please update to new version to continue")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(MainActivity.this, "Update", Toast.LENGTH_SHORT).show();
                        // String appName = getPackageName();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("" + urlApp)));
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
        json = preferences.getString(KEY_NAME, "");

    }
}
