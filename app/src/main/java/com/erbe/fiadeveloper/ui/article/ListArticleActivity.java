package com.erbe.fiadeveloper.ui.article;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.ArticleAdapter;
import com.erbe.fiadeveloper.databinding.ActivityListArticleBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class ListArticleActivity extends AppCompatActivity implements ArticleAdapter.OnArticleSelectedListener {

    private static final String TAG = "ListArticleActivity";

    public static final String ARTICLE_CATEGORY_ID = "article_category_id";

    private ActivityListArticleBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mArticleRef;

    private String categoryId;

    private ArticleAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityListArticleBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Get category ID from extras
        categoryId = getIntent().getExtras().getString(ARTICLE_CATEGORY_ID);
        if (categoryId == null) {
            throw new IllegalArgumentException("Must pass extra " + ARTICLE_CATEGORY_ID);
        }

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        mArticleRef = mFirestore.collection("article").document(categoryId);

        Query listArticleQuery = mArticleRef.collection("listarticle");

        // RecyclerView
        mAdapter = new ArticleAdapter(listArticleQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerArticle.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                    mBinding.progressLoading.setVisibility(View.GONE);
                } else {
                    mBinding.recyclerArticle.setVisibility(View.VISIBLE);
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

        mBinding.recyclerArticle.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerArticle.setAdapter(mAdapter);
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
    public void onArticleSelected(DocumentSnapshot article) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, DetailArticleActivity.class);
        intent.putExtra(DetailArticleActivity.KEY_ARTICLE_ID, article.getId());
        intent.putExtra(DetailArticleActivity.ARTICLE_CATEGORY_ID, categoryId);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}