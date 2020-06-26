package com.khan.quizzer_onlinequizapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsAdapter extends RecyclerView.Adapter {

    private List<SettingsModel> list;

    public SettingsAdapter(List<SettingsModel> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public int getItemViewType(int position) {
        switch (list.get(position).getType()) {
            case 0:
                return SettingsModel.SETTINGS_HEADER;
            case 1:
                return SettingsModel.SETTINGS_ITEM_WITH_SWITCH;
            case 2:
                return SettingsModel.SETTINGS_ITEM_WITH_OUT_SWITCH;
            case 3:
                return SettingsModel.SETTINGS_ITEM_WITH_OUT_SWITCH_AND_WITH_URL;
            case 4:
                return SettingsModel.NOTIFICATION_ITEM;
            default:
                return -1;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == SettingsModel.NOTIFICATION_ITEM) {
            View notificationView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_item, viewGroup, false);
            return new NotificationViewHolder(notificationView);
        } else {
            View settingsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.settings_item, viewGroup, false);
            return new SettingsViewHolder(settingsView);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (list.get(i).getType() == SettingsModel.NOTIFICATION_ITEM) {

            String notificationTitle = list.get(i).getNotificationTitle();
            String notificationType = list.get(i).getNotificationType();
            String notificationDescription = list.get(i).getNotificationDescription();
            String notificationPhotoUrl = list.get(i).getNotificationPhotoUrl();
            String notificationLink = list.get(i).getNotificationLink();
            long notificationTime = list.get(i).getNotificationTime();
            ((NotificationViewHolder) viewHolder).setNotificationViewHolder(notificationType, notificationTitle, notificationDescription, notificationPhotoUrl, notificationLink, notificationTime);

        } else {
            int type = list.get(i).getType();
            int image = list.get(i).getImage();
            String header = list.get(i).getHeader();
            String title = list.get(i).getTitle();
            String url = list.get(i).getUrl();
            boolean isEnable = list.get(i).isEnable();

            ((SettingsViewHolder) viewHolder).setSettingsViewHolder(type, image, header, title, url,isEnable);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SettingsViewHolder extends RecyclerView.ViewHolder {

        private ImageView settingImg;
        private TextView header, title;
        private Switch swt;
        private LinearLayout linearLayout;

        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            settingImg = itemView.findViewById(R.id.settings_image);
            header = itemView.findViewById(R.id.setting_header);
            title = itemView.findViewById(R.id.settings_title);
            swt = itemView.findViewById(R.id.settings_switch);
            linearLayout = itemView.findViewById(R.id.setting_item_layout);
        }

        private void setSettingsViewHolder(final int type, int image, String header, final String title, final String url, boolean isEnable) {
            if (type == SettingsModel.SETTINGS_HEADER) {
                this.linearLayout.setVisibility(View.GONE);
                this.header.setVisibility(View.VISIBLE);
                this.header.setText(header);
            } else {
                this.header.setVisibility(View.GONE);
                this.linearLayout.setVisibility(View.VISIBLE);

                if (type == SettingsModel.SETTINGS_ITEM_WITH_SWITCH) {
                    this.title.setVisibility(View.GONE);
                    this.swt.setVisibility(View.VISIBLE);
                    swt.setText(title);
                    swt.setChecked(isEnable);
                    swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            strorSettings(isChecked,title);
                        }
                    });
                } else {
                    this.swt.setVisibility(View.GONE);
                    this.title.setVisibility(View.VISIBLE);
                    this.title.setText(title);
                }

                settingImg.setImageResource(image);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if (type == SettingsModel.SETTINGS_ITEM_WITH_OUT_SWITCH_AND_WITH_URL) {
                            if (isValidUrl(url)) {
                                itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            }
                        }
                    }
                });

            }

        }

        private void strorSettings (boolean isChecked, String title){

            itemView.getContext().getSharedPreferences("Settings:"+title, Context.MODE_PRIVATE).edit().putBoolean(FirebaseAuth.getInstance().getCurrentUser().getUid(),isChecked).apply();
          //  Toast.makeText(itemView.getContext(), title+" - "+ isChecked, Toast.LENGTH_SHORT).show();
            if(title.equals("Dark Mode")){
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

            }
        }
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private ImageView notiHeaderImg, nofificationImg;
        private TextView description, title, date;


        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            nofificationImg = itemView.findViewById(R.id.notification_image);
            notiHeaderImg = itemView.findViewById(R.id.noti_first_image);
            title = itemView.findViewById(R.id.notification_title);
            description = itemView.findViewById(R.id.notification_description);
            date = itemView.findViewById(R.id.notification_date);

        }

        private void setNotificationViewHolder(final String notificationType, String notificationTitle, String notificationDescription, String notificationPhotoUrl, final String notificationLink, long notificationTime) {

            this.title.setText(notificationTitle);
            this.description.setText(notificationDescription);
            this.date.setText(formateTimeAgo(notificationTime));
            Glide.with(itemView.getContext()).load(notificationPhotoUrl).placeholder(R.color.place_holder).into(nofificationImg);

            if (notificationType.equals("WeeklyTestNotification")) {
                notiHeaderImg.setImageResource(R.drawable.mp2);
            }else if(notificationType.equals("ImportantNewsNotification")){
                notiHeaderImg.setImageResource(R.drawable.s3);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notificationType.equals("WeeklyTestNotification")) {
                        Intent intent = new Intent(itemView.getContext(), TestsActivity.class);
                        itemView.getContext().startActivity(intent);
                    }else if(notificationType.equals("ImportantNewsNotification")) {
                        itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(notificationLink)));
                    }
                }
            });

        }
    }

    private String formateTimeAgo(long dateInMili) {

        String finalTimeAgo = "";

        Calendar c = Calendar.getInstance();
        long currentDateInMili = c.getTimeInMillis();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentDateInMili - dateInMili);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentDateInMili - dateInMili);
        long hours = TimeUnit.MILLISECONDS.toHours(currentDateInMili - dateInMili);
        long days = TimeUnit.MILLISECONDS.toDays(currentDateInMili - dateInMili);
        long weeks = days / 7;


        if (seconds < 60) {
            finalTimeAgo =  "Just Now";
        } else if (minutes < 60) {
            if(minutes==1){
                finalTimeAgo = minutes + " minute ago";
            }else {
                finalTimeAgo = minutes + " minutes ago";
            }
        } else if (hours < 24) {
            if(hours==1){
                finalTimeAgo =hours + " hour ago";
            }else {
                finalTimeAgo = hours + " hours ago";
            }
        } else if (days < 7) {
            if(days==1){
                finalTimeAgo = days + " day ago";
            }else {
                finalTimeAgo = days + " days ago";
            }
        } else if (weeks < 5) {
            if(weeks==1){
                finalTimeAgo = weeks + " week ago";
            }else {
                finalTimeAgo = weeks + " weeks ago";
            }
        } else {
            finalTimeAgo = formateDate(dateInMili);
        }

        return finalTimeAgo;
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

    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
