package com.askmate.Adapter;

import android.content.Context;
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

import com.askmate.Fragments.YourAdapter;
import com.askmate.Modal.newsModal;
import com.askmate.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.imageview.ShapeableImageView;
import com.marcorei.infinitefire.InfiniteFireArray;
import com.marcorei.infinitefire.InfiniteFireRecyclerViewAdapter;

public class Adapter extends InfiniteFireRecyclerViewAdapter<newsModal> {

    private Context context;
    public static final int VIEW_TYPE_CONTENT = 1;
    public static final int VIEW_TYPE_FOOTER = 2;
    private boolean loadingMore = false;
    private static final int AD_FREQUENCY = 3; // Ad after every 3 items
    public Adapter(InfiniteFireArray snapshots, Context context) {
        super(snapshots, 0, 1);
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CONTENT) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem, parent, false);

            return new normalHolder(view);
        }
        else if (viewType == VIEW_TYPE_FOOTER) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
            return new AdViewHolder(view);
        }
        else
        {
            return null;
        }

        }


    /**
     * @return status of load-more loading procedures
     */
    public boolean isLoadingMore() {
        return loadingMore;
    }


    public void setLoadingMore(boolean loadingMore) {
        if (loadingMore == this.isLoadingMore()) return;
        this.loadingMore = loadingMore;
        notifyItemChanged(getItemCount() - 1);
    }

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


    @Override
    public int getItemCount() {
        int contentCount = snapshots.getCount();
        int adCount = (contentCount / AD_FREQUENCY);
        return contentCount + adCount;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == getItemCount() - 1) {
//            return VIEW_TYPE_FOOTER;
//        }
//        return VIEW_TYPE_CONTENT;
//    }
    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % (AD_FREQUENCY + 1) == 0) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_CONTENT;
    }



    private int getContentPosition(int position) {
        return position - (position / (AD_FREQUENCY + 1));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        normalHolder normalHolder = (normalHolder) holder;

//        if ( viewType > 0) {
            switch (viewType) {

                case VIEW_TYPE_CONTENT:
                    Log.d("view", "onBindViewHolder: " + "VIEW 1");
//                            newsModal news = snapshots.getItem(position - indexOffset).getValue();
                    newsModal news = snapshots.getItem(getContentPosition(position)).getValue();
        normalHolder.category.setText(news.getCategory());
        Glide.with(normalHolder.itemView.getContext()).load(news.getImage()).into(normalHolder.newsImage);
        normalHolder.title.setText(news.getTitle());
        normalHolder.timestamp.setText(news.getTime());

                    break;
//
                case VIEW_TYPE_FOOTER:
                    AdViewHolder adHolder = (AdViewHolder) holder;
            adHolder.hideAdView();

            AdLoader adLoader = new AdLoader.Builder(holder.itemView.getContext(), "ca-app-pub-1647095499376164/2598134097")
                    .forNativeAd(nativeAd -> {
                        populateNativeAdView(nativeAd, adHolder.adView);
                        adHolder.showAdView();
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
                            adHolder.adView.setVisibility(View.GONE);
                            adHolder.placeholderView.setVisibility(View.VISIBLE);
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
                    //            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//            return new AdViewHolder(view);

break;

            }
//        }
//        else
//        {
//            Log.d("view", "onBindViewHolder: " + "VIEW is empty");
//
//        }
//        newsModal news = snapshots.getItem(position - indexOffset).getValue();
//        normalHolder normalHolder = (normalHolder) holder;
//
//        normalHolder.category.setText(news.getCategory());
//        Glide.with(normalHolder.itemView.getContext()).load(news.getImage()).into(normalHolder.newsImage);
//        normalHolder.title.setText(news.getTitle());
//        normalHolder.timestamp.setText(news.getTime());

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
//    class AdViewHolder extends RecyclerView.ViewHolder {
    class AdViewHolder extends normalHolder{
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