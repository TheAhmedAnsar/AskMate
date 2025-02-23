package com.askmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.askmate.Adapter.Adapter_of_search_user;
import com.askmate.Modal.Question;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SearchQuestions extends AppCompatActivity {

    SearchView searchView;
    Adapter_of_search_user adapters;
    RecyclerView recyclerView;
//    FirebaseRecyclerOptions<Question>  options;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_questions);

        searchView = findViewById(R.id.searchview);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty query
        FirebaseRecyclerOptions<Question> options = new FirebaseRecyclerOptions
                .Builder<Question>()
                .setQuery(null, Question.class)
                .build();

        adapters = new Adapter_of_search_user(options);
        recyclerView.setAdapter(adapters);
//        adapters.startListening(); // Start listening initially

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                adapters.startListening();
//                adapters.stopListening();
                processSearch(newText);
                return false;
            }
        });
    }

    private void processSearch(String query) {
        String words = query.toLowerCase();
        Query firebaseSearchQuery;

        if (words.isEmpty()) {
            // If query is empty, set firebaseSearchQuery to null to display nothing
            firebaseSearchQuery = null;
        } else {
            // Construct the query to search for 'words' in the 'question' field
            firebaseSearchQuery = FirebaseDatabase.getInstance().getReference()
                    .child("askmate").child("questions")
                    .orderByChild("question")
                    .startAt(words)
                    .endAt(words + "\uf8ff");
        }
        FirebaseRecyclerOptions<Question> options;
        if (firebaseSearchQuery == null) {
            // Show the last 10 items if firebaseSearchQuery is null (no search query)
            options = new FirebaseRecyclerOptions.Builder<Question>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("askmate").child("questions").orderByChild("question").limitToLast(5), Question.class)
                    .build();
        } else {
            // Build options with the firebaseSearchQuery to display filtered results
            options = new FirebaseRecyclerOptions.Builder<Question>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("askmate").child("questions").orderByChild("question"), Question.class)
                    .build();
        }


        adapters.updateOptions(options); // Update the adapter with the new query options
        adapters.startListening(); // Start listening for data changes
    }

//    private void processSearch(String query) {
//        String[] words = query.toLowerCase().split(" ");
//        Query firebaseSearchQuery = FirebaseDatabase.getInstance().getReference().child("askmate").child("questions");
//
//        final Set<Question> resultSet = new HashSet<>();
//
//        for (String word : words) {
//            Query wordQuery = firebaseSearchQuery.orderByChild("question").startAt(word).endAt(word + "\uf8ff");
//
//            wordQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Question question = snapshot.getValue(Question.class);
//                        resultSet.add(question);
//                        Log.d("run", "onDataChange: Running"  );
//                    }
//                    updateRecyclerView(new ArrayList<>(resultSet));
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle possible errors.
//                }
//            });
//        }
//    }

    private void updateRecyclerView(List<Question> questions) {
        // Update your RecyclerView adapter with the new list of questions.
        adapters.updateData(questions); // You'll need to create this method in your adapter
        adapters.notifyDataSetChanged();
    }

}
