package com.askmate.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.askmate.Adapter.QnA_Adapter;
import com.askmate.Adapter.Adapter;
import com.askmate.Adapter.QnA_Adapter;
import com.askmate.Adapter.newQuestionAdapter;
import com.askmate.Modal.Question;
import com.askmate.Modal.newsModal;
import com.askmate.OnItemActionListener;
import com.askmate.QnA_Ans;
import com.askmate.R;
import com.askmate.SearchQuestions;

import com.askmate.roughRecyclerview.CourseModal;
import com.askmate.roughRecyclerview.MainActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.github.drjacky.imagepicker.ImagePicker;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marcorei.infinitefire.InfiniteFireArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_QnA#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_QnA extends Fragment   {
//public class fragment_QnA extends Fragment   {
    private OnItemActionListener itemActionListener;
    RecyclerView recyclerView;
    int count = 0;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    LinearLayoutCompat linearLayoutCompat;
    private boolean shouldReloadData = false;
//    AppCompatButton loadMoreButton;
    boolean imageCheck = false;
    boolean clickCheck = false;
    ShapeableImageView imageshow, image1;
    Uri questionUri;
    private ArrayList<Question> questionList;;
    ProgressDialog progressDialog;
    private newQuestionAdapter adapter;
    AppCompatEditText searhEdittxet;
    private DatabaseReference questionsRef;
    ProgressBar loadingPB;
    NestedScrollView nestedScrollView;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_AD = 1;
    private static final int AD_FREQUENCY = 5; // Ad after every 2 items
    FirebaseRecyclerPagingAdapter<Question, ItemViewHolder> mAdapter;
//    ArrayList<Event> event=new ArrayList<>();
    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
     boolean isLoading = false;
    private boolean hasMoreData = true;

     String lastKey = "";
    private int pageSize = 10;
    boolean likechekcer;
    String totalLikes;



    ArrayList<Question> questionArrayList;
//    ProgressDialog progressDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_QnA() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_QnA.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_QnA newInstance(String param1, String param2) {
        fragment_QnA fragment = new fragment_QnA();
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
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__qn_a, container, false);
linearLayoutCompat = view.findViewById(R.id.linearlayout_ask);
        toolbar = view.findViewById(R.id.htab_toolbar);
        nestedScrollView = view.findViewById(R.id.Nsv);

        progressDialog = new ProgressDialog(getActivity());
         loadingPB = view.findViewById(R.id.idPBLoading);
        searhEdittxet = view.findViewById(R.id.searchEditText);
         recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        recyclerView.setNestedScrollingEnabled(false);
         layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        questionArrayList = new ArrayList<>();



         progressDialog = new ProgressDialog(getActivity());

        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
        questionList = new ArrayList<>();
        adapter = new newQuestionAdapter(getActivity(), questionList, new newQuestionAdapter.OnItemActionListener() {
            @Override
            public void onDeleteItem(Question questionModal, int position) {

                deleteQuestion(questionModal.getQuestionId(), position);
            }
        });


        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference
        questionsRef = FirebaseDatabase.getInstance().getReference().child("askmate").child("questions");
        // Initialize itemActionListener
//        itemActionListener = new OnItemActionListener() {
//            @Override
//            public void onDeleteItem(Question question) {
//                // Handle the delete action
//            }
//        };

//                questionsRef.limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                questionList.clear();
//                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                    Question question = questionSnapshot.getValue(Question.class);
//                    lastKey = questionSnapshot.getKey();
//                    // Set the questionId for each Question object
//                    assert question != null;
//                    question.setQuestionId(questionSnapshot.getKey());
////                    Toast.makeText(getActivity(), , Toast.LENGTH_SHORT).show();
////                    Log.d("key", "onDataChange: " + questionSnapshot.getKey().toString());
//                    questionList.add(0,question);
////                    questionList.add(question);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle error
//            }
//        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            private int currentVisibleItemCount;
//            private int currentScrollState;
//            private int currentFirstVisibleItem;
//            private int totalItemCount;
//            private boolean loading = true; // True if we are still waiting for the previous load to finish
//
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                this.currentScrollState = newState;
//               isScrollCompleted();
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();
//                currentVisibleItemCount = recyclerView.getChildCount();
//                totalItemCount = layoutManager.getItemCount();
//            }
//
//            private void isScrollCompleted() {
//                if (totalItemCount - currentFirstVisibleItem == currentVisibleItemCount
//                        && this.currentScrollState == SCROLL_STATE_IDLE)  {
//                    // End has been reached
////                    loading = false;
//                    // Your logic for loading more data
//                    questionsRef.orderByKey().startAt(lastKey).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                questionList.clear();
//                            for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                                Question question = questionSnapshot.getValue(Question.class);
//                                lastKey = questionSnapshot.getKey();
//                                // Set the questionId for each Question object
//                                assert question != null;
//                                question.setQuestionId(questionSnapshot.getKey());
////                    Toast.makeText(getActivity(), , Toast.LENGTH_SHORT).show();
////                    Log.d("key", "onDataChange: " + questionSnapshot.getKey().toString());
//                                questionList.add(0,question);
////                    questionList.add(question);
//                            }
//                            adapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            // Handle possible errors
////                            loading = true;
//                        }
//                    });
//                }
//            }
//        });
//        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<Question> questionList = new ArrayList<>();
//                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                    Question question = questionSnapshot.getValue(Question.class);
//                    if (question != null) {
//                        question.setQuestionId(questionSnapshot.getKey());
//                        questionList.add(question);
//                    }
//                }
//
//                // Set up the adapter with the modified question list
//                setUpAdapter(questionList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle error
//            }
//        });

//
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance(2)
//                .setPageSize(1)
//                .build();
//
//        DatabasePagingOptions<Question> options = new DatabasePagingOptions.Builder<Question>()
//                .setLifecycleOwner(this)
//                .setQuery(questionsRef, config, Question.class)
//                .build();

