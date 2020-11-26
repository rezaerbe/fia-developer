package com.erbe.fiadeveloper.ui.article;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

    // Todo: Writer Uncomment
//    private CategoryDialogFragment mCategoryDialog;

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

        // Todo: Writer Uncomment
//        mBinding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mCategoryDialog.show(getSupportFragmentManager(), CategoryDialogFragment.TAG);
//            }
//        });

        // RecyclerView
        mAdapter = new CategoryArticleAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerCategoryArticle.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerCategoryArticle.setVisibility(View.VISIBLE);
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

        mBinding.recyclerCategoryArticle.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerCategoryArticle.setAdapter(mAdapter);
        mBinding.progressLoading.setVisibility(View.GONE);

        // Todo: Writer Uncomment
//        mCategoryDialog = new CategoryDialogFragment();
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
        // Go to the details page for the selected article
        Intent intent = new Intent(this, ListArticleActivity.class);
        intent.putExtra(ListArticleActivity.ARTICLE_CATEGORY_ID, categoryarticle.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    // Todo: Writer Uncomment
//    @Override
//    public void onCategory(Category category) {
//
//        CollectionReference docRef = mFirestore.collection("article");
//        Map<String, Object> data = new HashMap<>();
//        data.put("catName", category.getCatName());
//
//        docRef
//                .add(data)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(CategoryArticleActivity.this, "Submit Success", Toast.LENGTH_SHORT).show();
//                        hideKeyboard();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                        hideKeyboard();
//                    }
//                });
//    }

    // Todo: Writer Uncomment
//    private void hideKeyboard() {
//        View view = getCurrentFocus();
//        if (view != null) {
//            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
//                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }
}