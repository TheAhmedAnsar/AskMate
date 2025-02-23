package com.askmate.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.askmate.Adapter.AnswerAdapter;
//import com.askmate.Adapter.QnA_Adapter;
import com.askmate.Adapter.QuestionAdapter;
import com.askmate.Modal.QnA;
import com.askmate.Modal.myAnswerModal;
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
 * Use the {@link my_answer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class my_answer extends Fragment {

    private List<myAnswerModal> answerModalList;
    private AnswerAdapter adapter;
    RecyclerView recyclerView;
    TextView emptyview;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public my_answer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment my_answer.
     */
    // TODO: Rename and change types and number of parameters
    public static my_answer newInstance(String param1, String param2) {
        my_answer fragment = new my_answer();
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
        View view = inflater.inflate(R.layout.fragment_my_answer, container, false);
//        ArrayList<Home_modal> modals = new ArrayList<>();
////        Home_modal[] modal = new Home_modal[]
//        ArrayList<QnA[]> mo = new ArrayList<>();
        answerModalList = new ArrayList<>();
        adapter = new AnswerAdapter(answerModalList);

         recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        emptyview = view.findViewById(R.id.anotherLayout);

        recyclerView.setAdapter(adapter);


        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getUid()).child("answers");


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot answerIdSnapshot: snapshot.getChildren()) {
                    String answerId = answerIdSnapshot.getKey();
                    String questionId = answerIdSnapshot.getValue(String.class);

                    DatabaseReference questionRef = FirebaseDatabase.getInstance().getReference()
                            .child("askmate").child("questions").child(questionId);
                    questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String question = dataSnapshot.child("question").getValue(String.class);
                            DatabaseReference answerRef = FirebaseDatabase.getInstance().getReference()
                                    .child("askmate").child("questions").child(questionId)
                                    .child("answers").child(answerId);
                            answerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // Check if answer exists
                                    if (snapshot.exists()) {
                                        String answer = snapshot.child("answer").getValue(String.class);
                                        // Add both question and answer to your RecyclerView adapter
                                        answerModalList.add(0, new myAnswerModal(answerId, questionId, answer, question));
                                        adapter.notifyDataSetChanged();
                                        updateEmptyView();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle error
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });

                }
                if (!snapshot.hasChildren()) {
                    updateEmptyView();
                }

//                updateEmptyView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        return view;
    }
    private void updateEmptyView() {
        if (answerModalList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyview.setVisibility(View.VISIBLE);
//            Toast.makeText(getActivity(), "I'm ONN", Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyview.setVisibility(View.GONE);
//            Toast.makeText(getActivity(), "I'm OFF", Toast.LENGTH_SHORT).show();

        }
    }
}