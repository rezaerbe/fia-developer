package com.erbe.fiadeveloper.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ItemConsultantBinding;
import com.erbe.fiadeveloper.model.Consultant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class ConsultantAdapter extends  FirestoreAdapter<ConsultantAdapter.ViewHolder> {

    public interface OnConsultantSelectedListener {

        void onConsultantSelected(DocumentSnapshot consultant);

    }

    private OnConsultantSelectedListener mListener;

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
            Glide.with(binding.consultantImage.getContext())
                    .load(consultant.getPhoto())
                    .into(binding.consultantImage);

            binding.consultantName.setText(consultant.getConsultantName());
            binding.consultantTopic.setText(consultant.getTopic());
            binding.consultantRating.setRating((float) consultant.getAvgRating());
            binding.consultantNumRating.setText(resources.getString(R.string.fmt_num_ratings, consultant.getNumRatings()));

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
