package com.fia.femaleinaction.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fia.femaleinaction.R;
import com.fia.femaleinaction.databinding.ItemConsultantBinding;
import com.fia.femaleinaction.model.Consultant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class ConsultantAdapter extends  FirestoreAdapter<ConsultantAdapter.ViewHolder> {

    public interface OnConsultantSelectedListener {

        void onConsultantSelected(DocumentSnapshot consultant);

    }

    private final OnConsultantSelectedListener mListener;

    public ConsultantAdapter(Query query, OnConsultantSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemConsultantBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemConsultantBinding binding;

        public ViewHolder(ItemConsultantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnConsultantSelectedListener listener) {

            Consultant consultant = snapshot.toObject(Consultant.class);
            Resources resources = itemView.getResources();

            // Load image
            assert consultant != null;
            Glide.with(binding.consultantImage.getContext())
                    .load(consultant.getPhoto())
                    .centerCrop()
                    .placeholder(R.drawable.empty)
                    .into(binding.consultantImage);

            binding.consultantName.setText(consultant.getConsultantName());
            if (consultant.getTopic() != null) {
                binding.consultantTopic.setText(consultant.getTopic());
            } else {
                binding.consultantTopic.setText("Topic");
            }
            if (String.valueOf(consultant.getAvgRating()) != null) {
                binding.consultantRating.setRating((float) consultant.getAvgRating());
            } else {
                binding.consultantRating.setRating(0);
            }
            if (String.valueOf(consultant.getNumRatings()) != null) {
                binding.consultantNumRating.setText(resources.getString(R.string.fmt_num_ratings, consultant.getNumRatings()));
            } else {
                binding.consultantNumRating.setText("(0)");
            }

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onConsultantSelected(snapshot);
                    }
                }
            });
        }

    }
}
