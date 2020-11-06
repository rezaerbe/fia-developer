package com.erbe.fiadeveloper.ui.article;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.CategoryArticleAdapter;
import com.erbe.fiadeveloper.databinding.ActivityCategoryArticleBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class CategoryArticleActivity extends AppCompatActivity implements CategoryArticleAdapter.OnCategoryArticleSelectedListener {

    private static final String TAG = "CategoryArticleActivity";

    private ActivityCategoryArticleBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private CategoryArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCategoryArticleBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get article
        mQuery = mFirestore.collection("article");

        // RecyclerView
        mAdapter = new CategoryArticleAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerCategoryArticle.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                    mBinding.progressLoading.setVisibility(View.GONE);
                } else {
                    mBinding.recyclerCategoryArticle.setVisibility(View.VISIBLE);
                    mBinding.viewEmpty.setVisibility(View.GONE);
                    mBinding.progressLoading.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: maaf terjadi kesalahan.", Snackbar.LENGTH_LONG).show();
                mBinding.progressLoading.setVisibility(View.GONE);
            }
        };

        mBinding.recyclerCategoryArticle.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerCategoryArticle.setAdapter(mAdapter);
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
    public void onCategoryArticleSelected(DocumentSnapshot categoryarticle) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, ListArticleActivity.class);
        intent.putExtra(ListArticleActivity.ARTICLE_CATEGORY_ID, categoryarticle.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}