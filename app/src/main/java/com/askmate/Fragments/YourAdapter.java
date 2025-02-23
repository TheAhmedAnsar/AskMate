package com.askmate.Fragments;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.askmate.Modal.newsModal;
import com.askmate.NewsInfo;
import com.askmate.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YourAdapter extends FirebaseRecyclerPagingAdapter<newsModal, RecyclerView.ViewHolder> {

    private static final int AD_FREQUENCY = 5;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_AD = 1;

    private List<Object> combinedList = new ArrayList<>();

    public YourAdapter(@NonNull DatabasePagingOptions<newsModal> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull newsModal news) {
//        Collections.reverse((List<?>) news);
//        int actualPosition = getActualItemPosition(position);
//        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
        Log.d("NEWS", "onBindViewHolder: " + news.getTitle());

        normalHolder normalHolder = (normalHolder) holder;
            normalHolder.category.setText(news.getCategory());
            Glide.with(normalHolder.itemView.getContext()).load(news.getImage()).into(normalHolder.newsImage);
            normalHolder.title.setText(news.getTitle());
            normalHolder.timestamp.setText(news.getTime());

            normalHolder.knowMore.setOnClickListener(view -> {
                Intent myIntent = new Intent(view.getContext(), NewsInfo.class);
                myIntent.putExtra("title", news.getTitle());
                myIntent.putExtra("timestamp", news.getTime());
                myIntent.putExtra("category", news.getCategory());
                myIntent.putExtra("image", news.getImage());
                myIntent.putExtra("news", news.getContent());
                myIntent.putExtra("newby", news.getNewsBy());
                myIntent.putExtra("link", news.getLink());
                view.getContext().startActivity(myIntent);
            });
//        }
//
//        else {
//            AdViewHolder adHolder = (AdViewHolder) holder;
//            adHolder.hideAdView();
//
//            AdLoader adLoader = new AdLoader.Builder(holder.itemView.getContext(), "ca-app-pub-1647095499376164/2598134097")
//                    .forNativeAd(nativeAd -> {
//                        populateNativeAdView(nativeAd, adHolder.adView);
//                        adHolder.showAdView();
//                    })
//                    .withAdListener(new AdListener() {
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
//                            adHolder.adView.setVisibility(View.GONE);
//                            adHolder.placeholderView.setVisibility(View.VISIBLE);
//                        }
//                    })
//                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
//                    .build();
//
//            adLoader.loadAd(new AdRequest.Builder().build());
//        }
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem, parent, false);
            return new normalHolder(view);
//        } else if (viewType == VIEW_TYPE_AD) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//            return new AdViewHolder(view);
//        } else {
//            return null;
//        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        return (position % (AD_FREQUENCY + 1) == AD_FREQUENCY) ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
//    }

//    @Override
//    public int getItemCount() {
//        int originalCount = super.getItemCount();
//        int additionalAds = (originalCount / AD_FREQUENCY);
//        return originalCount + additionalAds;
//    }

//    private int getActualItemPosition(int position) {
//        return position - (position / (AD_FREQUENCY + 1));
//    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }

    class normalHolder extends RecyclerView.ViewHolder {
        public TextView category, title, timestamp;
        ShapeableImageView newsImage;
        RelativeLayout knowMore;

        public normalHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            title = itemView.findViewById(R.id.title);
            timestamp = itemView.findViewById(R.id.date);
            newsImage = itemView.findViewById(R.id.image);
            knowMore = itemView.findViewById(R.id.knowMore);
        }
    }

    class AdViewHolder extends RecyclerView.ViewHolder {
        NativeAdView adView;
        View placeholderView;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            placeholderView = itemView.findViewById(R.id.ad_placeholder_view);
            adView = itemView.findViewById(R.id.native_ad_view);
            adView.setMediaView(adView.findViewById(R.id.ad_media));
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        }

        public void hideAdView() {
            adView.setVisibility(View.GONE);
        }

        public void showAdView() {
            adView.setVisibility(View.VISIBLE);
        }
    }
}


