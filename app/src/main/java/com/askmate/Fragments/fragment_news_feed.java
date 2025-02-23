package com.askmate.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.askmate.Adapter.Adapter;
import com.askmate.Adapter.newsAdapter;
import com.askmate.GridSpacingItemDecoration;
import com.askmate.Modal.Question;
import com.askmate.Modal.Wrapper;
import com.askmate.Modal.newsModal;
import com.askmate.NewsInfo;
import com.askmate.QnA_Ans;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.rvadapter.AdmobNativeAdAdapter;
import com.marcorei.infinitefire.InfiniteFireArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_news_feed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_news_feed extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<newsModal> newsModalArrayList;
    private ArrayList<newsModal> emptyList;
     DatabaseReference newRef;
     newsAdapter adapter;
    ProgressBar loadingPB;
    String lastKey = "";
    boolean isLoading = false;

    LinearLayoutManager layoutManager;


    //    FirebaseRecyclerPagingAdapter<newsModal, RecyclerView.ViewHolder> mAdapter;
//    FirebaseRecyclerPagingAdapter<newsModal, RecyclerView.ViewHolder> mAdapter;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_AD = 1;
    private static final int AD_FREQUENCY = 5; // Ad after every 2 items
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_news_feed() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_news_feed.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_news_feed newInstance(String param1, String param2) {
        fragment_news_feed fragment = new fragment_news_feed();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);;
        recyclerView=view.findViewById(R.id.recyclerView);
loadingPB = view.findViewById(R.id.idPBLoading);
        newsModalArrayList=new ArrayList<>();

//        newsAdapter adapter=new newsAdapter(newsModalArrayList,getActivity());
//        YourAdapter adapter1 ;
        layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()), );

//        recyclerView.re
        newRef =  FirebaseDatabase.getInstance().getReference().child("news");
//
//        Query query = newRef.orderByChild("timestamp");
//
//
//        recyclerView.setHasFixedSize(true);
//        initializedata();

//        Query query = FirebaseDatabase.getInstance().getReference().child("news").orderByKey();
//
//        final InfiniteFireArray<newsModal> array = new InfiniteFireArray<>(
//                newsModal.class,// Model Class
//                query,//Database Ref
//                3,// Initial Size
//                3, //how many items to Load every time
//                false,//limitToFirst //True means the old appears on top
//                true
//        );
//
//
//        final Adapter adapter2 = new Adapter(array, getActivity());
//
//        recyclerView.setAdapter(adapter2);
//
//        array.addOnLoadingStatusListener(new InfiniteFireArray.OnLoadingStatusListener() {
//            @Override
//            public void onChanged(EventType type) {
//                switch (type) {
//                    case LoadingContent:
//                        adapter2.setLoadingMore(true);
//                        break;
//                    case LoadingNoContent:
//                        adapter2.setLoadingMore(false);
//                        break;
//                    case Done:
////                        swipeRefreshLayout.setRefreshing(false);
//                        adapter2.setLoadingMore(false);
//                        break;
//                }
//            }
//        });
//
////        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
////            @Override
////            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//////                if (dy < 0) {
//////
//////                    return;
//////
//////                }
//////                if (linearLayoutManager.findLastVisibleItemPosition() < array.getCount() - 5) {
//////                    return;
//////                }
////                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE)
////                {
////
////                }
////
//////                Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
////                array.more();
////            }
////        });

        emptyList = new ArrayList<>();
adapter = new newsAdapter(emptyList, getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    Log.d("tag", "scrolled to end " + lastKey);
////                                                Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
//                    isLoading = true; // Set the loading flag to true
//                    loadmoredata(lastKey);
//
                    if (!isLoading) {
                        isLoading = true;
                        loadmoredata(lastKey);
                    }
                }
            }
        });


        loadinitialdata();
//        recyclerView.addOnScrollListener(new );


