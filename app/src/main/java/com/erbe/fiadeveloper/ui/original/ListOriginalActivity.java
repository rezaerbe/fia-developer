package com.erbe.fiadeveloper.ui.original;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.OriginalAdapter;
import com.erbe.fiadeveloper.databinding.ActivityListOriginalBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class ListOriginalActivity extends AppCompatActivity implements OriginalAdapter.OnOriginalSelectedListener {

    private static final String TAG = "ListOriginalActivity";

    public static final String ORIGINAL_CATEGORY_ID = "original_category_id";

    private ActivityListOriginalBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mOriginalRef;

    String categoryId;

    private OriginalAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityListOriginalBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Get category ID from extras
        categoryId = getIntent().getExtras().getString(ORIGINAL_CATEGORY_ID);
        if (categoryId == null) {
            throw new IllegalArgumentException("Must pass extra " + ORIGINAL_CATEGORY_ID);
        }

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        mOriginalRef = mFirestore.collection("original").document(categoryId);

        Query listOriginalQuery = mOriginalRef.collection("listoriginal");

        // RecyclerView
        mAdapter = new OriginalAdapter(listOriginalQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerOriginal.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerOriginal.setVisibility(View.VISIBLE);
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

        mBinding.recyclerOriginal.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerOriginal.setAdapter(mAdapter);
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
    public void onOriginalSelected(DocumentSnapshot original) {
        // Go to the details page for the selected original
        Intent intent = new Intent(this, DetailOriginalActivity.class);
        intent.putExtra(DetailOriginalActivity.KEY_ORIGINAL_ID, original.getId());
        intent.putExtra(DetailOriginalActivity.ORIGINAL_CATEGORY_ID, categoryId);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}