//
//import android.content.Intent;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.askmate.Modal.Wrapper;
//import com.askmate.Modal.newsModal;
//import com.askmate.NewsInfo;
//import com.askmate.R;
//import com.bumptech.glide.Glide;
//import com.firebase.ui.database.paging.DatabasePagingOptions;
//import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
//import com.firebase.ui.database.paging.LoadingState;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdLoader;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.LoadAdError;
//import com.google.android.gms.ads.nativead.NativeAd;
//import com.google.android.gms.ads.nativead.NativeAdOptions;
//import com.google.android.gms.ads.nativead.NativeAdView;
//import com.google.android.material.imageview.ShapeableImageView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//        public class YourAdapter extends FirebaseRecyclerPagingAdapter<newsModal, RecyclerView.ViewHolder> {
//
//    private static final int AD_FREQUENCY = 3; // Show ad after every 5 items
//    private static final int VIEW_TYPE_ITEM = 0;
//    private static final int VIEW_TYPE_AD = 1;
//    int value = 0;
//    private List<newsModal> mainList = new ArrayList<>();
////            private List<Object> combinedList = new ArrayList<>();
//            private List<Wrapper> combinedList = new ArrayList<>();
//
//    /**
//     * Construct a new FirestorePagingAdapter from the given {@link DatabasePagingOptions}.
//     *
//     * @param options
//     */
//    public YourAdapter(@NonNull DatabasePagingOptions<newsModal> options) {
//        super(options);
//    }
////            public YourAdapter(@NonNull DatabasePagingOptions<Wrapper> options) {
////                super(options);
////            }
//
//    @Override
//    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull newsModal news) {
//        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
//            value = value+1;
//         normalHolder normalHolder =  (normalHolder) holder;
//////            newsModal newsItem = (newsModal) combinedList.get(position);
////            normalHolder.category.setText(newsItem.getCategory());
////            Glide.with(normalHolder.itemView.getContext()).load(newsItem.getImage()).into(normalHolder.newsImage);
////            normalHolder.title.setText(newsItem.getTitle());
////            normalHolder.timestamp.setText(newsItem.getTime());
////
////            normalHolder.knowMore.setOnClickListener(view -> {
////                Intent myIntent = new Intent(view.getContext(), NewsInfo.class);
////                myIntent.putExtra("title", newsItem.getTitle());
////                myIntent.putExtra("timestamp", newsItem.getTime());
////                myIntent.putExtra("category", newsItem.getCategory());
////                myIntent.putExtra("image", newsItem.getImage());
////                myIntent.putExtra("news", newsItem.getContent());
////                myIntent.putExtra("newby", newsItem.getNewsBy());
////                myIntent.putExtra("link", newsItem.getLink());
////                view.getContext().startActivity(myIntent);
////            });
//            normalHolder.category.setText(news.getCategory());
//            Glide.with(normalHolder.itemView.getContext()).load(news.getImage()).into(normalHolder.newsImage);
//            normalHolder.title.setText(news.getTitle());
//            normalHolder.timestamp.setText(news.getTime());
//
//            normalHolder.knowMore.setOnClickListener(view -> {
//                Intent myIntent = new Intent(view.getContext(), NewsInfo.class);
//                myIntent.putExtra("title", news.getTitle());
//                myIntent.putExtra("timestamp", news.getTime());
//                myIntent.putExtra("category", news.getCategory());
//                myIntent.putExtra("image", news.getImage());
//                myIntent.putExtra("news", news.getContent());
//                myIntent.putExtra("newby", news.getNewsBy());
//                myIntent.putExtra("link", news.getLink());
//                view.getContext().startActivity(myIntent);
//            });
//        }
//
//        else {
//            AdViewHolder adHolder = (AdViewHolder) holder;
//            adHolder.hideAdView();
//
//            AdLoader adLoader = new AdLoader.Builder(holder.itemView.getContext(), "ca-app-pub-1647095499376164/2598134097")
//                    .forNativeAd(nativeAd -> {
//                        populateNativeAdView(nativeAd, adHolder.adView);
//                        adHolder.showAdView();
//                    })
//                    .withAdListener(new AdListener() {
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
//                            adHolder.adView.setVisibility(View.GONE);
//                            adHolder.placeholderView.setVisibility(View.VISIBLE);
//                        }
//                    })
//                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
//                    .build();
//
//            adLoader.loadAd(new AdRequest.Builder().build());
//        }
//
//    }
//
//            // try
////            @Override
////            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Wrapper wrapper)
////            {
////                if (getItemViewType(position) == VIEW_TYPE_ITEM) {
////                    normalHolder normalHolder = (normalHolder) holder;
////                    newsModal news = wrapper.news;
////
////                    normalHolder.category.setText(news.getCategory());
////                    Glide.with(normalHolder.itemView.getContext()).load(news.getImage()).into(normalHolder.newsImage);
////                    normalHolder.title.setText(news.getTitle());
////                    normalHolder.timestamp.setText(news.getTime());
////
////                    normalHolder.knowMore.setOnClickListener(view -> {
////                        Intent myIntent = new Intent(view.getContext(), NewsInfo.class);
////                        myIntent.putExtra("title", news.getTitle());
////                        myIntent.putExtra("timestamp", news.getTime());
////                        myIntent.putExtra("category", news.getCategory());
////                        myIntent.putExtra("image", news.getImage());
////                        myIntent.putExtra("news", news.getContent());
////                        myIntent.putExtra("newby", news.getNewsBy());
////                        myIntent.putExtra("link", news.getLink());
////                        view.getContext().startActivity(myIntent);
////                    });
////                } else {
////                    AdViewHolder adHolder = (AdViewHolder) holder;
////                    adHolder.hideAdView();
////
////                    AdLoader adLoader = new AdLoader.Builder(holder.itemView.getContext(), "ca-app-pub-1647095499376164/2598134097")
////                            .forNativeAd(nativeAd -> {
////                                populateNativeAdView(nativeAd, adHolder.adView);
////                                adHolder.showAdView();
////                            })
////                            .withAdListener(new AdListener() {
////                                @Override
////                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
////                                    Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
////                                    adHolder.adView.setVisibility(View.GONE);
////                                    adHolder.placeholderView.setVisibility(View.VISIBLE);
////                                }
////                            })
////                            .withNativeAdOptions(new NativeAdOptions.Builder().build())
////                            .build();
////
////                    adLoader.loadAd(new AdRequest.Builder().build());
////                }
////            }
//
//
////            @Override
////            public int getItemCount() {
////                int itemCount = super.getItemCount();
////                int adCount = itemCount / AD_FREQUENCY;
////                return itemCount + adCount;
////            }
//
//            @Override
//    protected void onLoadingStateChanged(@NonNull LoadingState state) {
//
//    }
//
////            @Override
////            public int getItemCount() {
////                Log.d("count", "getItemCount: " + super
////                        .getItemCount());
////                return super.getItemCount();
////            }
////                        public void setItems(List<newsModal> items) {
////                combinedList.clear();
////                for (int i = 0; i < items.size(); i++) {
////                    combinedList.add(items.get(i));
////                    if ((i + 1) % AD_FREQUENCY == 0) {
////                        combinedList.add(new Object()); // Placeholder for ad
////                    }
////                }
////                notifyDataSetChanged();
////            }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == VIEW_TYPE_ITEM) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem, parent, false);
//            return new normalHolder(view);
//        } else if (viewType == VIEW_TYPE_AD) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//            return new AdViewHolder(view);
//        }
//        else
//        {
//            return null;
//        }
//    }
//
////    @Override
////    public int getItemViewType(int position) {
//////        return (position % (AD_FREQUENCY + 1) == AD_FREQUENCY) ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
////        return (position + 1) % 4 == 0 ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
//////        if ((position + 1) % AD_FREQUENCY == 0) {
//////            return VIEW_TYPE_AD;
//////        }
//////        return VIEW_TYPE_ITEM;
////    }
//
////            @Override
////            public int getItemViewType(int position) {
////                return combinedList.get(position).isAd ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
////            }
//
////            @Override
////            public int getItemViewType(int position) {
////                return getItem(position).isAd ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
////            }
//
////            public void setItems(List<newsModal> items) {
////                combinedList.clear();
////                for (int i = 0; i < items.size(); i++) {
////                    combinedList.add(new Wrapper(items.get(i), false));
////                    if ((i + 1) % AD_FREQUENCY == 0) {
////                        combinedList.add(new Wrapper(null, true)); // Placeholder for ad
////                    }
////                }
////                notifyDataSetChanged();
////            }
//
//    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
//        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
//        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
//        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
//
//        if (nativeAd.getIcon() == null) {
//            adView.getIconView().setVisibility(View.GONE);
//        } else {
//            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
//            adView.getIconView().setVisibility(View.VISIBLE);
//        }
//
//        adView.setNativeAd(nativeAd);
//    }
//
//    class normalHolder extends RecyclerView.ViewHolder {
//        public TextView category, title, timestamp;
//        ShapeableImageView newsImage;
//        RelativeLayout knowMore;
//
//        public normalHolder(@NonNull View itemView) {
//            super(itemView);
//            category = itemView.findViewById(R.id.category);
//            title = itemView.findViewById(R.id.title);
//            timestamp = itemView.findViewById(R.id.date);
//            newsImage = itemView.findViewById(R.id.image);
//            knowMore = itemView.findViewById(R.id.knowMore);
//        }
//    }
//
//     class AdViewHolder extends RecyclerView.ViewHolder {
//        NativeAdView adView;
//        View placeholderView;
//
//        public AdViewHolder(@NonNull View itemView) {
//            super(itemView);
//            placeholderView = itemView.findViewById(R.id.ad_placeholder_view);
//            adView = itemView.findViewById(R.id.native_ad_view);
//            adView.setMediaView(adView.findViewById(R.id.ad_media));
//            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//            adView.setBodyView(adView.findViewById(R.id.ad_body));
//            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
//            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//        }
//
//        public void hideAdView() {
//            adView.setVisibility(View.GONE);
//        }
//
//        public void showAdView() {
//            adView.setVisibility(View.VISIBLE);
//        }
//    }
//}
