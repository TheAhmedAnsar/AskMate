//package com.askmate;
//
//import androidx.annotation.NonNull;
//import androidx.paging.PagingSource;
//import androidx.paging.PagingState;
//
//import com.askmate.Modal.Question;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class QuestionPagingSource extends PagingSource<String, Question> {
//
//    private DatabaseReference questionsRef;
//
//    public QuestionPagingSource(DatabaseReference questionsRef) {
//        this.questionsRef = questionsRef;
//    }
//
//    @NonNull
//    @Override
//    public LoadResult<String, Question> load(@NonNull LoadParams<String> params) {
//        String startAfter = params.getKey();
//        Query query = questionsRef.orderByKey().startAfter(startAfter).limitToFirst(params.getLoadSize());
//
//        try {
//            DataSnapshot dataSnapshot = query.get().getResult();
//            List<Question> questions = new ArrayList<>();
//            String lastKey = null;
//
//            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                Question question = snapshot.getValue(Question.class);
//                if (question != null) {
//                    question.setQuestionId(snapshot.getKey());
//                    questions.add(question);
//                    lastKey = snapshot.getKey();
//                }
//            }
//
//            return new LoadResult.Page<>(
//                questions,
//                null, // No previous page key
//                lastKey // Next page key
//            );
//        } catch (Exception e) {
//            return new LoadResult.Error<>(e);
//        }
//    }
//
//    @NonNull
//    @Override
//    public PagingState<String, Question> getRefreshKey(@NonNull PagingState<String, Question> state) {
//        // Provide a key for refreshing data, e.g., the last item key
//        return null;
//    }
//}
