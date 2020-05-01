package com.khan.quizzer_onlinequizapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.test_quiz.Model.User;
//import com.example.test_quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.wang.avi.AVLoadingIndicatorView;

//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import ru.bullyboo.text_animation.TextCounter;

public class ViewResultsActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    private FirebaseAuth auth;
    //  private AVLoadingIndicatorView avLoadingIndicatorView;
    //private MaterialButton button;
    private CardView button;
    private TestAdapter testAdapter;
    ArrayList<TestResults> result = new ArrayList<>();
    private String testName;
    private String setId;
    private int lastPos = -1;
    // public boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);
        //  isAdmin = getIntent().getBooleanExtra("ISAdmin",false);
        testName = getIntent().getStringExtra("test");
        setId = getIntent().getStringExtra("setId");
        //  if(!isAdmin) {
        //      setTitle("Result");
        // }
        //  if(isAdmin)
        //      button.setVisibility(View.VISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // avLoadingIndicatorView = findViewById(R.id.loader1);
        //  avLoadingIndicatorView.setVisibility(View.VISIBLE);
        //  avLoadingIndicatorView.show();
        /*
        button for admin to see report in excel files
        */

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        ListView listView = findViewById(R.id.test_listview);
        testAdapter = new TestAdapter(ViewResultsActivity.this, result);
        listView.setAdapter(testAdapter);
        getSupportActionBar().setTitle(testName);
        getResults();

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // check if available and not read only
//                if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
//                    Log.e("TAG", "Storage not available or read only");
//                    return;
//                }
//                saveToExcel(testName.concat(".xls"));
//            }
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
//
//    public void saveToExcel(String fileName) {
//        getResults();
//        //New Workbook
//        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();
//        Sheet createSheet = hSSFWorkbook.createSheet(this.testName);
//        //create row
//        Row createRow = createSheet.createRow(0);
//        createRow.createCell(0).setCellValue("ID");
//        createRow.createCell(1).setCellValue("Semester");
//        createRow.createCell(2).setCellValue("Branch");
//        createRow.createCell(3).setCellValue("Section");
//        createRow.createCell(4).setCellValue("Score");
//
//        int i = 0;
//        while (i < this.testAdapter.dataList.size()) {
//            int i2 = i + 1;
//            Row createRow2 = createSheet.createRow(i2);
//            createRow2.createCell(0).setCellValue(this.testAdapter.dataList.get(i).user.name);
//            createRow2.createCell(1).setCellValue(this.testAdapter.dataList.get(i).user.semester);
//            createRow2.createCell(2).setCellValue(this.testAdapter.dataList.get(i).user.branch);
//            createRow2.createCell(3).setCellValue(this.testAdapter.dataList.get(i).user.sect);
//            createRow2.createCell(4).setCellValue(this.testAdapter.dataList.get(i).score);
//            i = i2;
//        }
//
//        //save the file under download folder of android
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);
//
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            hSSFWorkbook.write(fileOutputStream);
//            StringBuilder sb = new StringBuilder();
//            sb.append("Writing file");
//            sb.append(fileName);
//            Log.w("FileSaver",sb.toString());
//            fileOutputStream.close();
//            Toast.makeText(ResultsAdminDetailed.this, "File is saved under Download folder", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(ResultsAdminDetailed.this, "Can't be saved", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }
//
//    public boolean isExternalStorageReadOnly() {
//        return "mounted_ro".equals(Environment.getExternalStorageState());
//    }
//
//    public boolean isExternalStorageAvailable() {
//        return "mounted".equals(Environment.getExternalStorageState());
//    }

    public void getResults() {

        myRef.child("Results").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestResults t = new TestResults();
                    t.userID = snapshot.getKey();
                    // t.score= Objects.requireNonNull(snapshot.getValue()).toString();
                    t.score = (long) snapshot.child("score").getValue();
                    t.profileName = snapshot.child("name").getValue().toString();
                    t.institute = snapshot.child("instituteName").getValue().toString();
                    t.type = 1;
                    result.add(t);
                }

                Collections.sort(result, new Comparator<TestResults>() {
                    @Override
                    public int compare(TestResults o1, TestResults o2) {
                        return (int) (o2.score - o1.score);
                    }
                });

                //getDetails();
                TestResults tt = new TestResults();

                for (TestResults data : result) {
                    if (data.userID.equals(auth.getCurrentUser().getUid())) {
                        tt.score = data.score;
                        tt.merit = result.indexOf(data) + 1;
                        break;
                    }
                }

                tt.type = 0;
                // tt.merit = 1;
                //  tt.score = 10;
                result.add(0, tt);
                testAdapter.dataList = result;
                testAdapter.notifyDataSetChanged();
                Log.e("The read success: ", "su" + result.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // avLoadingIndicatorView.setVisibility(View.GONE);
                // avLoadingIndicatorView.hide();
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }


    private void getDetails() {
        myRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < result.size(); i++) {
                    if (dataSnapshot.child(result.get(i).userID).exists()) {

                        String profile = dataSnapshot.child(result.get(i).userID).child("firstname").getValue().toString() + " " + dataSnapshot.child(result.get(i).userID).child("lastName").getValue().toString();
                        String insti = dataSnapshot.child(result.get(i).userID).child("instituteName").getValue().toString();
                        result.get(i).profileName = profile;
                        result.get(i).institute = insti;
                    } else {

                        result.get(i).profileName = "Unknown";
                        result.get(i).institute = "Unknown Institute";
                    }

                }
                testAdapter.dataList = result;
                testAdapter.notifyDataSetChanged();
                //   avLoadingIndicatorView.setVisibility(View.GONE);
                //  avLoadingIndicatorView.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //   avLoadingIndicatorView.setVisibility(View.GONE);
                //   avLoadingIndicatorView.hide();
            }
        });
    }


    class TestResults {

        public String userID, profileName, institute;
        public long score;
        public int type, merit;
        //public  User user;
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
                // Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPos) ? R.anim.up_from_bottom : R.anim.down_from_top);
                //  (listItem).startAnimation(animation);
                //  lastPos = position;
//            if(isAdmin ) {
//                ((TextView) listItem.findViewById(R.id.item_textView1)).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (dataList.get(position).user.name != null) {
//
//                            Intent intent = new Intent(ResultsAdminDetailed.this, GetDetailReport.class);
//                            intent.putExtra("USERID", dataList.get(position).userID);
//                            intent.putExtra("DetailID", dataList.get(position).user.name);
//                            intent.putExtra("DetailBranch", dataList.get(position).user.branch);
//                            intent.putExtra("DetailSem", dataList.get(position).user.semester);
//                            intent.putExtra("DetailSec", dataList.get(position).user.sect);
//                            intent.putExtra("TestNAME", testName);
//                            intent.putExtra("Marks", dataList.get(position).score);
//                            startActivity(intent);
//                        }
//                    }
//                });
//            }
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

