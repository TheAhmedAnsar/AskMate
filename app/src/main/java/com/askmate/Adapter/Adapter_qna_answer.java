package com.askmate.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.askmate.Modal.Question;
import com.bumptech.glide.Glide;
import com.askmate.Modal.QnA;
import com.askmate.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_qna_answer extends RecyclerView.Adapter<Adapter_qna_answer.ViewHolder>{

String totalLikes ;
    ArrayList<QnA> modal = new ArrayList<>();
    private OnItemActionListener itemActionListener;

    private Context context;
    private String highlightedAnswerId;
    RecyclerView recyclerView;
    private String questionId;
    boolean likechekcer;

//    public Adapter_qna_answer(OnItemActionListener itemActionListener, ArrayList<QnA> modal, OnItemActionListener itemActionListener, Context context, String highlightedAnswerId, RecyclerView recyclerView, String questionId) {
//        this.modal = modal;
//        this.itemActionListener = itemActionListener;
//        this.context = context;
//        this.highlightedAnswerId = highlightedAnswerId;
//        this.recyclerView = recyclerView;
//        this.questionId = questionId;
//    }

    public Adapter_qna_answer(OnItemActionListener itemActionListener,String questionId, ArrayList<QnA> modal, Context context, String highlightedAnswerId, RecyclerView recyclerView) {
        this.questionId = questionId;
        this.modal = modal;
        this.context = context;
        this.highlightedAnswerId = highlightedAnswerId;
        this.recyclerView = recyclerView;
        this.itemActionListener = itemActionListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.dummy_answer, parent, false);
      ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    public interface OnItemActionListener {
        void onDeleteItem(QnA QnaModal);
    }


    public void setHighlightedAnswerId(String highlightedAnswerId) {
        this.highlightedAnswerId = highlightedAnswerId;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QnA question = modal.get(position);
//        holder.name.setText(modal.get(position).getName());
        holder.date.setText(getTimeAgo(modal.get(position).getTimestamp()));
        holder.question.setText(modal.get(position).getAnswer());


        if (modal.get(position).getImage() != null && !modal.get(position).getImage().isEmpty()) {
            holder.answerImage.setVisibility(View.VISIBLE);

            // Load image using Glide or Picasso library
            Glide.with(holder.itemView.getContext()).load(modal.get(position).getImage()).into(holder.answerImage);
        } else {
            holder.answerImage.setVisibility(View.GONE);
        }
        if (modal.get(position).getAnswerid().equals(highlightedAnswerId)) {
            // Highlight this answer
            holder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.newColor));
//);
            // Use a Handler to remove the highlight after a delay
            new Handler().postDelayed(() -> removeHighlight(holder.mainLayout), 2000);
        } else {


//            holder.linearLayoutCompat.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("myUid", "onClick: " + modal.get(position).getUid());

            }
        });
        DatabaseReference likesref = FirebaseDatabase.getInstance().getReference().child("askmate").child("likesAnswers");
        String AnswerId = modal.get(position).getAnswerid();
        String userId = FirebaseAuth.getInstance().getUid();

        holder.bind(question, itemActionListener);

        holder.likeDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                likechekcer = true;

                likesref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (likechekcer == true)
                        {
                            if (snapshot.child(AnswerId).hasChild(userId))
                            {
                                likesref.child(AnswerId).child(userId).removeValue();
                                likechekcer = false;
                            }
                            else
                            {
                                likesref.child(AnswerId).child(userId).setValue(true);
                                likechekcer = false;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//                likesref.runTransaction(new Transaction.Handler() {
//                    @NonNull
//                    @Override
//                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
//                        // Get the current value of likes for the post
//                        Boolean currentLikes = mutableData.child(QuestionId).child(userId).getValue(Boolean.class);
//
//                        if (likechekcer == true) {
//                            // If the current user has already liked the post, remove the like
//                            if (currentLikes != null && currentLikes) {
//                                mutableData.child(QuestionId).child(userId).setValue(null);
//                                likechekcer = false;
//                            } else {
//                                // If the current user has not liked the post, add the like
//                                mutableData.child(QuestionId).child(userId).setValue(true);
//                                likechekcer = false;
//                            }
//                        }
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
//                        if (databaseError != null) {
//                            Log.d("TAG", "Firebase transaction failed.", databaseError.toException());
//                        }
//                    }
//                });

            }
        });

        String questionId = this.questionId;

fetchLikesCount(questionId, holder, modal.get(position).getAnswerid());
        fetchUserProfile(modal.get(position).getUid(), holder);

    }

    private void removeHighlight(LinearLayoutCompat linearLayoutCompat) {
                    linearLayoutCompat.setBackgroundColor(Color.TRANSPARENT);

    }

    private void fetchLikesCount(String questionId, ViewHolder holder, String AnswerId) {

        DatabaseReference likesCount = FirebaseDatabase.getInstance().getReference().child("askmate").child("likesAnswers")
                .child(AnswerId);

        likesCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    long likes = snapshot.getChildrenCount();
                    if (likes > 1)
                    {
                        totalLikes = likes + " likes";
                    }
                    else if(likes == 1)
                    {
                        totalLikes = String.valueOf(likes + " like");

                    }
                    holder.like.setText(totalLikes) ;
                }
                else
                {
                    String zeroCount = "0 Like";
                    holder.like.setText(zeroCount) ;

                }
                if (snapshot.hasChild(FirebaseAuth.getInstance().getUid()))
                {
                    holder.likeDislike.setImageResource(R.drawable.like);
                }
                else{
                    holder.likeDislike.setImageResource(R.drawable.dislike);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void fetchUserProfile(String uid, ViewHolder holder) {
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
                    Glide.with(context).load(profileImageUrl).placeholder(R.drawable.plus).into(holder.profile);
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


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, date, question, like, comments;
        CircleImageView profile;
        ShapeableImageView answerImage;
        LinearLayoutCompat linearLayoutCompat, mainLayout;
        AppCompatImageView likeDislike, menuImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            question = itemView.findViewById(R.id.question);
            like = itemView.findViewById(R.id.likescount);
//            comments = itemView.findViewById(R.id.commentscount);
            profile = itemView.findViewById(R.id.profile_image);
            answerImage = itemView.findViewById(R.id.imageAnswer);
            linearLayoutCompat = itemView.findViewById(R.id.linearLayoutCompat16);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            likeDislike = itemView.findViewById(R.id.LikeDislike);
            menuImage = itemView.findViewById(R.id.menuItem);





        }

        public void bind(QnA questionData, OnItemActionListener itemActionListener) {
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
//                                        Toast.makeText(view.getContext(), "Delete operation", Toast.LENGTH_LONG).show();
                                    itemActionListener.onDeleteItem(questionData);
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