//        mAdapter = new FirebaseRecyclerPagingAdapter<Question, ItemViewHolder>(options) {
//            @Override
//            public int getItemViewType(int position) {
//                return (position % (AD_FREQUENCY + 1) == AD_FREQUENCY) ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
//            }
//
//
//
//
//            @NonNull
//            @Override
//            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                if (viewType == VIEW_TYPE_ITEM) {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummy_qna, parent, false);
//                    return new ItemViewHolder(view);
//                } else {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//                    return new ItemViewHolder.AdViewHolder(view);
//                }
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Question question) {
//                DatabaseReference reference = getRef(position);
//        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
//            String questionKey = reference.getKey();
//            question.setQuestionId(questionKey);
//            bindItemViewHolder(holder, position, question);
//
//            holder.bind(question, itemActionListener);
//
//            fetchUserProfile(question.getUid(), holder);
//            fetchLikesCount(question.getQuestionId(), holder);
//            fetchanswer(question.getQuestionId(), holder);
//
//
//            holder.commentLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent myIntent = new Intent(view.getContext(), QnA_Ans.class);
//                    myIntent.putExtra("check", "QnA");
//                    myIntent.putExtra("name",  holder.name.getText().toString());
//                    myIntent.putExtra("question", question.getQuestion());
//                    myIntent.putExtra("category", question.getCategory());
//                    myIntent.putExtra("time",  holder.date.getText().toString());
//                    myIntent.putExtra("image", question.getImage());
//                    myIntent.putExtra("uid", question.getUid());
//                    myIntent.putExtra("questionId", question.getQuestionId());
//
//                    view.getContext().startActivity(myIntent);
//                }
//            });
//
//        }
////
//        else {
//            bindAdViewHolder((ItemViewHolder.AdViewHolder) holder, position);
//        }
//
//
//            }
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
//        };
//initializedata(); <----------- load this
//        recyclerView.setAdapter(mAdapter);

//        adapter = new QnA_Adapter(this , questionList,  getActivity());
//        mAdapter = new FirebaseRecyclerPagingAdapter<Question, QnA_Adapter.ItemViewHolder>() {

//        }



        // Load questions from Firebase
//        loadQuestions();
//        loadQuestions(currentPage, PAGE_SIZE);
//        loadQuestions();



//        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
//            @Override
//            public void onLoadMore(int current_page) {
//                loadMoreData();
//            }
//        });
//        loadQuestions();

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
////                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == questionList.size() - 1) {
//                    loadQuestions();
//                    isLoading = true;
//                }
//            }
//        });


//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
//                    isLoading = true;
//                    Toast.makeText(getActivity(), "Loading more items...", Toast.LENGTH_SHORT).show();
//                    loadQuestions();
//                }
//            }
//        });


//        loadMoreButton = view.findViewById(R.id.loadMoreButton);

//        loadMoreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isLoading) {
//                    Log.d("lastkey", "loadmorebutton: " + lastKey);
//                    isLoading = true;
//                    loadQuestions();
//                }
//            }
//        });


//        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
//                    if (!isLoading && hasMoreData) {
//                        currentPage++;
//                        loadingPB.setVisibility(View.VISIBLE);
////                        loadQuestions(currentPage, PAGE_SIZE);
//                        loadQuestions();
//                        Toast.makeText(getActivity(), "Running again", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                Toast.makeText(getActivity(), "Running again", Toast.LENGTH_SHORT).show();
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (layoutManager != null && !isLoading && hasMoreData) {
//                    int visibleItemCount = layoutManager.getChildCount();
//                    int totalItemCount = layoutManager.getItemCount();
//                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
//                        // End has been reached, load more data
//                        loadQuestions(currentPage, PAGE_SIZE);
//
//                    }
//                }
//            }
//        });
//        AdmobNativeAdAdapter admobNativeAdAdapter=AdmobNativeAdAdapter.Builder
//                .with(
////                        "ca-app-pub-3940256099942544/2247696110",//Create a native ad id from admob console
//                        "ca-app-pub-3940256099942544/2247696110",//Create a native ad id from admob console
//                        adapter,//The adapter you would normally set to your recyClerView
//                        "medium"//Set it with "small","medium" or "custom"
//                )
//                .adItemIterval(2)//native ad repeating interval in the recyclerview
//                .build();
//        recyclerView.setAdapter(admobNativeAdAdapter);//set your RecyclerView adapter with the admobNativeAdAdapter

//        Query query =  FirebaseDatabase.getInstance().getReference().child("askmate").child("questions").orderByChild("timestamp");
//        final InfiniteFireArray<Question> array = new InfiniteFireArray<>(
//                Question.class,// Model Class
//                query,//Database Ref
//                30,// Initial Size
//                10, //how many items to Load every time
//                false,//limitToFirst //True means the old appears on top
//                true
//        );
//
//        final QnA_Adapter adapter2 = new QnA_Adapter(array, getActivity());
//        recyclerView.setAdapter(adapter2);

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

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy < 0) {
//                    return;
//                }
//                if (layoutManager.findLastVisibleItemPosition() < array.getCount() - 5) {
//                    return;
//                }
//
//                Log.d("data", "onScrolled: " + "Loadin data");
//                array.more();
//            }
//        });


//        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            private boolean isLoading = false;
//            private boolean hasMoreData = true; // Flag to check if there is more data to load
//            private Handler mainHandler = new Handler(Looper.getMainLooper());
//
//            @Override
//            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
//                    if (isLoading || !hasMoreData) {
//                        return;
//                    }
//
//                    isLoading = true;
//                    loadingPB.setVisibility(View.VISIBLE);
//
//                    // Loading more data in the background thread
//                    new Thread(() -> {
//                        Log.d("data", "onScrollChange: " + "Loading data");
//                        // Simulate a network delay (if needed)
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
////                        if (array.hasMore()) {
//                            array.more();
////                        } else {
////                            hasMoreData = false; // No more data to load
////                        }
//
//                        mainHandler.post(() -> {
//                            isLoading = false;
//                            loadingPB.setVisibility(View.GONE); // Hide progress bar after data is loaded
//                            if (!hasMoreData) {
//                                Toast.makeText(getActivity(), "No more data", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }).start();
//                }
//            }
//        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            private boolean isLoading = false;
//            private Handler handler = new Handler(Looper.getMainLooper());
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy < 0 || isLoading) {
//                    return;
//                }
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (layoutManager != null && layoutManager.findLastVisibleItemPosition() >= array.getCount() - 10) {
//                    isLoading = true;
////                    handler.post(() -> {
//                        Log.d("data", "onScrolled: " + "Loading data");
//                        array.more();
//                        isLoading = false;
////                    });
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    isLoading = false;
//                }
//            }
//        });

//loadingPB.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//        String id = questionArrayList.get(questionArrayList.size()-1).getQuestionId();
//        Log.d("key", "This is key: " + lastKey);
////        loadmoredata(lastKey);
//    }
//});

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
//                    Log.d("tag","scrolled to end");
//
//                }
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (layoutManager != null) {
//                    int visibleItemCount = layoutManager.getChildCount();
//                    int totalItemCount = layoutManager.getItemCount();
//                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//                    // Check if the user has scrolled to the end of the list
//                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
//                        isLoading = true;
//                        if (lastKey != null) {
//                            Log.d("key", "This is key: " + lastKey);
//                            Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
////                            loadMoreData(lastKey);
//                        }
//                    }
//                    else
//                    {
//                        Toast.makeText(getActivity(), "Not Loading", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            }
//        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("tag","scrolled to end " + lastKey );
//                                                Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();
                    isLoading = true; // Set the loading flag to true
                                            loadmoredata(lastKey);

                }
            }
        });


        loadinitialdata();



        linearLayoutCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        searhEdittxet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchQuestions.class);
                startActivity(intent);
            }
        });
