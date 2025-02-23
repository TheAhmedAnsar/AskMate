package com.askmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.askmate.Modal.Question;
import com.askmate.Modal.newsModal;
import com.askmate.NewsInfo;
import com.askmate.QnA_Ans;
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class newsAdapter  extends RecyclerView.Adapter<newsAdapter.ViewHolder> {

    private ArrayList<newsModal> newsModals;
    private Context mcontext;
//    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_AD = 1;
    private static final int AD_FREQUENCY = 2; // Ad after every 2 items

    public newsAdapter(ArrayList<newsModal> newsModals, Context mcontext) {
        this.newsModals = newsModals;
        this.mcontext = mcontext;
    }

    public void addData(List<newsModal> newData) {
        newsModals.addAll(newData);
        int startPosition = newsModals.size();
        notifyItemRangeInserted(startPosition, newData.size());
    }
//    @Override
//    public int getItemViewType(int position) {
//        return (position % (AD_FREQUENCY + 1) == AD_FREQUENCY) ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
//    }
    @NonNull
    @Override
    public newsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem, parent, false);
//        return new ViewHolder(view);

//        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem, parent, false);
            return new ItemViewHolder(view);
//        } else {
////            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout, parent, false);
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//            return new AdViewHolder(view);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull newsAdapter.ViewHolder holder, int position) {
        newsModal modal = newsModals.get(position);

//        if (getItemViewType(position) == VIEW_TYPE_ITEM) {


            ((ItemViewHolder) holder).category.setText(modal.getCategory());
//        holder.
//        holder.news.setText(modal.getNews());
            Glide.with(mcontext).load(modal.getImage()).into( ((ItemViewHolder) holder).newsImage);
            ((ItemViewHolder) holder).title.setText(modal.getTitle());
            ((ItemViewHolder) holder).timestamp.setText(modal.getTime());

            ((ItemViewHolder) holder).knowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), NewsInfo.class);
                    myIntent.putExtra("title", modal.getTitle());
                    myIntent.putExtra("timestamp", modal.getTime());
                    myIntent.putExtra("category", modal.getCategory());
                    myIntent.putExtra("image", modal.getImage());
                    myIntent.putExtra("news", modal.getContent());
                    myIntent.putExtra("newby", modal.getNewsBy());
                    myIntent.putExtra("link", modal.getLink());
                    view.getContext().startActivity(myIntent);
                }
            });

//        }

//        else if (holder instanceof AdViewHolder)
//        {
//            AdViewHolder adHolder = (AdViewHolder) holder;
//
////            AdViewHolder adHolder = (AdViewHolder) holder;
//            // Initially hide the ad view
//            adHolder.hideAdView();
//
//            // native updates
////            else if (holder instanceof AdViewHolder) {
//            AdLoader adLoader = new AdLoader.Builder(mcontext, "ca-app-pub-1647095499376164/2598134097")
//                    .forNativeAd(nativeAd -> {
//                        // Populate the native ad view
//                        populateNativeAdView(nativeAd, adHolder.adView);
//                        adHolder.showAdView();
//                    })
//                    .withAdListener(new AdListener() {
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
////                            populateNativeAdView(nativeAd, adHolder.adView);
//                            adHolder.adView.setVisibility(View.GONE);
//                            adHolder.placeholderView.setVisibility(View.VISIBLE);
//
//                        }
//                    })
//                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
//                    .build();
//
//            adLoader.loadAd(new AdRequest.Builder().build());
////        }
//
//            // Example:
////            adHolder.adView.setAdListener(new AdListener() {
////                @Override
////                public void onAdFailedToLoad(LoadAdError loadAdError) {
////                    // Log the error message
////                    Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
////                    Log.e("AdMob", "Loading" );
////
////                    // Handle the error and retry after a delay
////                    if (loadAdError.getCode() == AdRequest.ERROR_CODE_INTERNAL_ERROR ||
////                            loadAdError.getCode() == AdRequest.ERROR_CODE_NO_FILL) {
////                        // Retry after a short delay (e.g., 30 seconds)
////                        new Handler().postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
////                                AdRequest adRequest = new AdRequest.Builder().build();
////                                adHolder.adView.loadAd(adRequest);
////                            }
////                        }, 3000); // 30 seconds delay
////                    }
////                }
////            });
//
////            AdRequest adRequest = new AdRequest.Builder().build();
////            adHolder.adView.loadAd(adRequest);
////            adHolder.adView.setAdListener(new AdListener() {
////                @Override
////                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
////                    Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
////                }
////            });
//        }

//        holder.
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the headline
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        // Set the body
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        // Set the call to action
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // Set the icon if available
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        // Assign the native ad object to the native ad view
        adView.setNativeAd(nativeAd);
    }


    // Method to truncate the news text to 60 words
    private String truncateNewsText(String newsText) {
        String[] words = newsText.split("\\s+");
        StringBuilder truncatedText = new StringBuilder();
        for (int i = 0; i < Math.min(words.length, 60); i++) {
            truncatedText.append(words[i]).append(" ");
        }
        return truncatedText.toString().trim();
    }

    @Override
    public int getItemCount() {
        return newsModals.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView category, title;
//        public TextView timestamp;
//        ShapeableImageView newsImage, knowMore;


        public ViewHolder(View itemView) {
            super(itemView);
//            category = itemView.findViewById(R.id.category);
////            category = itemView.findViewById(R.id.category);
//            title = itemView.findViewById(R.id.title);
//            timestamp = itemView.findViewById(R.id.date);
//            newsImage = itemView.findViewById(R.id.image);
//            knowMore = itemView.findViewById(R.id.knowMore);
//

        }
    }

    public static class ItemViewHolder  extends ViewHolder {
        public TextView category, title;
        public TextView timestamp;
        ShapeableImageView newsImage;
            RelativeLayout knowMore;


        public ItemViewHolder (@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.category);
//            category = itemView.findViewById(R.id.category);
            title = itemView.findViewById(R.id.title);
            timestamp = itemView.findViewById(R.id.date);
            newsImage = itemView.findViewById(R.id.image);
            knowMore = itemView.findViewById(R.id.knowMore);

        }

    }

    public static class AdViewHolder extends ViewHolder {
        NativeAdView adView;

        View placeholderView;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize ad view
            placeholderView = itemView.findViewById(R.id.ad_placeholder_view);
            adView = (NativeAdView) itemView.findViewById(R.id.native_ad_view);
            // Initialize other ad assets
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
