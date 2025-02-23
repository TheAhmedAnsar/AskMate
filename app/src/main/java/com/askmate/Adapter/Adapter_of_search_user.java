package com.askmate.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.askmate.Modal.Question;
import com.askmate.QnA_Ans;
import com.askmate.R;
import com.bumptech.glide.Glide;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//public class Adapter_of_search_user extends RecyclerView.Adapter<Adapter_of_search_user.UserViewHolder> {
public class Adapter_of_search_user extends FirebaseRecyclerAdapter<Question,Adapter_of_search_user.UserViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     *
     */
    private List<Question> questions = new ArrayList<>();
    String value;
    public Context context;

    public Adapter_of_search_user(@NonNull FirebaseRecyclerOptions<Question> options) {
        super(options);
    }


//    private List<searchusers> users;
//
//    public Adapter_of_search_user(List<searchusers> users) {
//        this.users = users;
//    }

    @NonNull
    @Override
    public Adapter_of_search_user.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummysearchquestion, parent, false);
        return new UserViewHolder(view);
    }

    public void updateData(List<Question> questions) {
        this.questions = questions;
        notifyDataSetChanged();
    }
    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Question model) {

        context = holder.itemView.getContext();
        holder.questionText.setText(model.getQuestion());
//        String questionId = model.getQuestionId();

        DatabaseReference questionRef = getRef(position);
        model.setQuestionId(questionRef.getKey());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), QnA_Ans.class);
                myIntent.putExtra("check", "searchQna");
////                myIntent.putExtra("name", holder.name.getText().toString());
////                myIntent.putExtra("question", modal.get(position).getQuestion());
////                myIntent.putExtra("category", modal.get(position).getCategory());
////                myIntent.putExtra("time", holder.date.getText().toString());
////                myIntent.putExtra("image", modal.get(position).getImage());
                myIntent.putExtra("uid", model.getUid());
                myIntent.putExtra("myQuestionId", model.getQuestionId());

                view.getContext().startActivity(myIntent);
                Log.d("ID", "onClick: " + model.getQuestionId());
            }
        });

//
//        holder.username.setText(model.getUsername());
//        holder.profileName.setText(model.getProfile_name());
//
//        showprofile(holder.profileImage, holder.username, model.getScholar_id());
//
//
//        Glide.with(holder.profileImage.getContext()).load(model.getImage()).into(holder.profileImage);
//
//        SharedPreferences sharedPreferences = context.getSharedPreferences("myKey", MODE_PRIVATE);
//        value = sharedPreferences.getString("value", "");
//
//        System.out.println(model.getScholar_id() + " This is Something new ");


//         For second recyclerview





//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//
//
//                System.out.println(model.getUsername());
//
//
//                if (Objects.equals(model.getScholar_id(), value.trim()))
//                {
////                    ((user_details)context).setupViewPager(2);
////                    Toast.makeText(context, "Your Id", Toast.LENGTH_SHORT).show();
//
//                    Intent intent = new Intent(view.getContext(), MainActivity.class);
//                    intent.putExtra("NAVIGATION_TAB", 4); // Index of the third tab (zero-based)
//                    view.getContext().startActivity(intent);
//
//                }
//                else {
//                    Intent intent = new Intent(holder.itemView.getContext(), Searched_Uses_Profile.class);
//                    intent.putExtra("scholar_id", model.getScholar_id());
//                    intent.putExtra("name", model.getUsername());
//
//                    SharedPreferences contextSharedPreferences = context.getSharedPreferences("SearchedUsers", MODE_PRIVATE);
//String key = model.getScholar_id();
//                    SharedPreferences.Editor editor = contextSharedPreferences.edit();
//                    editor.putString(key, model.getScholar_id());
//                    editor.apply();
//
//                    view.getContext().startActivity(intent);
//                }
//
//
//            }
//        });


    }
//
//
//    @Override
//    public int getItemCount() {
//        return questions.size();
//    }



    public class UserViewHolder extends RecyclerView.ViewHolder {
      TextView questionText;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question);
//            profileImage = itemView.findViewById(R.id.profile_qna);
//            profileName = itemView.findViewById(R.id.username);
//            username = itemView.findViewById(R.id.public_name);

        }
    }


}
