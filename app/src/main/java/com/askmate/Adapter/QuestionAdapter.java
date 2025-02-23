package com.askmate.Adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.askmate.Modal.myQuestionModal;
import com.askmate.QnA_Ans;
import com.askmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<myQuestionModal> questionList;
boolean likechekcer;
String totalLikes ;
    public QuestionAdapter(List<myQuestionModal> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummymyquestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        myQuestionModal questionItem = questionList.get(position);
        holder.questionTextView.setText(questionItem.getQuestion());

        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), QnA_Ans.class);
                myIntent.putExtra("myQuestionId", questionList.get(position).getQuestionId());
                myIntent.putExtra("uid", FirebaseAuth.getInstance().getUid());
                myIntent.putExtra("check", "myQnA");
                view.getContext().startActivity(myIntent);

            }
        });

        String userId = FirebaseAuth.getInstance().getUid();
        String QuestionId = questionList.get(position).getQuestionId();
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
            }
        });

        fetchanswer(questionList.get(position).getQuestionId(), holder);
fetchLikesCount(questionItem.getQuestionId(), holder);

    }



    @Override
    public int getItemCount() {
        return questionList.size();
    }

    private void fetchLikesCount(String questionId, ViewHolder holder) {

        DatabaseReference likesCount = FirebaseDatabase.getInstance().getReference().child("askmate").child("likesQuestions").child(questionId);

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


    private void fetchanswer(String uid, ViewHolder holder) {
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
                                holder.commentCount.setText(commentCount);
                            } else {
                                holder.commentCount.setText("Be the first to answer!");

                            }
                        }
//                else
//                {
//
//                }

                    }
                }
                else
                {
                    holder.commentCount.setText("Be the first to answer!");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView questionTextView;
        public TextView commentCount, like;
        AppCompatImageView likeDislike;
        LinearLayoutCompat commentLayout;



        public ViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.question);
            commentCount = itemView.findViewById(R.id.commentscount);
            likeDislike = itemView.findViewById(R.id.LikeDislike);
            like = itemView.findViewById(R.id.likescount);
            commentLayout = itemView.findViewById(R.id.layoutComment);


        }
    }
}
