package com.fia.femaleinaction.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fia.femaleinaction.R;
import com.fia.femaleinaction.databinding.ItemVideoBinding;
import com.fia.femaleinaction.model.Video;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class VideoAdapter extends  FirestoreAdapter<VideoAdapter.ViewHolder> {

    public interface OnVideoSelectedListener {

        void onVideoSelected(DocumentSnapshot video);

    }

    private final OnVideoSelectedListener mListener;

    public VideoAdapter(Query query, OnVideoSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemVideoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemVideoBinding binding;

        public ViewHolder(ItemVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnVideoSelectedListener listener) {

            Video video = snapshot.toObject(Video.class);

            // Load image
            assert video != null;
            Glide.with(binding.videoImage.getContext())
                    .load(video.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.empty)
                    .into(binding.videoImage);

            binding.videoTitle.setText(video.getTitle());
            binding.videoSource.setText(video.getSource());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onVideoSelected(snapshot);
                    }
                }
            });
        }

    }
}
