package com.khan.quizzer_onlinequizapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.net.URL;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainPageAdapter extends RecyclerView.Adapter {

    private List<MainPageModel> mainPageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;

    public MainPageAdapter(List<MainPageModel> mainPageModelList) {
        this.mainPageModelList = mainPageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }


    @NonNull
    @Override
    public int getItemViewType(int position) {
        switch (mainPageModelList.get(position).getType()) {
            case 0:
                return MainPageModel.PROFILE_VIEW;
            case 1:
                return MainPageModel.GRID_VIEW;
            case 2:
                return MainPageModel.WEEKLY_TEST_VIEW;
            case 3:
                return MainPageModel.BANNER_VIEW;
            case 4:
                return MainPageModel.HORIZONTAL_BANNER_LAYOUT_VIEW;
            default:
                return -1;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case MainPageModel.PROFILE_VIEW:
                View profileView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mainpage_profile_item, viewGroup, false);
                return new ProfileViewholder(profileView);

            case MainPageModel.GRID_VIEW:
                View gridView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mainpage_grid_view_layout, viewGroup, false);
                return new GridViewholder(gridView);

            case MainPageModel.WEEKLY_TEST_VIEW:
                View weeklyTestView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mainpage_weeklytest_layout, viewGroup, false);
                return new WeeklyTestViewholder(weeklyTestView);

            case MainPageModel.BANNER_VIEW:
                View bannerView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mainpage_banner_item, viewGroup, false);
                return new BannerViewholder(bannerView);
            case MainPageModel.HORIZONTAL_BANNER_LAYOUT_VIEW:
                View horizontalBannerLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mainpage_horizontal_banner_layout, viewGroup, false);
                return new HorizontalBannerViewholder(horizontalBannerLayoutView);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        switch (mainPageModelList.get(i).getType()) {

            case MainPageModel.PROFILE_VIEW:
                //  String profileImg = mainPageModelList.get(i).getProfileImg();
                //  String name = mainPageModelList.get(i).getName();
                //   String instritute = mainPageModelList.get(i).getInstrituteName();
                ((ProfileViewholder) viewHolder).setProfileLayout();
                break;
            case MainPageModel.GRID_VIEW:
                List<HomeModel> homeModelList = mainPageModelList.get(i).getHomeModelList();
                ((GridViewholder) viewHolder).setGridLayout(homeModelList);
                break;
            case MainPageModel.WEEKLY_TEST_VIEW:
                int imageWeeklyTest = mainPageModelList.get(i).getImageWeeklyTest();
                String header = mainPageModelList.get(i).getHeader();
                long date = mainPageModelList.get(i).getDate();
                String title = mainPageModelList.get(i).getTitle();
                String description = mainPageModelList.get(i).getDescription();
                String setId = mainPageModelList.get(i).getSetId();

                ((WeeklyTestViewholder) viewHolder).setWeeklyTestLayout(header, date, title, description, setId, imageWeeklyTest);
                break;
            case MainPageModel.BANNER_VIEW:
                String bannerImg = mainPageModelList.get(i).getBannerImg();
                String bannerActionText = mainPageModelList.get(i).getBannerActionText();
                String bannerUrl = mainPageModelList.get(i).getBannerUrl();
                boolean bannerEnable = mainPageModelList.get(i).isBannerEnable();
                ((BannerViewholder) viewHolder).setBannerLayout(bannerImg, bannerActionText, bannerUrl, bannerEnable);
                break;
            case MainPageModel.HORIZONTAL_BANNER_LAYOUT_VIEW:
                String horizontalBannerLayoutTitle = mainPageModelList.get(i).getHorizontalScrollLayoutTitle();
                String horizontalBannerLayoutViewAllUrl = mainPageModelList.get(i).getHorizontalScrollLayoutViewAllUrl();
                List<MainPageModel> horizontalScrollBannerList = mainPageModelList.get(i).getHorizontalScrollBannerList();
                ((HorizontalBannerViewholder) viewHolder).setHorizontalBannerLayout(horizontalBannerLayoutTitle, horizontalBannerLayoutViewAllUrl, horizontalScrollBannerList);
                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return mainPageModelList.size();
    }

    public class ProfileViewholder extends RecyclerView.ViewHolder {

        private CircleImageView profileImg;
        private TextView name, instrituteName;

        public ProfileViewholder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.profile_name);
            instrituteName = itemView.findViewById(R.id.instritute_name);
        }

        private void setProfileLayout() {

            byte[] decodedBytes = Base64.decode(MainActivity.url, Base64.DEFAULT);
            Glide.with(itemView.getContext()).load(decodedBytes).placeholder(R.drawable.profile1_home).into(profileImg);

            this.name.setText(MainActivity.firstName + " " + MainActivity.lastName);
            this.instrituteName.setText(MainActivity.institute);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ProfileActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }


    public class GridViewholder extends RecyclerView.ViewHolder {

        private GridLayout gridLayout;

        public GridViewholder(@NonNull View itemView) {
            super(itemView);

            gridLayout = itemView.findViewById(R.id.gridview);

        }

        public void setGridLayout(List<HomeModel> homeModelList) {

            for (int x = 0; x < 3; x++) {
                CircleImageView image = gridLayout.getChildAt(x).findViewById(R.id.iamge_grid_view);
                TextView title = gridLayout.getChildAt(x).findViewById(R.id.title_grid_view);

                image.setImageResource(homeModelList.get(x).getImage());
                title.setText(homeModelList.get(x).getTitle());
            }

            gridLayout.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent categoryIntent = new Intent(itemView.getContext(), CategoriesActivity.class);
                    categoryIntent.putExtra("type", 0);
                    itemView.getContext().startActivity(categoryIntent);
                }
            });

            gridLayout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), TestsActivity.class);
                    intent.putExtra("type", 0);
                    itemView.getContext().startActivity(intent);
                }
            });
