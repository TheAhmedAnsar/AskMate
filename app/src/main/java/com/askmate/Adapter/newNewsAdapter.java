package com.askmate.Adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.askmate.Modal.newsModal;

import java.util.ArrayList;

public class newNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<newsModal> newsModals;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
