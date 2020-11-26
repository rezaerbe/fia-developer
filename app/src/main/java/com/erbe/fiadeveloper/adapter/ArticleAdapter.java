package com.erbe.fiadeveloper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ItemArticleBinding;
import com.erbe.fiadeveloper.model.Article;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class ArticleAdapter extends  FirestoreAdapter<ArticleAdapter.ViewHolder> {

    public interface OnArticleSelectedListener {

        void onArticleSelected(DocumentSnapshot article);

    }

    private final OnArticleSelectedListener mListener;

    public ArticleAdapter(Query query, OnArticleSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemArticleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemArticleBinding binding;

        public ViewHolder(ItemArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnArticleSelectedListener listener) {

            Article article = snapshot.toObject(Article.class);

            // Load image
            assert article != null;
            Glide.with(binding.articleImage.getContext())
                    .load(article.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.empty)
                    .into(binding.articleImage);

            binding.articleTitle.setText(article.getTitle());
            binding.articleSource.setText(article.getSource());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onArticleSelected(snapshot);
                    }
                }
            });
        }

    }
}