//        recyclerView.setAdapter(adapter);
        return  view;
    }

    private void deleteQuestion(String questionId, int position) {
        Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
        Log.d("ID", "deleteQuestion: " + questionId);
//        loadinitialdata();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Prevent user from dismissing
progressDialog.show();
        FirebaseDatabase.getInstance().getReference().child("askmate").child("questions").child(questionId)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("questions")
                                .child(questionId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        FirebaseDatabase.getInstance().getReference()
                                                        .child("user").child(FirebaseAuth.getInstance().getUid())
                                                        .child("questions")
                                                .child(questionId)
                                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), "Your question successfully deleted", Toast.LENGTH_SHORT).show();
//                                        ((fragment_QnA) fragment).reloadData();  // Refresh the adapter in the Fragment
                                                        adapter.removeItem(position);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });



//                                            if (fragment instanceof fragment_QnA) {
//                                                ((fragment_QnA) fragment).reloadData();  // Refresh the adapter in the Fragment
//                                            }
//                                        loadQuestions(currentPage, PAGE_SIZE);
//                                        loadQuestions();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();

                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(getActivity(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    public void loadinitialdata()
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("askmate").child("questions");
        Query initialQuery = databaseReference.orderByKey().limitToLast(30); // Load 10 latest items

        initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren())
                {

                    String questionkey = snapshot1.getKey();
                    Question question = snapshot1.getValue(Question.class);
                    question.setQuestionId(questionkey);
                    questionArrayList.add(question);

                }
                Collections.reverse(questionArrayList);
                adapter.addData(questionArrayList);

                if (!questionArrayList.isEmpty()) {
                    lastKey = questionArrayList.get(questionArrayList.size() - 1).getQuestionId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void reloadData() {
        // Clear the existing data
        questionArrayList.clear();
        adapter.clearData();
        // Reset lastKey
        lastKey = null;
        // Load initial data again
        loadinitialdata();
    }

    public void loadmoredata(String id) {
        Log.d("id", "Size: " + questionArrayList.size());

       String idd = questionArrayList.get(questionArrayList.size()-1).getQuestionId();
        Log.d("id", "IDDDD------>: " + id);
        ArrayList<Question> newQuestions = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("askmate").child("questions");

        Query initialQuery = databaseReference.orderByKey().endBefore(id).limitToLast(31); // Load 10 latest items
        initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String questionkey = snapshot1.getKey();
                    Question question = snapshot1.getValue(Question.class);
                    assert question != null;
                    question.setQuestionId(questionkey);

                    if (!questionkey.equals(idd)) {
                        newQuestions.add(question);
                    }

                }

                if (newQuestions.isEmpty()) {
//                    Toast.makeText(getActivity(), "Nothing left", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                }
                else {
                    Collections.reverse(newQuestions);
                    adapter.addData(newQuestions);
                    // Update lastKey for the next query
                    if (!newQuestions.isEmpty()) {
                        lastKey = newQuestions.get(newQuestions.size() - 1).getQuestionId();
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





    public void initializedata() {
//        Query query = questionsRef.orderByChild("timestamp");
        Query query = questionsRef.orderByChild("timestamp");
//        Query questionsQuery = questionsRef
//                .orderByChild("timestamp");

//        Paged
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(1)
                .build();
////        Query query = questionsRef.l


        DatabasePagingOptions<Question> options = new DatabasePagingOptions.Builder<Question>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Question.class)
                .build();
//
//        FirebaseRecyclerOptions<Question> options = new FirebaseRecyclerOptions.Builder<Question>()
//                .setLifecycleOwner(this)
//                .setQuery(query, Question.class)
//                .build();

//        DatabasePagingOptions
//        FirebasePagingOptions<Question> options = new FirebasePagingOptions.Builder<Question>()
//                .setQuery(query, Query.class)
//                .setLifecycleOwner(this) // Assuming you're in an Activity or Fragment
//                .build();
////FirebasePaging

        mAdapter = new FirebaseRecyclerPagingAdapter<Question, ItemViewHolder>(options) {
            @Override
            public int getItemViewType(int position) {
                return (position % (AD_FREQUENCY + 1) == AD_FREQUENCY) ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
            }

            public void refresh() {
                // Reinitialize the adapter with the same options to force a refresh
                notifyDataSetChanged();  // Trigger a UI update
            }


            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                if (viewType == VIEW_TYPE_ITEM) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummy_qna, parent, false);
                    return new ItemViewHolder(view, fragment_QnA.this);
//                } else {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//                    return new ItemViewHolder.AdViewHolder(view , fragment_QnA.this);
//                }
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Question question) {
                DatabaseReference reference = getRef(position);
//                if (getItemViewType(position) == VIEW_TYPE_ITEM) {
                    String questionKey = reference.getKey();

//                Log.d("KEY", "onBindViewHolder: " + questionKey);
                    question.setQuestionId(questionKey);
                    bindItemViewHolder(holder, position, question);

                    holder.bind(question, itemActionListener);

                    fetchUserProfile(question.getUid(), holder);
                    fetchLikesCount(question.getQuestionId(), holder);
                    fetchanswer(question.getQuestionId(), holder);


                    holder.commentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(view.getContext(), QnA_Ans.class);
                            myIntent.putExtra("check", "QnA");
                            myIntent.putExtra("name", holder.name.getText().toString());
                            myIntent.putExtra("question", question.getQuestion());
                            myIntent.putExtra("category", question.getCategory());
                            myIntent.putExtra("time", holder.date.getText().toString());
                            myIntent.putExtra("image", question.getImage());
                            myIntent.putExtra("uid", question.getUid());
                            myIntent.putExtra("questionId", question.getQuestionId());

                            view.getContext().startActivity(myIntent);
                        }
                    });

//                }
//
//                else {
//                    bindAdViewHolder((ItemViewHolder.AdViewHolder) holder, position);
//                }


            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {

                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        // Do your loading animation
                        loadingPB.setVisibility(View.VISIBLE);

//                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        loadingPB.setVisibility(View.GONE);

                        break;

                    case FINISHED:
                        //Reached end of Data set
                        loadingPB.setVisibility(View.GONE);
                        break;

                    case ERROR:
                        retry();
                        break;
                }

            }
        };

        recyclerView.setAdapter(mAdapter);


    }




    private void bindAdViewHolder(ItemViewHolder.AdViewHolder holder, int position){

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



    private void fetchanswer(String uid, ItemViewHolder holder) {
        DatabaseReference commentCount = FirebaseDatabase.getInstance().getReference().child("askmate").child("questions")
                .child(uid).child("answers");

        commentCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                        Log.d("key", "Okkk: " + datasnapshot.getKey());

                        if (datasnapshot.exists()) {
                            long count = snapshot.getChildrenCount();
                            String commentCount = String.valueOf(count);
                            if (count > 0) {
                                holder.comments.setText(commentCount);
                            } else {
                                holder.comments.setText("Be the first to answer!");

                            }
                        }
//                else
//                {
//
//                }

                    }
                } else {
                   holder.comments.setText("Be the first to answer!");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    private void fetchLikesCount(String questionId, ItemViewHolder holder) {

        DatabaseReference likesCount = FirebaseDatabase.getInstance().getReference().child("askmate").child("likesQuestions").child(questionId);

        likesCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long likes = snapshot.getChildrenCount();
                    if (likes > 1) {
                        totalLikes = likes + " likes";
                    } else if (likes == 1) {
                        totalLikes = String.valueOf(likes + " like");

                    }
                    holder.like.setText(totalLikes);
                } else {
                    String zeroCount = "0 Like";
                    holder.like.setText(zeroCount);

                }
                if (snapshot.hasChild(FirebaseAuth.getInstance().getUid())) {
                    holder.likeDislike.setImageResource(R.drawable.like);
                } else {
                   holder.likeDislike.setImageResource(R.drawable.dislike);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void fetchUserProfile(String uid, ItemViewHolder holder) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("image").getValue(String.class);

                    // Set username
                    holder.name.setText(username);

                    // Load profile image using Glide or Picasso library
                    if (isAdded()) {
                        Glide.with(getActivity()).load(profileImageUrl).placeholder(R.drawable.plus).into(holder.profile);
                    }
                } else {
                    // User profile not found
                    // You can set a default username or profile image, or handle the case as needed
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


//    private void bindItemViewHolder(ItemViewHolder holder, int position, Question question) {
//        holder.bind(question, itemActionListener );
//    }


//    private void loadMoreData(){
//        currentPage++;
//        loadQuestions();
//    }

    // previous method
//    private void loadQuestions() {
//
////        isLoading = true;
//        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                questionList.clear();
//                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                    Question question = questionSnapshot.getValue(Question.class);
//                    // Set the questionId for each Question object
//                    assert question != null;
//                    question.setQuestionId(questionSnapshot.getKey());
////                    Toast.makeText(getActivity(), , Toast.LENGTH_SHORT).show();
////                    Log.d("key", "onDataChange: " + questionSnapshot.getKey().toString());
//                    questionList.add(0,question);
////                    questionList.add(question);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle error
//            }
//        });
//    }


//    private void loadQuestions(int page, int pageSize) {
//    private void loadQuestions() {
//        if (isLoading) return;
//
//        isLoading = true;
////        Log.d("Pagination", "Loading page " + page);
//
//        Query query;
//
//            query = questionsRef.orderByKey().limitToFirst(PAGE_SIZE)
//                    .startAt(currentPage * PAGE_SIZE);
//
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<Question> newQuestions = new ArrayList<>();
//
//                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                    Question question = questionSnapshot.getValue(Question.class);
//                    if (question != null) {
//                        question.setQuestionId(questionSnapshot.getKey());
//                        newQuestions.add(question);
//                    }
//                }
//
//                questionList.addAll(newQuestions);
//                adapter.notifyDataSetChanged();
//                isLoading = false;
//                Log.d("Pagination", "Loaded " + newQuestions.size() + " items");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                isLoading = false;
//                Log.e("Pagination", "Error loading data: " + databaseError.getMessage());
//            }
//        });
//    }

// part 6
//    private void loadQuestions() {
//        if (isLoading) return; // Prevent multiple simultaneous loads
//
//        isLoading = true;
//        List<Question> newQuestions = new ArrayList<>();
//
//        Query query;
//        if (lastKey.isEmpty()) {
//            // Initial load
//            query = questionsRef.orderByKey().limitToFirst(pageSize); // Fetch one extra item to detect if there's more data
//        } else {
//            // Subsequent loads
//            query = questionsRef.orderByKey().startAfter(lastKey).limitToFirst(pageSize);
//        }
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    boolean hasMoreData = false;
//
//                    for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                        Question question = questionSnapshot.getValue(Question.class);
//                        if (question != null) {
//                            question.setQuestionId(questionSnapshot.getKey());
//                            newQuestions.add(question);
//                        }
//                    }
//
//                    // Check if we need to trim the extra item added for pagination check
//                    if (newQuestions.size() > pageSize) {
//                        newQuestions.remove(newQuestions.size() - 1); // Remove the extra item
//                        hasMoreData = true; // There are more items to load
//                    }
//
//                    if (!newQuestions.isEmpty()) {
//                        lastKey = newQuestions.get(newQuestions.size() - 1).getQuestionId();
//                        Log.d("lastkey", "onDataChange: " + lastKey);
//                        questionList.addAll(newQuestions);
//                        Log.d("newQuestions", "onDataChange: " + Arrays.toString(newQuestions.toArray()));
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    // Enable or disable the load more button based on availability of more data
////                    loadMoreButton.setEnabled(hasMoreData);
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                isLoading = false;
//            }
//        });
//    }


    // again working part 5
//    private void loadQuestions() {
//        isLoading = true;
//        List<Question> newQuestions = new ArrayList<>();
//
//        Query query;
//        if (lastKey.isEmpty()) {
//            Toast.makeText(getActivity(), "Loading one", Toast.LENGTH_SHORT).show();
//            query = questionsRef.orderByKey().limitToFirst(pageSize);
//        } else {
//            Toast.makeText(getActivity(), "Loading two", Toast.LENGTH_SHORT).show();
//
//            query = questionsRef.orderByKey().startAfter(lastKey).limitToFirst(pageSize);
//        }
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                        Question question = questionSnapshot.getValue(Question.class);
//                        if (question != null) {
//                            question.setQuestionId(questionSnapshot.getKey());
//                            newQuestions.add(question);
//                        }
//                    }
//
//                    if (!newQuestions.isEmpty()) {
//                        lastKey = newQuestions.get(newQuestions.size() - 1).getQuestionId();
//                        Log.d("lastkey", "onDataChange: " + lastKey);
//                        questionList.addAll(newQuestions);
//                        for(int i=0;i<newQuestions.size();i++){
//                            Log.d("lastkey", "onDataChange: " + newQuestions.get(i));
//
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//                isLoading = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                isLoading = false;
//            }
//        });
//    }
    // loading quesions
//    private void loadQuestions() {
//        isLoading = true;
//
//        Query query;
//        if (lastKey.isEmpty()) {
//            // Load the first batch of items
//            query = questionsRef.orderByKey().limitToFirst(pageSize);
//        } else {
//            // Load the next batch of items starting after the last key
//            query = questionsRef.orderByKey().startAfter(lastKey).limitToFirst(pageSize + 1);
//        }
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    isLoading = false;
//
//                    List<Question> newQuestions = new ArrayList<>();
//                    boolean isFirstBatch = lastKey.isEmpty();
//
//                    for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                        Question question = questionSnapshot.getValue(Question.class);
//                        assert question != null;
//                        question.setQuestionId(questionSnapshot.getKey());
//
//                        // Check if the item is not the first item in the next batch
//                        if (!isFirstBatch && questionSnapshot.getKey().equals(lastKey)) {
//                            continue;
//                        }
//
//                        newQuestions.add(question);
//                        lastKey = questionSnapshot.getKey(); // Update the last key
//                    }
//
//                    if (!isFirstBatch && !newQuestions.isEmpty()) {
//                        // Remove the first item if it's the one used to fetch the next batch
//                        newQuestions.remove(0);
//                    }
//
//                    questionList.addAll(newQuestions);
//                    adapter.notifyDataSetChanged();
//                }
//            }
////
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle error
//                isLoading = false;
//            }
//        });
//    }

    private void bindItemViewHolder(ItemViewHolder holder, int position, Question question)
    {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        // Bind data for items
        itemViewHolder.bind(question, itemActionListener);
        holder.date.setText(getTimeAgo(question.getTimestamp()));
        holder.questionn.setText(question.getQuestion());
        holder.category.setText(question.getCategory());
//        holder.bind(question, itemActionListener);


        Log.d("lastkey", "bindItemViewHolder: " + question.getQuestionId() );
        // Check if image URL is available and load image using Glide or Picasso library
        if (question.getImage() != null && !question.getImage().isEmpty()) {
          holder.questionImage.setVisibility(View.VISIBLE);

            // Load image using Glide or Picasso library
            Glide.with(getActivity()).load(question.getImage()).into(holder.questionImage);
        } else {
        holder.questionImage.setVisibility(View.GONE);
        }

//        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent myIntent = new Intent(view.getContext(), QnA_Ans.class);
//                myIntent.putExtra("check", "QnA");
//                myIntent.putExtra("name",   holder.name.getText().toString());
//                myIntent.putExtra("question", modal.get(position).getQuestion());
//                myIntent.putExtra("category", modal.get(position).getCategory());
//                myIntent.putExtra("time",  ((QnA_Adapter.ItemViewHolder) holder).date.getText().toString());
//                myIntent.putExtra("image", modal.get(position).getImage());
//                myIntent.putExtra("uid", modal.get(position).getUid());
//                myIntent.putExtra("questionId", modal.get(position).getQuestionId());
//
//                view.getContext().startActivity(myIntent);
//            }
//        });

//        fetchanswer(modal.get(position).getQuestionId(), holder);
//        fetchUserProfile(modal.get(position).getUid(), holder);
//        fetchLikesCount(modal.get(position).getQuestionId(), holder);
        String userId = FirebaseAuth.getInstance().getUid();


//        String QuestionId = modal.get(position).getQuestionId();
        String QuestionId = question.getQuestionId();
        DatabaseReference likesref = FirebaseDatabase.getInstance().getReference().child("askmate").child("likesQuestions");
      holder.likeDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                likechekcer = true;

                likesref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (likechekcer == true) {
                            if (snapshot.child(QuestionId).hasChild(userId)) {
                                likesref.child(QuestionId).child(userId).removeValue();
                                likechekcer = false;
                            } else {
                                likesref.child(QuestionId).child(userId).setValue(true);
                                likechekcer = false;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//
//
            }
        });
        }

//    }


    private String getLastItemKey() {
        if (questionList.isEmpty()) {
            return ""; // Start at the beginning
        } else {
            Log.e("Pagination", "Last Key " + questionList.get(questionList.size() - 1).getQuestionId() );

            return questionList.get(questionList.size() - 1).getQuestionId();
        }
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.ask_menu, menu);
//        return true;
//    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.ask_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void showDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_qna_asked);
        AppCompatButton button = dialog.findViewById(R.id.Button);
        TextInputEditText editText = dialog.findViewById(R.id.question_asked);
        TextView addImage = dialog.findViewById(R.id.addImage);

        imageshow = dialog.findViewById(R.id.imageshow);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);
        FrameLayout fl = dialog.findViewById(R.id.frameHide);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                // Toggle visibility of imageshow ConstraintLayout
                                if (fl.getVisibility() == View.VISIBLE) {
                                    fl.setVisibility(View.GONE);
                                    imageCheck = false;
                                    clickCheck = false;
                                } else {
                fl.setVisibility(View.VISIBLE);
                imageCheck = true;
                clickCheck = false;
                                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the imageshow ConstraintLayout
                fl.setVisibility(View.GONE);
                imageCheck = false;
                clickCheck = false;
            }
        });

        imageshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launcher1.launch(ImagePicker.Companion.with(getActivity())
                        .crop()
                        .cropFreeStyle()
                        .galleryOnly()
                        .setOutputFormat(Bitmap.CompressFormat.JPEG)
                        .createIntent()
                );
            }
        });


        String[] languages = getResources().getStringArray(R.array.subjects);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, languages);

        MaterialAutoCompleteTextView autocompleteTV = dialog.findViewById(R.id.autoCompleteTextView);

        autocompleteTV.setAdapter(arrayAdapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Uploading question...");
                progressDialog.show();

                String question = editText.getText().toString();
                String category = autocompleteTV.getText().toString();
                String timestamp = String.valueOf(System.currentTimeMillis());
                Uri uri = questionUri;

                if (question.isEmpty()) {
                    Toast.makeText(getActivity(), "Please ask question", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    return;
                }
                if (category.isEmpty() || category.equals("Choose category")) {
                    Toast.makeText(getActivity(), "Please choose a category", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    return;
                }

                if (uri != null) {


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("askmate").child("questions");
                    String pushKey = reference.push().getKey();

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imageRef = storageRef.child(FirebaseAuth.getInstance().getUid()).child("askmate").child(pushKey);

                    imageRef.putFile(questionUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    HashMap<String, Object> withImage = new HashMap<>();
                                    withImage.put("question", question);
                                    withImage.put("timestamp", timestamp);
                                    withImage.put("category", category);
                                    withImage.put("likes", null);
                                    withImage.put("uid", FirebaseAuth.getInstance().getUid());
                                    withImage.put("image", uri.toString());

                                    reference.child(pushKey).updateChildren(withImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
//                                       reference.child(questionKey).child("likes").setValue(null);
                                            HashMap<String, Object> myQuestion = new HashMap<>();
                                            myQuestion.put(pushKey, category);
                                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("questions")
                                                    .updateChildren(myQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            progressDialog.dismiss();
                                                            dialog.dismiss();
                                                            Toast.makeText(getActivity(), pushKey.toString(), Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getActivity(), QnA_Ans.class);
                                                            intent.putExtra("myQuestionId",pushKey);
//                                            intent.putExtra("myAnswerId", answerList.get(position).getAnswerId());
                                                            intent.putExtra("uid", FirebaseAuth.getInstance().getUid());
                                                            intent.putExtra("check", "questionCreate");
                                                             shouldReloadData = true;
                                                            startActivity(intent);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                        }
                                                    });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();

                                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    });


                    Log.d("Question", "onClick: " + question.toString() + " " + category + " " + timestamp + " " + String.valueOf(uri));
                } else {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("askmate").child("questions");
                    String pushKey = reference.push().getKey();
                    HashMap<String, Object> withoutImage = new HashMap<>();
                    withoutImage.put("question", question);
                    withoutImage.put("timestamp", timestamp);
                    withoutImage.put("category", category);
                    withoutImage.put("uid", FirebaseAuth.getInstance().getUid());
                    withoutImage.put("likes", null);

                    reference.child(pushKey).updateChildren(withoutImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            HashMap<String, Object> myQuestion = new HashMap<>();
                            myQuestion.put(pushKey, category);
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("questions")

                                    .updateChildren(myQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            dialog.dismiss();
//                                            Toast.makeText(getActivity(), pushKey.toString(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), QnA_Ans.class);
                                            intent.putExtra("myQuestionId",pushKey);
//                                            intent.putExtra("myAnswerId", answerList.get(position).getAnswerId());
                                            intent.putExtra("uid", FirebaseAuth.getInstance().getUid());
                                            intent.putExtra("check", "questionCreate");
                                            shouldReloadData = true;
                                            startActivity(intent);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

                    Log.d("Question", "onClick: " + question.toString() + " " + category + " " + timestamp + " " + "Without Image");

                }


//                Intent intent = new Intent(getActivity(), SplashScreen.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                if (getActivity() != null) {
//                    getActivity().finish();
//                }
            }
        });



