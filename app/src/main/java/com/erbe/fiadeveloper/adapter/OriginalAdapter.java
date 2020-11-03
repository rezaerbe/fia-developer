package com.erbe.fiadeveloper.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.databinding.ItemOriginalBinding;
import com.erbe.fiadeveloper.model.Original;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class OriginalAdapter extends  FirestoreAdapter<OriginalAdapter.ViewHolder> {

    public interface OnOriginalSelectedListener {

        void onOriginalSelected(DocumentSnapshot original);

    }

    private OnOriginalSelectedListener mListener;

    public OriginalAdapter(Query query, OnOriginalSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOriginalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemOriginalBinding binding;

        public ViewHolder(ItemOriginalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnOriginalSelectedListener listener) {

            Original original = snapshot.toObject(Original.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(binding.originalImage.getContext())
                    .load(original.getImage())
                    .into(binding.originalImage);

            binding.originalTitle.setText(original.getTitle());
            binding.originalSource.setText(original.getSource());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onOriginalSelected(snapshot);
                    }
                }
            });
        }

    }
}
