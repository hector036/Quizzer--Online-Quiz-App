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

import com.devs.readmoreoption.ReadMoreOption;

import java.util.ArrayList;

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

//            ((ImageView)listItem.findViewById(R.id.item_imageView)).
        //     setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_appicon));

        ((TextView)listItem.findViewById(R.id.item_textView1))
                .setText(dataList.get(position).getName());
        ((TextView)listItem.findViewById(R.id.date))
                .setText(dataList.get(position).getDate());
        ((TextView)listItem.findViewById(R.id.start_time))
                .setText(dataList.get(position).getStartTime());

        ReadMoreOption readMoreOption = new ReadMoreOption.Builder(parent.getContext())
                // .textLength(3, ReadMoreOption.TYPE_LINE) // OR
                .textLength(78, ReadMoreOption.TYPE_CHARACTER)
                .moreLabel("See More")
                .lessLabel("See Less")
                .moreLabelColor(Color.BLACK)
                .lessLabelColor(Color.BLACK)
                //.labelUnderLine(false)
                .build();
        readMoreOption.addReadMoreTo((TextView)listItem.findViewById(R.id.description), dataList.get(position).getDescription());

//
//            ((TextView)listItem.findViewById(R.id.description))
//                    .setText(dataList.get(position).getDescription());

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
}