//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressDialog.setCancelable(false);
//                progressDialog.setMessage("Uploading question...");
//                progressDialog.show();
//
//                String question = editText.getText().toString();
//                String category = autocompleteTV.getText().toString();
//                String timestamp = String.valueOf(System.currentTimeMillis());
//                Uri uri = questionUri;
//
//                if (question.isEmpty()) {
//                    Toast.makeText(getActivity(), "Please ask a question", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                    return;
//                }
//                if (category.isEmpty() || category.equals("Choose category")) {
//                    Toast.makeText(getActivity(), "Please choose a category", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                    return;
//                }
//
//                // Reference to the counter node
//                DatabaseReference counterRef = FirebaseDatabase.getInstance().getReference().child("sequentialCounter");
//
//                counterRef.runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData mutableData) {
//                        Integer currentCounter = mutableData.getValue(Integer.class);
//                        if (currentCounter == null) {
//                            currentCounter = 0;
//                        }
//                        mutableData.setValue(currentCounter + 1);
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
//                        if (databaseError != null) {
//                            Toast.makeText(getActivity(), "Counter increment failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();
//                            return;
//                        }
//
//                        Integer newCounterValue = dataSnapshot.getValue(Integer.class);
//                        String pushKey = String.valueOf(newCounterValue);
//
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("askmate").child("questions");
//
//                        if (uri != null) {
//                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//                            StorageReference imageRef = storageRef.child(FirebaseAuth.getInstance().getUid()).child("askmate").child(pushKey);
//
//                            imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri downloadUri) {
//                                            HashMap<String, Object> withImage = new HashMap<>();
//                                            withImage.put("question", question);
//                                            withImage.put("timestamp", timestamp);
//                                            withImage.put("category", category);
//                                            withImage.put("likes", null);
//                                            withImage.put("uid", FirebaseAuth.getInstance().getUid());
//                                            withImage.put("image", downloadUri.toString());
//
//                                            reference.child(pushKey).updateChildren(withImage).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    updateUserQuestions(pushKey, category);
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(getActivity(), "Failed to upload question", Toast.LENGTH_SHORT).show();
//                                                    progressDialog.dismiss();
//                                                }
//                                            });
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(getActivity(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
//                                            progressDialog.dismiss();
//                                        }
//                                    });
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
//                                    progressDialog.dismiss();
//                                }
//                            });
//                        } else {
//                            HashMap<String, Object> withoutImage = new HashMap<>();
//                            withoutImage.put("question", question);
//                            withoutImage.put("timestamp", timestamp);
//                            withoutImage.put("category", category);
//                            withoutImage.put("uid", FirebaseAuth.getInstance().getUid());
//                            withoutImage.put("likes", null);
//
//                            reference.child(pushKey).updateChildren(withoutImage).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    updateUserQuestions(pushKey, category);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(getActivity(), "Failed to upload question", Toast.LENGTH_SHORT).show();
//                                    progressDialog.dismiss();
//                                }
//                            });
//                        }
//                    }
//                });
//            }
//
//            private void updateUserQuestions(String pushKey, String category) {
//                HashMap<String, Object> myQuestion = new HashMap<>();
//                myQuestion.put(pushKey, category);
//                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("questions")
//                        .updateChildren(myQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                progressDialog.dismiss();
//                                dialog.dismiss();
//                                Intent intent = new Intent(getActivity(), QnA_Ans.class);
//                                intent.putExtra("myQuestionId", pushKey);
//                                intent.putExtra("uid", FirebaseAuth.getInstance().getUid());
//                                intent.putExtra("check", "questionCreate");
//                                shouldReloadData = true;
//                                startActivity(intent);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getActivity(), "Failed to update user questions", Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
//                            }
//                        });
//            }
//        });


//
//        LinearLayoutCompat postcreate = dialog.findViewById(R.id.post_linear_layout);
//        LinearLayoutCompat AskCreate = dialog.findViewById(R.id.Ask_linear_layout);




        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    ActivityResultLauncher<Intent> launcher1 =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    //                    uri2  = uri;

                    if (uri != null && !uri.toString().isEmpty()) {
                        //                        imageUriCache.add(uri);
                        imageshow.setImageURI(uri);
                        imageshow.setTag(R.id.source, "gallery");
                        questionUri = uri;
                        imageCheck = true;
                        clickCheck = true;

                    } else {
                        // Handle the case where URI is null or empty
                        Toast.makeText(getActivity(), "Failed to get image. Please try again.", Toast.LENGTH_SHORT).show();
                        clickCheck = false;

                    }
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    Toast.makeText(getActivity(), "Error while picking image. Please try again.", Toast.LENGTH_SHORT).show();
                    clickCheck = false;

                }
            });

    @Override
    public void onResume() {
        super.onResume();
        if(shouldReloadData) {
//            loadQuestions(currentPage, PAGE_SIZE);
//            loadQuestions();
//            initializedata();
//            loadinitialdata();
            reloadData();
            shouldReloadData = false;
        }

        // Refresh data when the fragment is visible again
//        refreshData();
        // Reload current fragment
//        Fragment frg = null;
//        frg = getActivity().getSupportFragmentManager().findFragmentByTag("Your_Fragment_TAG");
//        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.detach(frg);
//        ft.attach(frg);
//        ft.commit();

    }

