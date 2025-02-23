package com.askmate;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.askmate.Modal.Question;

import java.util.List;

public class ListDataSource extends PositionalDataSource<Question> {
    private final List<Question> questionList;

    public ListDataSource(List<Question> questionList) {
        this.questionList = questionList;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Question> callback) {
        callback.onResult(questionList, 0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Question> callback) {
        int start = params.startPosition;
        int end = Math.min(start + params.loadSize, questionList.size());
        callback.onResult(questionList.subList(start, end));
    }
}