//        PagedList.Config config = new PagedList.Config.Builder()
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance(2)
//                .setPageSize(1)
//                .build();
//
//        DatabasePagingOptions<newsModal> options = new DatabasePagingOptions.Builder<newsModal>()
//                .setLifecycleOwner(this)
//                .setQuery(newRef, config, newsModal.class)
//                .build();
//
//
////        adapter = new YourAdapter(options);
//        adapter1 = new YourAdapter(options);
//
//        // Set up RecyclerView
//        recyclerView.setAdapter(adapter1);

//        AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.Companion
//                .with("ca-app-pub-1647095499376164/1668195803", adapter1,"small")
//                .adItemInterval(4).build();
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // or GridLayoutManager
//
//        recyclerView.setAdapter(admobNativeAdAdapter);

//        FirebaseDatabase.getInstance().getReference().child("news").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot: snapshot.getChildren())
//                {
//                    newsModal NewsModal  = dataSnapshot.getValue(newsModal.class);
//recyclerDataArrayList.add(0,NewsModal);
//                    Log.d("title", "onDataChange: " + NewsModal.getTitle());
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        recyclerView.setAdapter(adapter);
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            PagedList.Config config = new PagedList.Config.Builder()
//                    .setEnablePlaceholders(false)
//                    .setPrefetchDistance(2)
//                    .setPageSize(1)
//                    .build();
//
//            DatabasePagingOptions<newsModal> options = new DatabasePagingOptions.Builder<newsModal>()
//                    .setLifecycleOwner(this)
//                    .setQuery(newRef, config, newsModal.class)
//                    .build();
//
//            requireActivity().runOnUiThread(() -> {
//                mAdapter = new FirebaseRecyclerPagingAdapter<newsModal, normalHolder>(options) {
//                    @Override
//                    public int getItemViewType(int position) {
//                        return (position % (AD_FREQUENCY + 1) == AD_FREQUENCY) ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
//                    }
//
//                    public void refresh() {
//                        // Reinitialize the adapter with the same options to force a refresh
//                        notifyDataSetChanged();  // Trigger a UI update
//                    }
//
//
//                    @NonNull
//                    @Override
//                    public normalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        if (viewType == VIEW_TYPE_ITEM) {
//                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem, parent, false);
//                            return new normalHolder(view);
//                        } else {
////            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout, parent, false);
//                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//                            return new AdViewHolder(view);
//                        }
//                    }
//
//                    @Override
//                    protected void onBindViewHolder(@NonNull normalHolder holder, int position, @NonNull newsModal news) {
//
//                        DatabaseReference reference = getRef(position);
//                        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
//
//
//                            holder.category.setText(news.getCategory());
////        holder.
////        holder.news.setText(modal.getNews());
//                            Glide.with(getActivity()).load(news.getImage()).into( (holder).newsImage);
//                            (holder).title.setText(news.getTitle());
//                            (holder).timestamp.setText(news.getTime());
//
//                            (holder).knowMore.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Intent myIntent = new Intent(view.getContext(), NewsInfo.class);
//                                    myIntent.putExtra("title", news.getTitle());
//                                    myIntent.putExtra("timestamp", news.getTime());
//                                    myIntent.putExtra("category", news.getCategory());
//                                    myIntent.putExtra("image", news.getImage());
//                                    myIntent.putExtra("news", news.getContent());
//                                    myIntent.putExtra("newby", news.getNewsBy());
//                                    myIntent.putExtra("link", news.getLink());
//                                    view.getContext().startActivity(myIntent);
//                                }
//                            });
//
//                        }
//
//
//                        else if (holder instanceof AdViewHolder) {
//                            AdViewHolder adHolder = (AdViewHolder) holder;
//
////            AdViewHolder adHolder = (AdViewHolder) holder;
//                            // Initially hide the ad view
//                            adHolder.hideAdView();
//
//                            // native updates
////            else if (holder instanceof AdViewHolder) {
//                            AdLoader adLoader = new AdLoader.Builder(getActivity(), "ca-app-pub-1647095499376164/2598134097")
//                                    .forNativeAd(nativeAd -> {
//                                        // Populate the native ad view
//                                        populateNativeAdView(nativeAd, adHolder.adView);
//                                        adHolder.showAdView();
//                                    })
//                                    .withAdListener(new AdListener() {
//                                        @Override
//                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                                            Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
////                            populateNativeAdView(nativeAd, adHolder.adView);
//                                            adHolder.adView.setVisibility(View.GONE);
//                                            adHolder.placeholderView.setVisibility(View.VISIBLE);
//
//                                        }
//                                    })
//                                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
//                                    .build();
//
//                            adLoader.loadAd(new AdRequest.Builder().build());
//                        }
////        }
////                if (getItemViewType(position) == VIEW_TYPE_ITEM) {
////                    String questionKey = reference.getKey();
////                    question.setQuestionId(questionKey);
////                    bindItemViewHolder(holder, position, question);
////
////                    holder.bind(question, itemActionListener);
////
////                    fetchUserProfile(question.getUid(), holder);
////                    fetchLikesCount(question.getQuestionId(), holder);
////                    fetchanswer(question.getQuestionId(), holder);
////
////
////                    holder.commentLayout.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View view) {
////                            Intent myIntent = new Intent(view.getContext(), QnA_Ans.class);
////                            myIntent.putExtra("check", "QnA");
////                            myIntent.putExtra("name", holder.name.getText().toString());
////                            myIntent.putExtra("question", question.getQuestion());
////                            myIntent.putExtra("category", question.getCategory());
////                            myIntent.putExtra("time", holder.date.getText().toString());
////                            myIntent.putExtra("image", question.getImage());
////                            myIntent.putExtra("uid", question.getUid());
////                            myIntent.putExtra("questionId", question.getQuestionId());
////
////                            view.getContext().startActivity(myIntent);
////                        }
////                    });
////
////                }
////
////                else {
////                    bindAdViewHolder((ItemViewHolder.AdViewHolder) holder, position);
////                }
//
//
//                    }
//
//
//                    @Override
//                    protected void onLoadingStateChanged(@NonNull LoadingState state) {
//
//                        switch (state) {
//                            case LOADING_INITIAL:
//                            case LOADING_MORE:
//                                // Do your loading animation
//                                loadingPB.setVisibility(View.VISIBLE);
//
////                        mSwipeRefreshLayout.setRefreshing(true);
//                                break;
//
//                            case LOADED:
//                                // Stop Animation
//                                loadingPB.setVisibility(View.GONE);
//
//                                break;
//
//                            case FINISHED:
//                                //Reached end of Data set
//                                loadingPB.setVisibility(View.GONE);
//                                break;
//
//                            case ERROR:
//                                retry();
//                                break;
//                        }
//
//                    }
//                };
//
//
//                recyclerView.setAdapter(mAdapter);
//                loadingPB.setVisibility(View.GONE);
//            });
//        });

