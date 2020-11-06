package com.erbe.fiadeveloper.ui.consultation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ActivityDetailConsultantBinding;
import com.erbe.fiadeveloper.model.Consultant;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailConsultantActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "DetailConsultant";

    public static final String KEY_CONSULTANT_ID = "key_consultant_id";

    private ActivityDetailConsultantBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mConsultantRef;
    private ListenerRegistration mConsultantRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailConsultantBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Get consultant ID from extras
        String consultantId = getIntent().getExtras().getString(KEY_CONSULTANT_ID);
        if (consultantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_CONSULTANT_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mConsultantRef = mFirestore.collection("consultant").document(consultantId);
    }

    @Override
    public void onStart() {
        super.onStart();
        mConsultantRegistration = mConsultantRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mConsultantRegistration != null) {
            mConsultantRegistration.remove();
            mConsultantRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Listener for the Restaurant document ({@link #mConsultantRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "consultant:onEvent", e);
            return;
        }

        onConsultantLoaded(snapshot.toObject(Consultant.class));
    }

    private void onConsultantLoaded(Consultant consultant) {

        final SimpleDateFormat FORMAT  = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.US);

        mBinding.consultantNameDetail.setText(consultant.getConsultantName());
        mBinding.consultantTopicDetail.setText(consultant.getTopic());
        mBinding.consultantDescription.setText(consultant.getDescription());

        mBinding.consultantRatingDetail.setRating((float) consultant.getAvgRating());
        mBinding.consultantNumRatingDetail.setText(getString(R.string.fmt_num_ratings, consultant.getNumRatings()));

        mBinding.consultantAvailable.setText(FORMAT.format(consultant.getAvailable()));

        // Background image
        Glide.with(mBinding.consultantImageDetail.getContext())
                .load(consultant.getPhoto())
                .into(mBinding.consultantImageDetail);
    }
}