package com.askmate.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.askmate.Fragments.OnDataChangedListener;
import com.askmate.Fragments.fragment_QnA;
import com.askmate.Modal.Question;
import com.askmate.OnItemActionListener;
import com.askmate.QnA_Ans;
import com.askmate.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

//        public class newQuestionAdapter extends RecyclerView.Adapter<newQuestionAdapter.ItemViewHolder> {
public class newQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private OnItemActionListener onItemActionListener;
        String totalLikes;





    public newQuestionAdapter(Context context, ArrayList<Question> modal , OnItemActionListener onItemActionListener) {
        this.context = context;
        this.onItemActionListener = onItemActionListener;
        this.modal = modal;
    }

//            @Override
//            public int getItemViewType(int position) {
//                if (position % 5 == 0 && position != 0) {
//                    return VIEW_TYPE_AD;
//                }
//                return VIEW_TYPE_ITEM;
//            }
    public interface OnItemActionListener {
        void onDeleteItem(Question questionModal, int position);
    }

    public void removeItem(int position) {
        modal.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, modal.size());
    }


    public void addData(List<Question> newData) {
        modal.addAll(newData);
        int startPosition = modal.size();
        notifyItemRangeInserted(startPosition, newData.size());
    }

    public void clearData() {
        modal.clear();
        notifyDataSetChanged();
    }



    private ArrayList<Question> modal;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummy_qna, parent, false);
//        return new ItemViewHolder(view);

//        if (viewType == VIEW_TYPE_AD) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.native_ad_layout, parent, false);
//            return new AdViewHolder(view);
//        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummy_qna, parent, false);
            return new ItemViewHolder(view);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//    public void onBindViewHolder(@NonNull newQuestionAdapter.ItemViewHolder holder, int position) {
//        CourseModal courses = courseModalArrayList.get(position);
//        holder.courseNameTV.setText(courses.getCourseName());

//        Question question =courseModalArrayList.get(position);
//        holder.questionn.setText(question.getQuestion());
//        if (getItemViewType(position) == VIEW_TYPE_AD) {
//            AdViewHolder adViewHolder = (AdViewHolder) holder;
//            loadNativeAd(adViewHolder.adView);
//        }
//        else
//        {
            Question question = modal.get(position);


//            ((ItemViewHolder) holder).date.setText(modal.get(position).getTimestamp());
            ((ItemViewHolder)  holder).name.setText(modal.get(position).getQuestion());


            ((ItemViewHolder)  holder).date.setText(getTimeAgo(modal.get(position).getTimestamp()));
            ((ItemViewHolder)  holder).questionn.setText(modal.get(position).getQuestion());
            ((ItemViewHolder)  holder).category.setText(modal.get(position).getCategory());
            ((ItemViewHolder)  holder).bind(question, onItemActionListener, position);


            // Check if image URL is available and load image using Glide or Picasso library
            if (modal.get(position).getImage() != null && !modal.get(position).getImage().isEmpty()) {
                ((ItemViewHolder) holder).questionImage.setVisibility(View.VISIBLE);

                // Load image using Glide or Picasso library
                Glide.with(context).load(modal.get(position).getImage()).into(((ItemViewHolder) holder).questionImage);
            } else {
                ((ItemViewHolder) holder).questionImage.setVisibility(View.GONE);
            }

            ((ItemViewHolder) holder).commentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), QnA_Ans.class);
                    myIntent.putExtra("check", "QnA");
                    myIntent.putExtra("name",  ((ItemViewHolder)  holder).name.getText().toString());
                    myIntent.putExtra("question", modal.get(position).getQuestion());
                    myIntent.putExtra("category", modal.get(position).getCategory());
                    myIntent.putExtra("time",  ((ItemViewHolder)  holder).date.getText().toString());
                    myIntent.putExtra("image", modal.get(position).getImage());
                    myIntent.putExtra("uid", modal.get(position).getUid());
                    myIntent.putExtra("questionId", modal.get(position).getQuestionId());

                    view.getContext().startActivity(myIntent);
                }
            });

            fetchUserProfile(question.getUid(), holder);
            fetchLikesCount(question.getQuestionId(), holder);
            fetchanswer(question.getQuestionId(), holder);


//        }

