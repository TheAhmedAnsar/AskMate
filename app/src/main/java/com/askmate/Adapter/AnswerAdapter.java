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

import com.askmate.Modal.myAnswerModal;
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

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder>{
String totalLikes;
boolean likechekcer;
    private List<myAnswerModal> answerList;

    public AnswerAdapter(List<myAnswerModal> answerList) {
        this.answerList = answerList;
    }



    @NonNull
    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummymyanswer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerAdapter.ViewHolder holder, int position) {

        myAnswerModal answerItem = answerList.get(position);
        String questionText = "Q. " + answerItem.getQuestion();
        int maxLength = 80; // Set the maximum length before adding ellipsis

        if (questionText.length() > maxLength) {
            String trimmedText = questionText.substring(0, maxLength) + "...";
            holder.questionTextView.setText(trimmedText);
            Log.d("text", "onBindViewHolder: " + trimmedText);
        } else {
            holder.questionTextView.setText(questionText);
            Log.d("text", "onBindViewHolder: " + questionText);

        }
//        holder.questionTextView.setText(questionText);
        String answerText = "Ans. " + answerItem.getAnswer();
        holder.answerTextView.setText(answerText);


        holder.viewAllAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), QnA_Ans.class);
                myIntent.putExtra("myQuestionId", answerList.get(position).getQuestionId());
                myIntent.putExtra("myAnswerId", answerList.get(position).getAnswerId());
                myIntent.putExtra("uid", FirebaseAuth.getInstance().getUid());
                myIntent.putExtra("check", "myAns");
                view.getContext().startActivity(myIntent);



            }
        });

        String userId = FirebaseAuth.getInstance().getUid();
        String AnswerId = answerList.get(position).getAnswerId();
        Log.d("IdNew", "onBindViewHolder: " + AnswerId);
        DatabaseReference likesref = FirebaseDatabase.getInstance().getReference().child("askmate").child("likesAnswers");
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


            }
        });
        fetchLikesCount(answerList.get(position).getAnswerId(), holder);

    }

    private void fetchLikesCount(String AnswerId, ViewHolder holder) {

        DatabaseReference likesCount = FirebaseDatabase.getInstance().getReference().child("askmate").child("likesAnswers").child(AnswerId);

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

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView questionTextView, like;
        public TextView answerTextView;
        public TextView viewAllAnswers;
        AppCompatImageView likeDislike;


        public ViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.question);
            answerTextView = itemView.findViewById(R.id.answer);
            viewAllAnswers = itemView.findViewById(R.id.viewAll);
            likeDislike = itemView.findViewById(R.id.LikeDislike);
            like = itemView.findViewById(R.id.likescount);

        }
    }

}