// Show loading spinner
        loadingPB.setVisibility(View.VISIBLE);

        return view;
    }


    public void loadinitialdata()
    {

        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference().child("news");
        Query initialQuery = databaseReference.orderByKey().limitToLast(3); // Load 10 latest items
        initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren())
                {
                    String key = snapshot1.getKey();
                    Log.d("key", "onDataChange: " + key);
                    newsModal newsModal = snapshot1.getValue(newsModal.class);
                    newsModal.setNewsID(key);
                    newsModalArrayList.add(newsModal);
                }
                Collections.reverse(newsModalArrayList);
                adapter.addData(newsModalArrayList);
                lastKey = newsModalArrayList.get(newsModalArrayList.size()-1).getNewsID();

                Log.d("lastkey", "onDataChange: " + lastKey);
//                if (!newsModalArrayList.isEmpty()) {
//                    lastKey = newsModalArrayList.get(newsModalArrayList.size() - 1).getNewsID();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void loadmoredata(String id) {
        String idd = newsModalArrayList.get(newsModalArrayList.size()-1).getNewsID();
//        Log.d("value", "IDDDD------>: " + id + lastKey);
        ArrayList<newsModal> newsModals = new ArrayList<>();

        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference().child("news");

        Query initialQuery = databaseReference.orderByKey().endBefore(id).limitToLast(6); // Load 10 latest items
        initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    String questionkey = snapshot1.getKey();
                    String title = snapshot1.child("title").getValue(String.class);
                    newsModal modal = snapshot1.getValue(newsModal.class);
                    Log.d("key123", "onDataChange: " + title);
                    assert modal != null;
                    modal.setNewsID(questionkey);
                    Log.d("title", "onDataChange: " + title);

                    if (!questionkey.equals(idd)) {
                        newsModals.add(modal);
                    } else {
                        Toast.makeText(getActivity(), "Not this time", Toast.LENGTH_SHORT).show();
                    }
                }

                if (newsModals.isEmpty()) {
//                    Toast.makeText(getActivity(), "Nothing left", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                }

                else {

                    Collections.reverse(newsModals);
                    adapter.addData(newsModals);
                    // Update lastKey for the next query
                    if (!newsModals.isEmpty()) {
                        lastKey = newsModals.get(newsModals.size() - 1).getNewsID();
                        Log.d("id", "LastKey updated to: " + lastKey);
                    }

                }
                isLoading = false;
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false;

            }
        });
    }


