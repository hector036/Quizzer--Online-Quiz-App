package com.khan.quizzer_onlinequizapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import ru.bullyboo.text_animation.TextCounter;

public class ViewResultsActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    private FirebaseAuth auth;
    private CardView button;
    private TestAdapter testAdapter;
    ArrayList<TestResults> result = new ArrayList<>();
    private String testName;
    private String setId;
    private int lastPos = -1;
    private ProgressBar progressBar;
    private LinearLayout blankFigureLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);

        blankFigureLinearLayout = findViewById(R.id.black_image_linear_layout_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar_test);
        progressBar.setVisibility(View.VISIBLE);

        testName = getIntent().getStringExtra("test");
        setId = getIntent().getStringExtra("setId");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        ListView listView = findViewById(R.id.test_listview);
        testAdapter = new TestAdapter(ViewResultsActivity.this, result);
        listView.setAdapter(testAdapter);
        getSupportActionBar().setTitle(testName);
        getResults();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void getResults() {

        myRef.child("Results").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestResults t = new TestResults();
                    t.userID = snapshot.getKey();
                    // t.score= Objects.requireNonNull(snapshot.getValue()).toString();
                    t.score = Double.parseDouble(snapshot.child("score").getValue().toString());
                    t.profileName = snapshot.child("name").getValue().toString();
                    t.institute = snapshot.child("instituteName").getValue().toString();
                    t.type = 1;
                    result.add(t);
                }

                if (result.isEmpty()) {
                    blankFigureLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    blankFigureLinearLayout.setVisibility(View.GONE);
                }

                Collections.sort(result, new Comparator<TestResults>() {
                    @Override
                    public int compare(TestResults o1, TestResults o2) {
                        return Double.compare(o2.score, o1.score);
                        //return (int) (o2.score - o1.score);
                    }
                });

                TestResults tt = new TestResults();

                for (TestResults data : result) {
                    if (data.userID.equals(auth.getCurrentUser().getUid())) {
                        tt.score = data.score;
                        tt.merit = result.indexOf(data) + 1;
                        break;
                    }
                }

                tt.type = 0;
                result.add(0, tt);
                testAdapter.dataList = result;
                testAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                Log.e("The read success: ", "su" + result.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);

                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }


    class TestResults {

        public String userID, profileName, institute;
        public double score;
        public int type, merit;
    }

    class TestAdapter extends ArrayAdapter<TestResults> {

        public static final int TYPE_SCORE_BOARD = 0;
        public static final int TYPE_LEADER_BOARD = 1;

        private Context mContext;
        ArrayList<TestResults> dataList;

        public TestAdapter(Context context, ArrayList<TestResults> list) {
            super(context, 0, list);
            mContext = context;
            dataList = list;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return dataList.get(position).type;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem;
            if (convertView == null) {

                if (getItemViewType(position) == TYPE_LEADER_BOARD) {
                    listItem = LayoutInflater.from(mContext).inflate(R.layout.leader_board_item, parent, false);

                } else {
                    listItem = LayoutInflater.from(mContext).inflate(R.layout.score_board_item, parent, false);
                }

            } else {
                listItem = convertView;
            }

            if (getItemViewType(position) == TYPE_LEADER_BOARD) {

                ((TextView) listItem.findViewById(R.id.item_textView1)).setText(position + ". ");
                ((TextView) listItem.findViewById(R.id.profile_name_leader_board)).setText(dataList.get(position).profileName);
                ((TextView) listItem.findViewById(R.id.lb_institute)).setText(dataList.get(position).institute);

                ((TextView) listItem.findViewById(R.id.item_button1)).setText("" + dataList.get(position).score);
            } else if (getItemViewType(position) == TYPE_SCORE_BOARD) {
                TextView score = (TextView) listItem.findViewById(R.id.my_score);
                TextView merit = (TextView) listItem.findViewById(R.id.my_merit);
                TextView participants = (TextView) listItem.findViewById(R.id.participants);
                TextView dintAttempt = listItem.findViewById(R.id.dint_attemp);
                LinearLayout linearLayout = listItem.findViewById(R.id.linear_layout_score_board);

                if (dataList.get(0).merit == 0) {
                    dintAttempt.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                } else {

                    dintAttempt.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);

                    if (dataList.get(0).score == 0) {
                        TextCounter.newBuilder().setTextView(score).setType(TextCounter.LONG).from(1).to(dataList.get(0).score).setDuration(1000).setMode(TextCounter.ACCELERATION_FROM_ALPHA_MODE).setFPS(100).build().start();
                    } else {
                        TextCounter.newBuilder().setTextView(score).setType(TextCounter.LONG).from(0).to(dataList.get(0).score).setDuration(1000).setMode(TextCounter.ACCELERATION_FROM_ALPHA_MODE).setFPS(100).build().start();
                    }
                    TextCounter.newBuilder().setTextView(merit).setType(TextCounter.INT).from(0).to(dataList.get(0).merit).setDuration(1000).setMode(TextCounter.ACCELERATION_FROM_ALPHA_MODE).setFPS(100).build().start();
                    TextCounter.newBuilder().setTextView(participants).setType(TextCounter.INT).from(0).to(dataList.size() - 1).setDuration(1000).setMode(TextCounter.ACCELERATION_FROM_ALPHA_MODE).setFPS(100).build().start();

                }
            }

            return listItem;
        }
    }
}

