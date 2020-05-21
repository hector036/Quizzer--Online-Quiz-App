package com.khan.quizzer_onlinequizapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.Calendar;

class TestAdapter extends ArrayAdapter<Test> implements Filterable {
    private Context mContext;
    ArrayList<Test> dataList;
    public TestAdapter( Context context,ArrayList<Test> list) {
        super(context, 0 , list);
        mContext = context;
        dataList = list;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.test_item,parent,false);

        final TextView descripton = (TextView)listItem.findViewById(R.id.description);
        final TextView seeMore = (TextView)listItem.findViewById(R.id.see_more);

//            ((ImageView)listItem.findViewById(R.id.item_imageView)).
        //     setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_appicon));

        ((TextView) listItem.findViewById(R.id.item_textView1))
                .setText(dataList.get(position).getName());
        ((TextView) listItem.findViewById(R.id.date))
                .setText(formateDate(dataList.get(position).getDateTime()));
        ((TextView)listItem.findViewById(R.id.start_time))
                .setText(formateTime(dataList.get(position).getDateTime()));

        (descripton).setText(dataList.get(position).getDescription());
        (descripton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (descripton).setMaxLines(100);
                seeMore.setVisibility(View.GONE);
            }
        });
        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (descripton).setMaxLines(100);
                seeMore.setVisibility(View.GONE);
            }
        });

        ((Button)listItem.findViewById(R.id.item_button1)).setText("Attempt");

        (listItem.findViewById(R.id.item_button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, QuestionsActivity.class);
                //  intent.putExtra("Questions",dataList.get(position));
                intent.putExtra("setId",dataList.get(position).getSetId());
                intent.putExtra("type",2);
                intent.putExtra("test",dataList.get(position).getName());

                parent.getContext().startActivity(intent);
            }
        });

        ((Button)listItem.findViewById(R.id.leader_board)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(parent.getContext(), ViewResultsActivity.class);
                intent.putExtra("test",dataList.get(position).getName());
                intent.putExtra("setId",dataList.get(position).getSetId());

                // intent.putExtra("ISAdmin",isAdmin);
                parent.getContext().startActivity(intent);
            }
        });

        //  Animation animation = AnimationUtils.loadAnimation(getContext(),
        //           (position > lastPos) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //   (listItem).startAnimation(animation);
        //   lastPos = position;

        return listItem;
    }

    private String formateDate(long dateInMili) {

        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(dateInMili);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String monthS = months[mMonth];

        String finalDate = mDay + " " + monthS + ", " + mYear;
        return finalDate;
    }

    private String formateTime(long dateInMili) {
        String finalTime = "";

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateInMili);
        Calendar current = Calendar.getInstance();

        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int currentDay = current.get(Calendar.DAY_OF_MONTH);

        int mHour = (c.get(Calendar.HOUR_OF_DAY)) % 12;
        int mAM_PM = (c.get(Calendar.AM_PM));
        int mMin = (c.get(Calendar.MINUTE));

        int difDay = currentDay - mDay;

        if (difDay == 0) {
            StringBuilder str = new StringBuilder();
            str.append("Today - ");
            str.append(""+mHour);
            if(mMin!=0){
                str.append(":");
                String s = String.format("%02d", mMin);
                str.append(s);
            }
            if(mAM_PM==0){
                str.append(" AM");
            }else {
                str.append(" PM");
            }
            finalTime = str.toString();
        }
        else if(difDay==1){
            StringBuilder str = new StringBuilder();
            str.append("Yesterday - ");
            str.append(""+mHour);
            if(mMin!=0){
                str.append(":");
                String s = String.format("%02d", mMin);
                str.append(s);
            }
            if(mAM_PM==0){
                str.append(" AM");
            }else {
                str.append(" PM");
            }
            finalTime = str.toString();
        }
        else if(difDay > 1 && difDay <8){
            finalTime = difDay + " days ago";
        }

        return finalTime;
    }
}
