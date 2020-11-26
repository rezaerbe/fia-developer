package com.fia.femaleinaction.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.fia.femaleinaction.databinding.ItemCategoryArticleBinding;
import com.fia.femaleinaction.model.Category;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class CategoryArticleAdapter extends  FirestoreAdapter<CategoryArticleAdapter.ViewHolder> {

    public interface OnCategoryArticleSelectedListener {

        void onCategoryArticleSelected(DocumentSnapshot category);

    }

    private final OnCategoryArticleSelectedListener mListener;

    public CategoryArticleAdapter(Query query, OnCategoryArticleSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCategoryArticleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCategoryArticleBinding binding;

        public ViewHolder(ItemCategoryArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnCategoryArticleSelectedListener listener) {

            Category category = snapshot.toObject(Category.class);

            assert category != null;
            binding.categoryArticle.setText(category.getCatName());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCategoryArticleSelected(snapshot);
                    }
                }
            });
        }

    }
}
