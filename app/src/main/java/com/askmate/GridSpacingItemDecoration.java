package com.askmate;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;
    private final int spanCount;

    public GridSpacingItemDecoration(Context context, int spacing, int spanCount) {
        this.spacing = spacing;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        outRect.left = spacing / 2;
        outRect.right = spacing / 2;

//        if (column == 0) {
//            outRect.left = spacing;
//        } else if (column == spanCount - 1) {
            outRect.right = spacing;
//        }

//        outRect.bottom = spacing;
    }
}
