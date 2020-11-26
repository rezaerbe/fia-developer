package com.erbe.fiadeveloper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.erbe.fiadeveloper.databinding.ItemReportBinding;
import com.erbe.fiadeveloper.model.Report;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class ReportAdapter extends  FirestoreAdapter<ReportAdapter.ViewHolder> {

    public interface OnReportSelectedListener {

        void onReportSelected(DocumentSnapshot report);

    }

    private final OnReportSelectedListener mListener;

    public ReportAdapter(Query query, OnReportSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemReportBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemReportBinding binding;

        public ViewHolder(ItemReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnReportSelectedListener listener) {

            Report report = snapshot.toObject(Report.class);

            assert report != null;
            binding.userName.setText(report.getUserName());
            binding.type.setText(report.getType());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onReportSelected(snapshot);
                    }
                }
            });
        }

    }
}

