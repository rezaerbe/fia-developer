package com.erbe.fiadeveloper.ui.consultation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.ConsultantAdapter;
import com.erbe.fiadeveloper.databinding.ActivityListConsultantBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class ListConsultantActivity extends AppCompatActivity implements ConsultantAdapter.OnConsultantSelectedListener {

    private static final String TAG = "ListConsultantActivity";

    private ActivityListConsultantBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private ConsultantAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityListConsultantBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get consultant
        mQuery = mFirestore.collection("consultant");

        // RecyclerView
        mAdapter = new ConsultantAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerConsultant.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerConsultant.setVisibility(View.VISIBLE);
                    mBinding.viewEmpty.setVisibility(View.GONE);
                }
                mBinding.progressLoading.setVisibility(View.GONE);
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: maaf terjadi kesalahan.", Snackbar.LENGTH_LONG).show();
                mBinding.progressLoading.setVisibility(View.GONE);
            }
        };

        mBinding.recyclerConsultant.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerConsultant.setAdapter(mAdapter);
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
    public void onConsultantSelected(DocumentSnapshot consultant) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, DetailConsultantActivity.class);
        intent.putExtra(DetailConsultantActivity.KEY_CONSULTANT_ID, consultant.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}