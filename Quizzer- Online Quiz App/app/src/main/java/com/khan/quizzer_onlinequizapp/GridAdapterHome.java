package com.khan.quizzer_onlinequizapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

public class GridAdapterHome extends BaseAdapter {
    private List<HomeModel> list;
   // private String category;
   // private InterstitialAd interstitialAd;

    public GridAdapterHome(List<HomeModel> list) {

        this.list = list;
      //  this.category = category;
       // this.interstitialAd = interstitialAd;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final View view;

        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item,parent,false);

        }else {
            view = convertView;
        }

        ((TextView)view.findViewById(R.id.textview)).setText(list.get(position).getTitle());
        ((ImageView)view.findViewById(R.id.imageView)).setImageResource(list.get(position).getImage());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position==0){
                    Intent categoryIntent = new Intent(parent.getContext(), CategoriesActivity.class);
                    categoryIntent.putExtra("type",0);
                    parent.getContext().startActivity(categoryIntent);
                }
                else if(position==1){
                    Intent intent = new Intent(parent.getContext(), TestsActivity.class);
                    parent.getContext().startActivity(intent);
                }
                else if(position==2){
                    Intent bookmarkIntent = new Intent(parent.getContext(), BookmarksActivity.class);
                   // bookmarkIntent.putExtra("type",1);
                    parent.getContext().startActivity(bookmarkIntent);
                }
                else if(position==3){
                    Intent intent = new Intent(parent.getContext(), ProfileActivity.class);
                    parent.getContext().startActivity(intent);
                }

            }
        });

        return view;
    }
}