//            gridLayout.getChildAt(2).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(itemView.getContext(), TestsActivity.class);
//                    intent.putExtra("type",1);
//                    itemView.getContext().startActivity(intent);
//                }
//            });

            gridLayout.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent bookmarkIntent = new Intent(itemView.getContext(), BookmarksActivity.class);
                    itemView.getContext().startActivity(bookmarkIntent);
                }
            });
//            gridLayout.getChildAt(4).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent categoryIntent = new Intent(itemView.getContext(), CategoriesActivity.class);
//                    categoryIntent.putExtra("type", 1);
//                    itemView.getContext().startActivity(categoryIntent);
//                }
//            });
//            gridLayout.getChildAt(5).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent categoryIntent = new Intent(itemView.getContext(), CategoriesActivity.class);
//                    categoryIntent.putExtra("type", 2);
//                    itemView.getContext().startActivity(categoryIntent);
//                }
//            });
//            gridLayout.getChildAt(6).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent categoryIntent = new Intent(itemView.getContext(), CategoriesActivity.class);
//                    categoryIntent.putExtra("type", 3);
//                    itemView.getContext().startActivity(categoryIntent);
//                }
//            });

        }
    }

    public class WeeklyTestViewholder extends RecyclerView.ViewHolder {

        private CircleImageView imageWeeklyTest;
        private TextView header, dateTime, title, description;
        private LinearLayout attemp;

        public WeeklyTestViewholder(@NonNull View itemView) {
            super(itemView);
            imageWeeklyTest = itemView.findViewById(R.id.image_mainpage_weeklytest);
            header = itemView.findViewById(R.id.header_mainpage_weeklytest);
            dateTime = itemView.findViewById(R.id.date_time_mainpage_weeeklytest);
            title = itemView.findViewById(R.id.title_mainpage_weeklytest);
            description = itemView.findViewById(R.id.description_weeklytest_mainpage);
            attemp = itemView.findViewById(R.id.attemp_layout);
        }

        private void setWeeklyTestLayout(String header, long date, final String title, String des, final String setId, int imageWeeklyTest) {

            this.imageWeeklyTest.setImageResource(imageWeeklyTest);
            this.header.setText(header);
            this.dateTime.setText(formateTime(date));
            this.title.setText(title);
            this.description.setText(des);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), QuestionsActivity.class);
                    intent.putExtra("setId", setId);
                    intent.putExtra("type", 2);
                    intent.putExtra("test", title);
                    itemView.getContext().startActivity(intent);
                }
            });


        }
    }

    public class BannerViewholder extends RecyclerView.ViewHolder {

        private ImageView bannerImg;
        private TextView bannerText;

        public BannerViewholder(@NonNull View itemView) {
            super(itemView);

            bannerImg = itemView.findViewById(R.id.banner_img);
            bannerText = itemView.findViewById(R.id.bannerText);
        }

        private void setBannerLayout(String url, String bannerActionText, final String bannerUrl, boolean bannerEnable) {

            Glide.with(itemView.getContext()).load(url).transform(new CenterCrop(), new RoundedCorners(12)).placeholder(R.color.place_holder).into(bannerImg);
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
    }

    public class HorizontalBannerViewholder extends RecyclerView.ViewHolder {

        private TextView horizontalBannerLayoutTitle;
        private Button horizontalBannerLayoutViewAllButton;
        private RecyclerView horizontalBannerLayoutRecyclerView;

        public HorizontalBannerViewholder(@NonNull View itemView) {
            super(itemView);

            horizontalBannerLayoutTitle = itemView.findViewById(R.id.horizontal_banner_layout_title);
            horizontalBannerLayoutViewAllButton = itemView.findViewById(R.id.horizontal_banner_layout_view_all);
            horizontalBannerLayoutRecyclerView = itemView.findViewById(R.id.horizontal_banner_layout_rv);
            horizontalBannerLayoutRecyclerView.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalBannerLayout(String horizontalBannerLayoutTitle, final String horizontalBannerLayoutViewAllUrl, List<MainPageModel> horizontalScrollBannerList){
            this.horizontalBannerLayoutTitle.setText(horizontalBannerLayoutTitle);
            if(horizontalBannerLayoutViewAllUrl.isEmpty()){
                this.horizontalBannerLayoutViewAllButton.setVisibility(View.GONE);
            }else {
                this.horizontalBannerLayoutViewAllButton.setVisibility(View.VISIBLE);
            }
            this.horizontalBannerLayoutViewAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isValidUrl(horizontalBannerLayoutViewAllUrl)) {
                        itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(horizontalBannerLayoutViewAllUrl)));
                    }
                }
            });
            MainPageHorizontalBannerAdapter mainPageHorizontalBannerAdapter = new MainPageHorizontalBannerAdapter(horizontalScrollBannerList);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalBannerLayoutRecyclerView.setLayoutManager(linearLayoutManager);

            horizontalBannerLayoutRecyclerView.setAdapter(mainPageHorizontalBannerAdapter);
            mainPageHorizontalBannerAdapter.notifyDataSetChanged();
        }

    }


    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String formateTime(long dateInMili) {
        String finalTime = "";
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateInMili);
        Calendar current = Calendar.getInstance();

        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int currentDay = current.get(Calendar.DAY_OF_MONTH);

        int mMonth = c.get(Calendar.MONTH);
        String monthS = months[mMonth];
        int mHour = (c.get(Calendar.HOUR_OF_DAY)) % 12;
        int mAM_PM = (c.get(Calendar.AM_PM));
        int mMin = (c.get(Calendar.MINUTE));

        int difDay = currentDay - mDay;

        if (difDay == 0) {
            StringBuilder str = new StringBuilder();
            str.append("Today - ");
            str.append("" + mHour);
            if (mMin != 0) {
                str.append(":");
                String s = String.format("%02d", mMin);
                str.append(s);
            }
            if (mAM_PM == 0) {
                str.append(" AM");
            } else {
                str.append(" PM");
            }
            finalTime = str.toString();
        } else if (difDay == 1) {
            StringBuilder str = new StringBuilder();
            str.append("Yesterday - ");
            str.append("" + mHour);
            if (mMin != 0) {
                str.append(":");
                String s = String.format("%02d", mMin);
                str.append(s);
            }
            if (mAM_PM == 0) {
                str.append(" AM");
            } else {
                str.append(" PM");
            }
            finalTime = str.toString();
        } else if (difDay > 1 && difDay < 8) {
            finalTime = difDay + " days ago";
        } else {
            finalTime = mDay + " " + monthS;
        }

        return finalTime;
    }


}
