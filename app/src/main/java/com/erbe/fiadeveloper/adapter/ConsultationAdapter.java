package com.erbe.fiadeveloper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ItemConsultationBinding;
import com.erbe.fiadeveloper.model.Consultation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConsultationAdapter extends FirestoreAdapter<ConsultationAdapter.ViewHolder> {

    public interface OnConsultationSelectedListener {

        void onConsultationSelected(DocumentSnapshot consultation, Consultation model);

    }

    private final OnConsultationSelectedListener mListener;

    public ConsultationAdapter(Query query, OnConsultationSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemConsultationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemConsultationBinding binding;

        public ViewHolder(ItemConsultationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnConsultationSelectedListener listener) {

            Consultation consultation = snapshot.toObject(Consultation.class);

            final SimpleDateFormat FORMAT  = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);

            final SimpleDateFormat DATE  = new SimpleDateFormat(
                    "MM/dd/yyyy", Locale.US);

            final SimpleDateFormat TIME  = new SimpleDateFormat(
                    "HH:mm", Locale.US);

            Date current = Calendar.getInstance().getTime();

            if (consultation.getStatus().equals("accepted")) {
                if (FORMAT.format(current).compareTo(FORMAT.format(consultation.getFrom())) >= 0 && FORMAT.format(current).compareTo(FORMAT.format(consultation.getTo())) <= 0) {
                    consultation.setStatus("chat");
                } else if (FORMAT.format(current).compareTo(FORMAT.format(consultation.getFrom())) < 0) {
                    consultation.setStatus("pending");
                } else if (FORMAT.format(current).compareTo(FORMAT.format(consultation.getTo())) > 0) {
                    consultation.setStatus("rate");
                }
            } else {
                consultation.setStatus("finished");
            }

            binding.consultationName.setText(consultation.getConsultantName());
            binding.userName.setText(consultation.getUserName());
            binding.consultationStatus.setText(consultation.getStatus());
            binding.consultationDate.setText(String.valueOf(TIME.format(consultation.getFrom()) + " - " + TIME.format(consultation.getTo()) + " " + DATE.format(consultation.getFrom())));


            // Load image
            Glide.with(binding.userImage.getContext())
                    .load(consultation.getConsultantImage())
                    .centerCrop()
                    .placeholder(R.drawable.empty)
                    .into(binding.userImage);

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onConsultationSelected(snapshot, consultation);
                    }
                }
            });
        }

    }
}