//    @Override
//    public void onDeleteItem(Question questionModal) {
//
//        Log.d("Reload", "onDeleteItem: " + questionModal.getQuestionId());
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Deleting your question...");
//        progressDialog.show();
//
//        Log.d("ID", "onDeleteItem: " + questionModal.getQuestionId());
////        Toast.makeText(getActivity(), q, Toast.LENGTH_SHORT).show();
////        FirebaseDatabase.getInstance().getReference().child("askmate").child("questions").child(questionModal.getQuestionId())
////                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
////                    @Override
////                    public void onSuccess(Void unused) {
////                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("questions")
////                                        .child(questionModal.getQuestionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
////                                    @Override
////                                    public void onSuccess(Void unused) {
////                                        progressDialog.dismiss();
////                                        Toast.makeText(getActivity(), "Your question successfully deleted", Toast.LENGTH_SHORT).show();
////
//////                                        loadQuestions(currentPage, PAGE_SIZE);
//////                                        loadQuestions();
////                                    }
////                                }).addOnFailureListener(new OnFailureListener() {
////                                    @Override
////                                    public void onFailure(@NonNull Exception e) {
////                                        progressDialog.dismiss();
////                                        Toast.makeText(getActivity(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
////
////                                    }
////                                });
////
////
////                    }
////                }).addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception e) {
////                        progressDialog.dismiss();
////
////                        Toast.makeText(getActivity(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
////                    }
////                });
//    }
    public String getTimeAgo(String timestamp) {
        long timeInMillis = Long.parseLong(timestamp);
        long now = System.currentTimeMillis();
        long diff = now - timeInMillis;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (seconds < 60) {
            return "just now";
        } else if (minutes < 60) {
            return minutes + " min ago";
        } else if (hours < 24) {
            return hours + " hour ago";
        } else if (days == 1) {
            return "yesterday";
        } else if (days < 7) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
            Date date = new Date(timeInMillis);
            return sdf.format(date);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
            Date date = new Date(timeInMillis);
            return sdf.format(date);
        }
    }
