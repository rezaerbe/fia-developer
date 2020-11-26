package com.erbe.fiadeveloper.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ItemCoachBinding;
import com.erbe.fiadeveloper.model.Coach;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class CoachAdapter extends  FirestoreAdapter<CoachAdapter.ViewHolder> {

    public interface OnCoachSelectedListener {

        void onCoachSelected(DocumentSnapshot coach);

    }

    private final OnCoachSelectedListener mListener;

    public CoachAdapter(Query query, OnCoachSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCoachBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCoachBinding binding;

        public ViewHolder(ItemCoachBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnCoachSelectedListener listener) {

            Coach coach = snapshot.toObject(Coach.class);
            Resources resources = itemView.getResources();

            // Load image
            assert coach != null;
            Glide.with(binding.coachImage.getContext())
                    .load(coach.getPhoto())
                    .centerCrop()
                    .placeholder(R.drawable.empty)
                    .into(binding.coachImage);

            binding.coachName.setText(coach.getCoachName());
            if (coach.getTopic() != null) {
                binding.coachTopic.setText(coach.getTopic());
            } else {
                binding.coachTopic.setText("Topic");
            }
            if (String.valueOf(coach.getAvgRating()) != null) {
                binding.coachRating.setRating((float) coach.getAvgRating());
            } else {
                binding.coachRating.setRating(0);
            }
            if (String.valueOf(coach.getNumRatings()) != null) {
                binding.coachNumRating.setText(resources.getString(R.string.fmt_num_ratings, coach.getNumRatings()));
            } else {
                binding.coachNumRating.setText("(0)");
            }

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCoachSelected(snapshot);
                    }
                }
            });
        }

    }
}
