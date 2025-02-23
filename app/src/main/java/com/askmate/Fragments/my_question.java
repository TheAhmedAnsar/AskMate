package com.askmate.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.askmate.Adapter.QnA_Adapter;
import com.askmate.Adapter.QuestionAdapter;
import com.askmate.Modal.QnA;
import com.askmate.Modal.myQuestionModal;
import com.askmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link my_question#newInstance} factory method to
 * create an instance of this fragment.
 */
public class my_question extends Fragment {


//    private RecyclerView recyclerView;
    private List<myQuestionModal> questionList;
    private QuestionAdapter adapter;
    RecyclerView recyclerView;
    NestedScrollView scrollView;
    TextView emptyview;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public my_question() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment my_question.
     */
    // TODO: Rename and change types and number of parameters
    public static my_question newInstance(String param1, String param2) {
        my_question fragment = new my_question();
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
        View view = inflater.inflate(R.layout.fragment_my_question, container, false);






         recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
         scrollView = view.findViewById(R.id.nestedScrollView);
         emptyview = view.findViewById(R.id.anotherLayout);

        questionList = new ArrayList<>();
        adapter = new QuestionAdapter(questionList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("questions");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate through the question IDs
//                questionList.clear();
                // Create a counter to track how many questions have been loaded
                List<myQuestionModal> tempQuestionList = new ArrayList<>();
                int totalQuestions = (int) dataSnapshot.getChildrenCount();
                final int[] loadedQuestions = {0};
                questionList.clear();
                for (DataSnapshot questionIdSnapshot : dataSnapshot.getChildren()) {
                    String questionId = questionIdSnapshot.getKey();

                    // Query the "questions" node to get the details of each question
                    DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference().child("askmate").child("questions").child(questionId);

                    questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Retrieve the question details
                            String question = dataSnapshot.child("question").getValue(String.class);

                            questionList.add(0,new myQuestionModal(question, questionId));
////                            adapter.notifyDataSetChanged();
//                            tempQuestionList.add(new myQuestionModal(question, questionId));
//
//                            // Increment the loaded questions counter
//                            loadedQuestions[0]++;
//
//                            // Check if all questions have been loaded
//                            if (loadedQuestions[0] == totalQuestions) {
//                                questionList.addAll(0, tempQuestionList);
                            adapter.notifyDataSetChanged();

                            // Check if questionList is empty
                            updateEmptyView();
//                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                }
            

                // Check if questionList is empty
                updateEmptyView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });


//        recyclerView.setAdapter(adapter);

        return view;
    }

    private void updateEmptyView() {
        if (questionList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyview.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyview.setVisibility(View.GONE);
        }
    }
}