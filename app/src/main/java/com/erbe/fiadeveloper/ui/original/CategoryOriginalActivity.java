package com.erbe.fiadeveloper.ui.original;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.CategoryOriginalAdapter;
import com.erbe.fiadeveloper.model.Category;
import com.erbe.fiadeveloper.ui.fragment.CategoryDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class CategoryOriginalActivity extends AppCompatActivity implements CategoryOriginalAdapter.OnCategoryOriginalSelectedListener
        , CategoryDialogFragment.CategoryListener{

    private static final String TAG = "CatOriginalActivity";

    private com.erbe.fiadeveloper.databinding.ActivityCategoryOriginalBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private CategoryOriginalAdapter mAdapter;

    // Todo: Writer Uncomment
    private CategoryDialogFragment mCategoryDialog;

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

        // Todo: Writer Uncomment
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCategoryDialog.show(getSupportFragmentManager(), CategoryDialogFragment.TAG);
            }
        });

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

        // Todo: Writer UnComment
        mCategoryDialog = new CategoryDialogFragment();
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

    // Todo: Writer Uncomment
    @Override
    public void onCategory(Category category) {

        CollectionReference docRef = mFirestore.collection("original");
        Map<String, Object> data = new HashMap<>();
        data.put("catName", category.getCatName());

        docRef
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CategoryOriginalActivity.this, "Submit Success", Toast.LENGTH_SHORT).show();
                        hideKeyboard();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        hideKeyboard();
                    }
                });
    }

    // Todo: Writer Uncomment
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}