//        Question question = modal.get(position);
//
//
////            ((ItemViewHolder) holder).date.setText(modal.get(position).getTimestamp());
//        ((ItemViewHolder)  holder).name.setText(modal.get(position).getQuestion());
//
//
//        ((ItemViewHolder)  holder).date.setText(getTimeAgo(modal.get(position).getTimestamp()));
//        ((ItemViewHolder)  holder).questionn.setText(modal.get(position).getQuestion());
//        ((ItemViewHolder)  holder).category.setText(modal.get(position).getCategory());
//        ((ItemViewHolder)  holder).bind(question, onItemActionListener, position);
//
//
//        // Check if image URL is available and load image using Glide or Picasso library
//        if (modal.get(position).getImage() != null && !modal.get(position).getImage().isEmpty()) {
//            ((ItemViewHolder) holder).questionImage.setVisibility(View.VISIBLE);
//
//            // Load image using Glide or Picasso library
//            Glide.with(context).load(modal.get(position).getImage()).into(((ItemViewHolder) holder).questionImage);
//        } else {
//            ((ItemViewHolder) holder).questionImage.setVisibility(View.GONE);
//        }
//
//        ((ItemViewHolder) holder).commentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent myIntent = new Intent(view.getContext(), QnA_Ans.class);
//                myIntent.putExtra("check", "QnA");
//                myIntent.putExtra("name",  ((ItemViewHolder)  holder).name.getText().toString());
//                myIntent.putExtra("question", modal.get(position).getQuestion());
//                myIntent.putExtra("category", modal.get(position).getCategory());
//                myIntent.putExtra("time",  ((ItemViewHolder)  holder).date.getText().toString());
//                myIntent.putExtra("image", modal.get(position).getImage());
//                myIntent.putExtra("uid", modal.get(position).getUid());
//                myIntent.putExtra("questionId", modal.get(position).getQuestionId());
//
//                view.getContext().startActivity(myIntent);
//            }
//        });
//
//        fetchUserProfile(question.getUid(), holder);
//        fetchLikesCount(question.getQuestionId(), holder);
//        fetchanswer(question.getQuestionId(), holder);


    }

//        private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
//        // Set the headline
//        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
//        // Set the body
//        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
//        // Set the call to action
//        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
//
//        // Set the icon if available
//        if (nativeAd.getIcon() == null) {
//            adView.getIconView().setVisibility(View.GONE);
//        } else {
//            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
//            adView.getIconView().setVisibility(View.VISIBLE);
//        }
//
//        // Assign the native ad object to the native ad view
//        adView.setNativeAd(nativeAd);
//    }


        private void fetchLikesCount(String questionId, RecyclerView.ViewHolder holder) {

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
                    ((ItemViewHolder) holder).like.setText(totalLikes);
                } else {
                    String zeroCount = "0 Like";
                    ((ItemViewHolder) holder).like.setText(zeroCount);

                }
                if (snapshot.hasChild(FirebaseAuth.getInstance().getUid())) {
                    ((ItemViewHolder) holder).likeDislike.setImageResource(R.drawable.like);
                } else {
                    ((ItemViewHolder) holder).likeDislike.setImageResource(R.drawable.dislike);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void fetchanswer(String uid, RecyclerView.ViewHolder holder) {
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
                                ((ItemViewHolder) holder).comments.setText(commentCount);
                            } else {
                                ((ItemViewHolder) holder).comments.setText("Be the first to answer!");

                            }
                        }
//                else
//                {
//
//                }

                    }
                } else {
                    ((ItemViewHolder) holder).comments.setText("Be the first to answer!");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


        private void fetchUserProfile(String uid, RecyclerView.ViewHolder holder) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("image").getValue(String.class);

                    // Set username
                    ((ItemViewHolder) holder).name.setText(username);

                    // Load profile image using Glide or Picasso library
                    Glide.with(context).load(profileImageUrl).placeholder(R.drawable.plus).into( ((ItemViewHolder) holder).profile);
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




    @Override
    public int getItemCount() {
        return modal.size();

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
        public ItemViewHolder(@NonNull View itemView) {
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

                                            ((fragment_QnA) fragment).reloadData();  // Refresh the adapter in the Fragment


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

        public void bind(Question questionData, OnItemActionListener itemActionListener, int position) {
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
                                    itemActionListener.onDeleteItem(questionData, position);
//                                        onDeleteItem(questionData);
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

//        public class AdViewHolder extends ItemViewHolder {
//            NativeAdView adView;
//
//            View placeholderView;
//
//            public AdViewHolder(@NonNull View itemView, Fragment fragment) {
//                super(itemView);
//                // Initialize ad view
//                placeholderView = itemView.findViewById(R.id.ad_placeholder_view);
//                adView = (NativeAdView) itemView.findViewById(R.id.native_ad_view);
//                // Initialize other ad assets
//                adView.setMediaView(adView.findViewById(R.id.ad_media));
//                adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//                adView.setBodyView(adView.findViewById(R.id.ad_body));
//                adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
//                adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//            }
//
//            public void hideAdView() {
//                adView.setVisibility(View.GONE);
//            }
//
//            public void showAdView() {
//                adView.setVisibility(View.VISIBLE);
//            }
//        }


    }

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


            private String getTimeAgo(String timestamp) {
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
}

