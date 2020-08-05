package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class MainActivity extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener {

    private static final String TOPIC_WEEKLY_TEST_NOTIFICATION = "WEEKLYTEST";
    private static final String TOPIC_IMPORTANT_NEWS_NOTIFICATION = "IMPORTANTNEWS";
    private static final String TOPIC_GENERAL_MESSAGE_NOTIFICATION = "GENERALMESSAGE";

    private static final int FIRST_TIME_LOAD = 0;
    private static final int SWIPE_REFRESH_LOAD = 1;
    public static boolean showUpdate = true;

    public static String url = "";
    public static byte[] decodedBytes;
    public static String firstName = "", lastName = "", institute = "", phone = "";

    private FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private MainPageAdapter adapter;
    private List<MainPageModel> mainPageModelList = new ArrayList<>();
    private List<HomeModel> listGrid = new ArrayList<>();

    private String setId, title, description;
    private TextView badge_count;
    private int count, size;
    private long date;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private SwipeRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private int mLastDayNightMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {

            if (currentUser.getDisplayName() == null) {
                Intent editProfileIntent = new Intent(MainActivity.this, EditProfileActivity.class);
                editProfileIntent.putExtra("type", 1);
                editProfileIntent.putExtra("phone", auth.getCurrentUser().getPhoneNumber());
                startActivity(editProfileIntent);
                finish();
            }

        } else {
            Intent loginIntent = new Intent(MainActivity.this, OtpActivity.class);
            startActivity(loginIntent);
            finish();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.app_logo_action_bar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOverflowIcon(getDrawable(R.drawable.action));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, OtpActivity.class);
            startActivity(intent);
            finish();
        }

        boolean isDarkMode = getSharedPreferences("Settings:" + "Dark Mode", MODE_PRIVATE).getBoolean(auth.getCurrentUser().getUid(), false);

        if (savedInstanceState == null) {
            if (isDarkMode) {
                mLastDayNightMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                mLastDayNightMode = AppCompatDelegate.MODE_NIGHT_NO;
            }
            AppCompatDelegate.setDefaultNightMode(mLastDayNightMode);
        } else {
            mLastDayNightMode = AppCompatDelegate.getDefaultNightMode();

        }


        boolean isWeeklyTestEnable = getSharedPreferences("Settings:" + "Weekly Test Notification", MODE_PRIVATE).getBoolean(auth.getCurrentUser().getUid(), true);
        boolean isEducationalNewsEnable = getSharedPreferences("Settings:" + "Educational News", MODE_PRIVATE).getBoolean(auth.getCurrentUser().getUid(), true);

        if (isWeeklyTestEnable) {
            subscribePostNotification();
        } else {
            unsubscripbePostNotification();
        }

        if (isEducationalNewsEnable) {
            subscribeNewsNotification();
        } else {
            unsubscripbeNewsNotification();
        }

        subscribeGeneralMessageNotification();

        progressBar = findViewById(R.id.mainpage_progress);
        linearLayout = findViewById(R.id.progress_bar_layout);
        refreshLayout = findViewById(R.id.mainpage_swipe_refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlack), getResources().getColor(R.color.colorBlack), getResources().getColor(R.color.colorBlack));
        refreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.swipeRefreshColor));

        RecyclerView mainRecyclerView = findViewById(R.id.main_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mainRecyclerView.setLayoutManager(linearLayoutManager);

        listGrid.add(new HomeModel(R.drawable.mp1, "Subject-wise Test"));
        listGrid.add(new HomeModel(R.drawable.mp2, "Weekly Test"));
        listGrid.add(new HomeModel(R.drawable.mp4, "Central Test"));
        listGrid.add(new HomeModel(R.drawable.mp3, "Bookmarks"));
        listGrid.add(new HomeModel(R.drawable.mp5, "Board Question Bank"));
        listGrid.add(new HomeModel(R.drawable.mp6, "Admission Question Bank"));
        listGrid.add(new HomeModel(R.drawable.mp7, "Admission Preparation"));

        adapter = new MainPageAdapter(mainPageModelList);
        mainRecyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        getUserDetailsAndMainPageData(FIRST_TIME_LOAD);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                getUserDetailsAndMainPageData(SWIPE_REFRESH_LOAD);
            }
        });

    }

    private void loadMainPage(final int type) {
        myRef.child("tests").limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    setId = snapshot.getKey();
                    title = snapshot.child("name").getValue().toString();
                    date = (long) snapshot.child("date").getValue();
                    description = snapshot.child("description").getValue().toString();
                }

                myRef.child("Banner").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mainPageModelList.clear();

                        mainPageModelList.add(0, new MainPageModel(0));
                        mainPageModelList.add(1, new MainPageModel(1, listGrid));

                        mainPageModelList.add(new MainPageModel(2,
                                "Recent Weekly Test", date, title, description, setId, R.drawable.mp2));

                        if (dataSnapshot.child("important_banner").exists()) {
                            DataSnapshot snapshot = dataSnapshot.child("important_banner").child("1");
                            String bannerImg = snapshot.child("bannerImg").getValue().toString();
                            String bannerText = snapshot.child("bannerText").getValue().toString();
                            String bannerUrl = snapshot.child("bannerUrl").getValue().toString();
                            mainPageModelList.add(2, new MainPageModel(3,
                                    bannerImg, bannerText, bannerUrl, true));
                        }

                        if (dataSnapshot.child("horizontal_scroll_banner").exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.child("horizontal_scroll_banner").getChildren()) {
                                String title = snapshot.child("title").getValue().toString();
                                String viewAllUrl = "";
                                int actionType;
                                if (snapshot.child("viewAllUrl").exists()) {
                                    viewAllUrl = snapshot.child("viewAllUrl").getValue().toString();
                                }
                                if (snapshot.child("actionType").exists()) {
                                    actionType = Integer.parseInt(snapshot.child("actionType").getValue().toString());
                                } else {
                                    actionType = 0;
                                }
                                List<MainPageModel> bannerList = new ArrayList<>();

                                if (snapshot.child("banners").exists()) {
                                    for (DataSnapshot snapshot1 : snapshot.child("banners").getChildren()) {
                                        String bannerImg = snapshot1.child("bannerImg").getValue().toString();
                                        String bannerText = snapshot1.child("bannerText").getValue().toString();
                                        String bannerUrl = snapshot1.child("bannerUrl").getValue().toString();
                                        bannerList.add(0, new MainPageModel(3,
                                                bannerImg, bannerText, bannerUrl, true));
                                    }
                                    mainPageModelList.add(new MainPageModel(4,
                                            title, viewAllUrl, bannerList, actionType));
                                }

                            }

                        }


                        for (DataSnapshot snapshot : dataSnapshot.child("feature_banner").getChildren()) {
                            String bannerImg = snapshot.child("bannerImg").getValue().toString();
                            String bannerText = snapshot.child("bannerText").getValue().toString();
                            String bannerUrl = snapshot.child("bannerUrl").getValue().toString();
                            mainPageModelList.add(new MainPageModel(3,
                                    bannerImg, bannerText, bannerUrl, true));
                        }
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                        if (showUpdate) {
                            UpdateHelper.with(MainActivity.this)
                                    .onUpdateCheck(MainActivity.this)
                                    .check();
                            showUpdate = false;
                        }
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

    private void unsubscripbePostNotification() {

        FirebaseMessaging.getInstance().unsubscribeFromTopic("" + TOPIC_WEEKLY_TEST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive Notification";
                        if (!task.isSuccessful()) {
                            msg = "Subcription Faild";
                        }
                        // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribeNewsNotification() {

        FirebaseMessaging.getInstance().subscribeToTopic("" + TOPIC_IMPORTANT_NEWS_NOTIFICATION)
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

    private void unsubscripbeNewsNotification() {

        FirebaseMessaging.getInstance().unsubscribeFromTopic("" + TOPIC_IMPORTANT_NEWS_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive Notification";
                        if (!task.isSuccessful()) {
                            msg = "Subcription Faild";
                        }
                        // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribePostNotification() {

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

    private void subscribeGeneralMessageNotification() {

        FirebaseMessaging.getInstance().subscribeToTopic("" + TOPIC_GENERAL_MESSAGE_NOTIFICATION)
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

        String pFirstName = getSharedPreferences("FirstName", MODE_PRIVATE).getString("" + auth.getCurrentUser().getUid(), "");
        String pLastName = getSharedPreferences("LastName", MODE_PRIVATE).getString("" + auth.getCurrentUser().getUid(), "");
        String pInstitute = getSharedPreferences("Institute", MODE_PRIVATE).getString("" + auth.getCurrentUser().getUid(), "");
        String pPhone = getSharedPreferences("Phone", MODE_PRIVATE).getString("" + auth.getCurrentUser().getUid(), "");
        String pUrl = getSharedPreferences("PhotoUrl", MODE_PRIVATE).getString("" + auth.getCurrentUser().getUid(), "");

        if (pFirstName.equals("") || pLastName.equals("")) {

            myRef.child("Users").child(Objects.requireNonNull(auth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    firstName = dataSnapshot.child("firstname").getValue().toString();
                    lastName = dataSnapshot.child("lastName").getValue().toString();
                    institute = dataSnapshot.child("instituteName").getValue().toString();
                    phone = dataSnapshot.child("phone").getValue().toString();
                    url = dataSnapshot.child("url").getValue().toString();
                    loadMainPage(type);

                    getSharedPreferences("FirstName", MODE_PRIVATE).edit().putString("" + auth.getCurrentUser().getUid(), firstName).apply();
                    getSharedPreferences("LastName", MODE_PRIVATE).edit().putString("" + auth.getCurrentUser().getUid(), lastName).apply();
                    getSharedPreferences("Institute", MODE_PRIVATE).edit().putString("" + auth.getCurrentUser().getUid(), institute).apply();
                    getSharedPreferences("Phone", MODE_PRIVATE).edit().putString("" + auth.getCurrentUser().getUid(), phone).apply();
                    getSharedPreferences("PhotoUrl", MODE_PRIVATE).edit().putString("" + auth.getCurrentUser().getUid(), url).apply();
                    // Toast.makeText(MainActivity.this, "FROM DATABASE", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            firstName = pFirstName;
            lastName = pLastName;
            institute = pInstitute;
            phone = pPhone;
            url = pUrl;
            loadMainPage(type);
            // Toast.makeText(MainActivity.this, "FROM SHAREDPREFARENCE", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_app_home, menu);
        MenuItem notification = menu.findItem(R.id.noti);
        notification.setActionView(R.layout.custom_notification_icon);
        badge_count = notification.getActionView().findViewById(R.id.bg);
        notification.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                badge_count.setVisibility(View.GONE);
            }
        });
        count = getSharedPreferences("Notifications", MODE_PRIVATE).getInt("" + auth.getCurrentUser().getUid(), 0);
        myRef.child("Users").child(auth.getCurrentUser().getUid()).child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                size = (int) dataSnapshot.getChildrenCount();
                if (size > count) {
                    badge_count.setVisibility(View.VISIBLE);
                    if ((size - count) > 9) {
                        badge_count.setText("9+");
                    } else {
                        badge_count.setText(String.valueOf(size - count));
                    }
                } else {
                    badge_count.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                            firstName = "";
                            lastName = "";
                            institute = "";
                            phone = "";
                            url = "";
                            decodedBytes = null;
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(MainActivity.this, OtpActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.noti) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateCheckListener(final String urlApp, String updateMsg, boolean setCancelable) {


        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.bottomSheetTheme);

        View view = getLayoutInflater().inflate(R.layout.update_dialog, null);

        //  bottomSheetDialog.setContentView(R.layout.update_dialog);
        bottomSheetDialog.setContentView(view);
        //    bottomSheetDialog.setCanceledOnTouchOutside(setCancelable);
        bottomSheetDialog.setCancelable(setCancelable);

        Button cancel = bottomSheetDialog.findViewById(R.id.update_cancelBtn);
        Button update = bottomSheetDialog.findViewById(R.id.update_updateBtn);
        TextView updateText = bottomSheetDialog.findViewById(R.id.update_text);
        updateText.setText(updateMsg);

        if (!setCancelable) {
            cancel.setVisibility(View.GONE);
        } else
            cancel.setVisibility(View.VISIBLE);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("" + urlApp)));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.show();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (AppCompatDelegate.getDefaultNightMode() != mLastDayNightMode) {
            recreate();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
