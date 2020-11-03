package com.erbe.fiadeveloper.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.erbe.fiadeveloper.databinding.ItemConsultationBinding;
import com.erbe.fiadeveloper.model.Consultation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ConsultationAdapter extends  FirestoreAdapter<ConsultationAdapter.ViewHolder> {

    public interface OnConsultationSelectedListener {

        void onConsultationSelected(DocumentSnapshot consultation);

    }

    private OnConsultationSelectedListener mListener;

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
            Resources resources = itemView.getResources();

            final SimpleDateFormat FORMAT  = new SimpleDateFormat(
                    "MM/dd/yyyy", Locale.US);

            binding.consultationName.setText(consultation.getConsultantName());
            binding.userName.setText(consultation.getUserName());
            binding.consultationStatus.setText(consultation.getStatus());
            binding.consultationDate.setText(FORMAT.format(consultation.getTimestamp()));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onConsultationSelected(snapshot);
                    }
                }
            });
        }

    }
}