//    public void initializedata() {
//
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance(2)
//                .setPageSize(1)
//                .build();
//
//        DatabasePagingOptions<newsModal> options = new DatabasePagingOptions.Builder<newsModal>()
//                .setLifecycleOwner(this)
//                .setQuery(newRef, config, newsModal.class)
//                .build();
//
//        mAdapter = new FirebaseRecyclerPagingAdapter<newsModal, normalHolder>(options) {
//            @Override
//            public int getItemViewType(int position) {
//                return (position % (AD_FREQUENCY + 1) == AD_FREQUENCY) ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
//            }
//
//            public void refresh() {
//                // Reinitialize the adapter with the same options to force a refresh
//                notifyDataSetChanged();  // Trigger a UI update
//            }
//
//
//            @NonNull
//            @Override
//            public normalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//               if (viewType == VIEW_TYPE_ITEM) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem, parent, false);
//                return new normalHolder(view);
//        } else {
////            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout, parent, false);
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//            return new AdViewHolder(view);
//        }
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull normalHolder holder, int position, @NonNull newsModal news) {
//
//                if (getItemViewType(position) == VIEW_TYPE_ITEM) {
//                   holder.category.setText(news.getCategory());
//                    Glide.with(getActivity()).load(news.getImage()).into( (holder).newsImage);
//                    (holder).title.setText(news.getTitle());
//                    (holder).timestamp.setText(news.getTime());
//
//                    (holder).knowMore.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent myIntent = new Intent(view.getContext(), NewsInfo.class);
//                            myIntent.putExtra("title", news.getTitle());
//                            myIntent.putExtra("timestamp", news.getTime());
//                            myIntent.putExtra("category", news.getCategory());
//                            myIntent.putExtra("image", news.getImage());
//                            myIntent.putExtra("news", news.getContent());
//                            myIntent.putExtra("newby", news.getNewsBy());
//                            myIntent.putExtra("link", news.getLink());
//                            view.getContext().startActivity(myIntent);
//                        }
//                    });
//
//                }
//
//
//        else if (holder instanceof AdViewHolder) {
//                    AdViewHolder adHolder = (AdViewHolder) holder;
//
////            AdViewHolder adHolder = (AdViewHolder) holder;
//                    // Initially hide the ad view
//                    adHolder.hideAdView();
//
//                    // native updates
////            else if (holder instanceof AdViewHolder) {
//                    AdLoader adLoader = new AdLoader.Builder(getActivity(), "ca-app-pub-1647095499376164/2598134097")
//                            .forNativeAd(nativeAd -> {
//                                // Populate the native ad view
//                                populateNativeAdView(nativeAd, adHolder.adView);
//                                adHolder.showAdView();
//                            })
//                            .withAdListener(new AdListener() {
//                                @Override
//                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                                    Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
////                            populateNativeAdView(nativeAd, adHolder.adView);
//                                    adHolder.adView.setVisibility(View.GONE);
//                                    adHolder.placeholderView.setVisibility(View.VISIBLE);
//
//                                }
//                            })
//                            .withNativeAdOptions(new NativeAdOptions.Builder().build())
//                            .build();
//
//                    adLoader.loadAd(new AdRequest.Builder().build());
//                }
//
//
//            }
//
//
//            @Override
//            protected void onLoadingStateChanged(@NonNull LoadingState state) {
//
//                switch (state) {
//                    case LOADING_INITIAL:
//                    case LOADING_MORE:
//                        // Do your loading animation
//                        loadingPB.setVisibility(View.VISIBLE);
//
////                        mSwipeRefreshLayout.setRefreshing(true);
//                        break;
//
//                    case LOADED:
//                        // Stop Animation
//                        loadingPB.setVisibility(View.GONE);
//
//                        break;
//
//                    case FINISHED:
//                        //Reached end of Data set
//                        loadingPB.setVisibility(View.GONE);
//                        break;
//
//                    case ERROR:
//                        retry();
//                        break;
//                }
//
//            }
//
//
////            @Override
////            public int getItemCount() {
////                int itemCount = super.getItemCount();
////                int adCount = (itemCount - 1) / AD_FREQUENCY;
////                return itemCount + adCount;
////            }
//
//        };
//
//
//        recyclerView.setAdapter(mAdapter);
//
//
//    }



//    public void initializedata() {
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance(2)
//                .setPageSize(1)
//                .build();
//
//        DatabasePagingOptions<newsModal> options = new DatabasePagingOptions.Builder<newsModal>()
//                .setLifecycleOwner(this)
//                .setQuery(newRef, config, newsModal.class)
//                .build();
//
//        mAdapter = new FirebaseRecyclerPagingAdapter<newsModal, RecyclerView.ViewHolder>(options) {
//
//            @Override
//            public int getItemViewType(int position) {
////                return (position % (AD_FREQUENCY + 1) == AD_FREQUENCY) ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
//
//                if ((position + 1) % AD_FREQUENCY == 0) {
//                    return VIEW_TYPE_AD;
//                }
//                return VIEW_TYPE_ITEM;
//            }
//
//            @NonNull
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                if (viewType == VIEW_TYPE_ITEM) {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsitem, parent, false);
//                    return new normalHolder(view);
//                } else if (viewType == VIEW_TYPE_AD) {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//                    return new AdViewHolder(view);
//                }
//                else
//                {
//                    return null;
//                }
//            }
//
////            @Override
////            public int getItemCount() {
////                if (mainList.size() > 0) {
////                    return mainList.size() + Math.round(mainList.size() / ITEM_FEED_COUNT);
////                }
////                return mainList.size();
////            }
//
//
//            @Override
//            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull newsModal news) {
//                if (getItemViewType(position) == VIEW_TYPE_ITEM) {
//                    normalHolder normalHolder = (normalHolder) holder;
//                    normalHolder.category.setText(news.getCategory());
//                    Glide.with(getActivity()).load(news.getImage()).into(normalHolder.newsImage);
//                    normalHolder.title.setText(news.getTitle());
//                    normalHolder.timestamp.setText(news.getTime());
//
//                    normalHolder.knowMore.setOnClickListener(view -> {
//                        Intent myIntent = new Intent(view.getContext(), NewsInfo.class);
//                        myIntent.putExtra("title", news.getTitle());
//                        myIntent.putExtra("timestamp", news.getTime());
//                        myIntent.putExtra("category", news.getCategory());
//                        myIntent.putExtra("image", news.getImage());
//                        myIntent.putExtra("news", news.getContent());
//                        myIntent.putExtra("newby", news.getNewsBy());
//                        myIntent.putExtra("link", news.getLink());
//                        view.getContext().startActivity(myIntent);
//                    });
//                }
//
//                else {
//                    AdViewHolder adHolder = (AdViewHolder) holder;
//                    adHolder.hideAdView();
//
//                    AdLoader adLoader = new AdLoader.Builder(getActivity(), "ca-app-pub-1647095499376164/2598134097")
//                            .forNativeAd(nativeAd -> {
//                                populateNativeAdView(nativeAd, adHolder.adView);
//                                adHolder.showAdView();
//                            })
//                            .withAdListener(new AdListener() {
//                                @Override
//                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                                    Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
//                                    adHolder.adView.setVisibility(View.GONE);
//                                    adHolder.placeholderView.setVisibility(View.VISIBLE);
//                                }
//                            })
//                            .withNativeAdOptions(new NativeAdOptions.Builder().build())
//                            .build();
//
//                    adLoader.loadAd(new AdRequest.Builder().build());
//                }
//            }
//
//            @Override
//            protected void onLoadingStateChanged(@NonNull LoadingState state) {
//                switch (state) {
//                    case LOADING_INITIAL:
//                    case LOADING_MORE:
//                        loadingPB.setVisibility(View.VISIBLE);
//                        break;
//
//                    case LOADED:
//                        loadingPB.setVisibility(View.GONE);
//                        break;
//
//                    case FINISHED:
//                        loadingPB.setVisibility(View.GONE);
//                        break;
//
//                    case ERROR:
//                        retry();
//                        break;
//                }
//            }
//        };
//recyclerView.setAdapter(mAdapter);
//    }

    private void bindAdViewHolder(AdViewHolder holder, int position){

        AdLoader adLoader = new AdLoader.Builder(getActivity(), "ca-app-pub-1647095499376164/2598134097")
                .forNativeAd(nativeAd -> {
                    populateNativeAdView(nativeAd, holder.adView);
                    holder.showAdView();
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e("AdMob", "Ad failed to load: " + loadAdError.getMessage());
                        holder.adView.setVisibility(View.GONE);
                        holder.placeholderView.setVisibility(View.VISIBLE);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
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


}

     class normalHolder  extends RecyclerView.ViewHolder {
    public TextView category, title;
    public TextView timestamp;
    ShapeableImageView newsImage;
    RelativeLayout knowMore;


    public normalHolder (@NonNull View itemView) {
        super(itemView);

        category = itemView.findViewById(R.id.category);
//            category = itemView.findViewById(R.id.category);
        title = itemView.findViewById(R.id.title);
        timestamp = itemView.findViewById(R.id.date);
        newsImage = itemView.findViewById(R.id.image);
        knowMore = itemView.findViewById(R.id.knowMore);

    }

}
//    class AdViewHolder extends normalHolder {
//    NativeAdView adView;
//
//    View placeholderView;
//    public AdViewHolder(@NonNull View itemView) {
//        super(itemView);
//        // Initialize ad view
//        placeholderView = itemView.findViewById(R.id.ad_placeholder_view);
//        adView = (NativeAdView) itemView.findViewById(R.id.native_ad_view);
//        // Initialize other ad assets
//        adView.setMediaView(adView.findViewById(R.id.ad_media));
//        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//        adView.setBodyView(adView.findViewById(R.id.ad_body));
//        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
//        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//    }
//    public void hideAdView() {
//        adView.setVisibility(View.GONE);
//    }
//
//    public void showAdView() {
//        adView.setVisibility(View.VISIBLE);
//    }
//}

class AdViewHolder extends RecyclerView.ViewHolder {
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
