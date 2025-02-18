package com.fia.femaleinaction.ui.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fia.femaleinaction.R;
import com.fia.femaleinaction.adapter.ReportAdapter;
import com.fia.femaleinaction.databinding.ActivityListReportBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class ListReportActivity extends AppCompatActivity implements ReportAdapter.OnReportSelectedListener {

    private static final String TAG = "ListReportActivity";

    private ActivityListReportBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private ReportAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityListReportBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get report
        mQuery = mFirestore
                .collection("report")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // RecyclerView
        mAdapter = new ReportAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerReport.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerReport.setVisibility(View.VISIBLE);
                    mBinding.viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: maaf terjadi kesalahan.", Snackbar.LENGTH_LONG).show();
            }
        };

        mBinding.recyclerReport.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerReport.setAdapter(mAdapter);
        mBinding.progressLoading.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onReportSelected(DocumentSnapshot report) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, DetailReportActivity.class);
        intent.putExtra(DetailReportActivity.KEY_REPORT_ID, report.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}