//    @Override
//    public void onDataChanged() {
//        Toast.makeText(getActivity(), "Restarting", Toast.LENGTH_SHORT).show();
//    }

//    @Override
//    public void onDeleteItem(Question questionModal) {
//                Toast.makeText(getActivity(), "Restarting", Toast.LENGTH_SHORT).show();
//        Log.d("lastkey", "onDeleteItem: " + questionModal.getQuestion());
//
//    }
//    @Override
//    public void onDeleteItem(Question questionModal) {
//        mAdapter.refresh();
//    }
}

   class ItemViewHolder extends RecyclerView.ViewHolder {
    private OnDataChangedListener onDataChangedListener;
    private OnItemActionListener onItemActionListener;
    TextView name;
    public TextView date;
    TextView questionn;
    TextView like;
    TextView comments;
    TextView category;
    CircleImageView profile;
    ShapeableImageView questionImage;
    AppCompatImageView likeDislike, menuImage;
    LinearLayoutCompat commentLayout;
            private Fragment fragment; // Reference to the Fragment
    //        OnItemActionListener itemActionListener;
    public ItemViewHolder(@NonNull View itemView,  Fragment fragment) {
        super(itemView);
//        this.onItemActionListener = listener;

        name = itemView.findViewById(R.id.name);
        date = itemView.findViewById(R.id.date);
        questionn = itemView.findViewById(R.id.question);
        category = itemView.findViewById(R.id.category);
        like = itemView.findViewById(R.id.likescount);
        comments = itemView.findViewById(R.id.commentscount);
        profile = itemView.findViewById(R.id.profile_image);
        questionImage = itemView.findViewById(R.id.imageQuestion);
        likeDislike = itemView.findViewById(R.id.LikeDislike);
        commentLayout = itemView.findViewById(R.id.layoutComment);
        menuImage = itemView.findViewById(R.id.menuItem);

        this.fragment = fragment;

    }

    public void onDeleteItem(Question questionModal) {

        Log.d("Reload", "onDeleteItem: " + questionModal.getQuestionId());
        ProgressDialog progressDialog = new ProgressDialog(itemView.getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Prevent user from dismissing
//        progressDialog.show();


        Log.d("ID", "onDeleteItem: " + questionModal.getQuestionId());

//        Toast.makeText(itemView.getContext(), "", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference().child("askmate").child("questions").child(questionModal.getQuestionId())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("questions")
                                .child(questionModal.getQuestionId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(itemView.getContext(), "Your question successfully deleted", Toast.LENGTH_SHORT).show();

                                        if (fragment instanceof fragment_QnA) {
                                            ((fragment_QnA) fragment).loadinitialdata();  // Refresh the adapter in the Fragment
                                        }
//                                        loadQuestions(currentPage, PAGE_SIZE);
//                                        loadQuestions();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(itemView.getContext(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();

                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(itemView.getContext(), "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void bind(Question questionData, OnItemActionListener itemActionListener) {
        if (questionData.getUid().equals(FirebaseAuth.getInstance().getUid())) {
            // Condition to check if the UID matches
            menuImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(view.getContext(), view);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.report:
                                    Toast.makeText(view.getContext(), "Reported", Toast.LENGTH_LONG).show();
                                    return true;
                                case R.id.delete:
//                                    Toast.makeText(view.getContext(), "Delete operation", Toast.LENGTH_LONG).show();
//                                    itemActionListener.onDeleteItem(questionData);
                                    onDeleteItem(questionData);
//                                    itemActionListener.onDeleteItem(questionData);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.questionmenu);
                    popup.show();
                }
            });

        } else {
            menuImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(view.getContext(), view);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.report:
                                    Toast.makeText(view.getContext(), "Report", Toast.LENGTH_LONG).show();
                                    return true;
                                case R.id.delete:
                                    Toast.makeText(view.getContext(), "Delete", Toast.LENGTH_LONG).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.questionmenu);

                    // Remove the delete menu item if UID matches
                    Menu menu = popup.getMenu();
                    MenuItem deleteItem = menu.findItem(R.id.delete);
                    if (deleteItem != null) {
                        menu.removeItem(R.id.delete);
                    }

                    popup.show();
                }
            });
        }
    }


//    public void setItem(Question question) {
//       questionn.setText(question.getQuestion());
//         name;
//         date;
//
//        like;
//        comments;
//        category;
//        profile;
//        questionImage;
//        likeDislike, menuImage;
//        commentLayout;
//    }

    public static class AdViewHolder extends ItemViewHolder {
        NativeAdView adView;

        View placeholderView;

        public AdViewHolder(@NonNull View itemView, Fragment fragment) {
            super(itemView, fragment);
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
