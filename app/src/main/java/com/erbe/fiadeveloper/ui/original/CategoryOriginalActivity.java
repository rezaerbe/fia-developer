package com.erbe.fiadeveloper.ui.original;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.CategoryOriginalAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class CategoryOriginalActivity extends AppCompatActivity implements CategoryOriginalAdapter.OnCategoryOriginalSelectedListener {

    private static final String TAG = "CategoryOriginalActivity";

    private com.erbe.fiadeveloper.databinding.ActivityCategoryOriginalBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private CategoryOriginalAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = com.erbe.fiadeveloper.databinding.ActivityCategoryOriginalBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get original
        mQuery = mFirestore.collection("original");

        // RecyclerView
        mAdapter = new CategoryOriginalAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerCategoryOriginal.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerCategoryOriginal.setVisibility(View.VISIBLE);
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

        mBinding.recyclerCategoryOriginal.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerCategoryOriginal.setAdapter(mAdapter);
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
    public void onCategoryOriginalSelected(DocumentSnapshot categoryoriginal) {
        // Go to the details page for the selected original
        Intent intent = new Intent(this, ListOriginalActivity.class);
        intent.putExtra(ListOriginalActivity.ORIGINAL_CATEGORY_ID, categoryoriginal.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}