package com.khan.quizzer_onlinequizapp;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.net.URL;
import java.util.List;

public class MainPageHorizontalBannerAdapter extends RecyclerView.Adapter<MainPageHorizontalBannerAdapter.ViewHolder> {

    private List<MainPageModel> horizontalScrollBannerList;

    public MainPageHorizontalBannerAdapter(List<MainPageModel> horizontalScrollBannerList) {
        this.horizontalScrollBannerList = horizontalScrollBannerList;
    }

    @NonNull
    @Override
    public MainPageHorizontalBannerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_banner_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainPageHorizontalBannerAdapter.ViewHolder viewHolder, int i) {
        String bannerImg = horizontalScrollBannerList.get(i).getBannerImg();
        String bannerActionText = horizontalScrollBannerList.get(i).getBannerActionText();
        String bannerUrl = horizontalScrollBannerList.get(i).getBannerUrl();
        boolean bannerEnable = horizontalScrollBannerList.get(i).isBannerEnable();

        viewHolder.setData(bannerImg, bannerActionText, bannerUrl, bannerEnable);
    }

    @Override
    public int getItemCount() {
        return horizontalScrollBannerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView bannerImg;
        private TextView bannerText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bannerImg = itemView.findViewById(R.id.banner_img);
            bannerText = itemView.findViewById(R.id.bannerText);
        }

        public void setData(String url, String bannerActionText, final String bannerUrl, boolean bannerEnable) {
            Glide.with(itemView.getContext()).load(url).transform(new CenterCrop(), new GranularRoundedCorners(22,22,0,0)).placeholder(R.color.place_holder).into(bannerImg);
            this.bannerText.setText(bannerActionText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValidUrl(bannerUrl)) {
                        itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bannerUrl)));
                    }
                }
            });

        }

        private boolean isValidUrl(String url) {
            try {
                new URL(url).toURI();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
