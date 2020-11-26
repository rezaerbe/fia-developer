package com.erbe.fiadeveloper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ItemCoachingBinding;
import com.erbe.fiadeveloper.model.Coaching;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CoachingAdapter extends FirestoreAdapter<CoachingAdapter.ViewHolder> {

    public interface OnCoachingSelectedListener {

        void onCoachingSelected(DocumentSnapshot coaching, Coaching model);

    }

    private final OnCoachingSelectedListener mListener;

    public CoachingAdapter(Query query, OnCoachingSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCoachingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCoachingBinding binding;

        public ViewHolder(ItemCoachingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnCoachingSelectedListener listener) {

            Coaching coaching = snapshot.toObject(Coaching.class);

            final SimpleDateFormat FORMAT  = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);

            final SimpleDateFormat DATE  = new SimpleDateFormat(
                    "MM/dd/yyyy", Locale.US);

            final SimpleDateFormat TIME  = new SimpleDateFormat(
                    "HH:mm", Locale.US);

            Date current = Calendar.getInstance().getTime();

            assert coaching != null;
            if (coaching.getStatus().equals("accepted")) {
                if (FORMAT.format(current).compareTo(FORMAT.format(coaching.getFrom())) >= 0 && FORMAT.format(current).compareTo(FORMAT.format(coaching.getTo())) <= 0) {
                    coaching.setStatus("chat");
                } else if (FORMAT.format(current).compareTo(FORMAT.format(coaching.getFrom())) < 0) {
                    coaching.setStatus("pending");
                } else if (FORMAT.format(current).compareTo(FORMAT.format(coaching.getTo())) > 0) {
                    coaching.setStatus("rate");
                }
            } else {
                coaching.setStatus("finished");
            }

            binding.coachingName.setText(coaching.getCoachName());
            binding.userName.setText(coaching.getUserName());
            binding.coachingStatus.setText(coaching.getStatus());
            binding.coachingDate.setText(String.valueOf(TIME.format(coaching.getFrom()) + " - " + TIME.format(coaching.getTo()) + " " + DATE.format(coaching.getFrom())));


            // Load image
            Glide.with(binding.userImage.getContext())
                    .load(coaching.getCoachImage())
                    .centerCrop()
                    .placeholder(R.drawable.empty)
                    .into(binding.userImage);

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCoachingSelected(snapshot, coaching);
                    }
                }
            });
        }

    }
}
