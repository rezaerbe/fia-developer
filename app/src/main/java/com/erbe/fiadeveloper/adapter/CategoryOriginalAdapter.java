package com.erbe.fiadeveloper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.erbe.fiadeveloper.databinding.ItemCategoryOriginalBinding;
import com.erbe.fiadeveloper.model.Category;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;



public class CategoryOriginalAdapter extends  FirestoreAdapter<CategoryOriginalAdapter.ViewHolder> {

    public interface OnCategoryOriginalSelectedListener {

        void onCategoryOriginalSelected(DocumentSnapshot category);

    }

    private final OnCategoryOriginalSelectedListener mListener;

    public CategoryOriginalAdapter(Query query, OnCategoryOriginalSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCategoryOriginalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCategoryOriginalBinding binding;

        public ViewHolder(ItemCategoryOriginalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnCategoryOriginalSelectedListener listener) {

            Category category = snapshot.toObject(Category.class);

            binding.categoryOriginal.setText(category.getCatName());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCategoryOriginalSelected(snapshot);
                    }
                }
            });
        }

    }
}
