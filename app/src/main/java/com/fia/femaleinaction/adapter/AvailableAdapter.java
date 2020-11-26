package com.fia.femaleinaction.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.fia.femaleinaction.databinding.ItemAvailableBinding;
import com.fia.femaleinaction.model.Available;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AvailableAdapter extends  FirestoreAdapter<AvailableAdapter.ViewHolder> {

    public interface OnAvailableSelectedListener {

        void onAvailableSelected(DocumentSnapshot available, Available model);

    }

    private final OnAvailableSelectedListener mListener;

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

            final SimpleDateFormat DATE  = new SimpleDateFormat(
                    "dd/MM/yyyy", Locale.US);

            final SimpleDateFormat TIME  = new SimpleDateFormat(
                    "HH:mm", Locale.US);

            Available available = snapshot.toObject(Available.class);

            assert available != null;
            binding.dateAvailable.setText(String.valueOf(TIME.format(available.getFrom()) + " - " + TIME.format(available.getTo()) + " " + DATE.format(available.getFrom())));

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
