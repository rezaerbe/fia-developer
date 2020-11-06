package com.erbe.fiadeveloper.ui.original;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.OriginalAdapter;
import com.erbe.fiadeveloper.databinding.ActivityDetailOriginalBinding;
import com.erbe.fiadeveloper.model.Original;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class DetailOriginalActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "DetailOriginal";

    public static final String KEY_ORIGINAL_ID = "key_original_id";
    public static final String ORIGINAL_CATEGORY_ID = "original_category_id";

    private ActivityDetailOriginalBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mOriginalRef, mListOriginalRef;
    private ListenerRegistration mOriginalRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailOriginalBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Get category ID from extras
        String categoryId = getIntent().getExtras().getString(ORIGINAL_CATEGORY_ID);
        if (categoryId == null) {
            throw new IllegalArgumentException("Must pass extra " + ORIGINAL_CATEGORY_ID);
        }

        // Get original ID from extras
        String originalId = getIntent().getExtras().getString(KEY_ORIGINAL_ID);
        if (originalId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_ORIGINAL_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the original
        mOriginalRef = mFirestore.collection("original").document(categoryId);
        mListOriginalRef = mOriginalRef.collection("listoriginal").document(originalId);

    }

    @Override
    public void onStart() {
        super.onStart();

        mOriginalRegistration = mListOriginalRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mOriginalRegistration != null) {
            mOriginalRegistration.remove();
            mOriginalRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Listener for the Original document ({@link #mOriginalRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "original:onEvent", e);
            return;
        }

        onOriginalLoaded(snapshot.toObject(Original.class));
    }

    private void onOriginalLoaded(Original original) {

        mBinding.originalTitleDetail.setText(original.getTitle());
        mBinding.originalSourceDetail.setText(original.getSource());
        mBinding.originalDescription.setText(original.getDescription());

        // Background image
        Glide.with(mBinding.originalImageDetail.getContext())
                .load(original.getImage())
                .into(mBinding.originalImageDetail);
    }

}
