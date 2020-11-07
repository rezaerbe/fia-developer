package com.erbe.fiadeveloper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.erbe.fiadeveloper.databinding.ItemAvailableBinding;
import com.erbe.fiadeveloper.model.Available;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AvailableAdapter extends  FirestoreAdapter<AvailableAdapter.ViewHolder> {

    public interface OnAvailableSelectedListener {

        void onAvailableSelected(DocumentSnapshot available, Available model);

    }

    private OnAvailableSelectedListener mListener;

    public AvailableAdapter(Query query, OnAvailableSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemAvailableBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemAvailableBinding binding;

        public ViewHolder(ItemAvailableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnAvailableSelectedListener listener) {

            final SimpleDateFormat FORMAT  = new SimpleDateFormat(
                    "MM/dd/yyyy", Locale.US);

            Available available = snapshot.toObject(Available.class);

            binding.dateAvailable.setText(FORMAT.format(available.getAvailable()));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onAvailableSelected(snapshot, available);
                    }
                }
            });
        }

    }
}

