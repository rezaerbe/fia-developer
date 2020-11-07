package com.erbe.fiadeveloper.ui.coaching;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ActivityDetailCoachBinding;
import com.erbe.fiadeveloper.model.Coach;
import com.erbe.fiadeveloper.ui.MainActivity;
import com.erbe.fiadeveloper.ui.ReportActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailCoachActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "DetailCoach";

    public static final String KEY_COACH_ID = "key_coach_id";

    private ActivityDetailCoachBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mCoachRef;
    private ListenerRegistration mCoachRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailCoachBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Get coach ID from extras
        String coachId = getIntent().getExtras().getString(KEY_COACH_ID);
        if (coachId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_COACH_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the coach
        mCoachRef = mFirestore.collection("coach").document(coachId);
    }

    @Override
    public void onStart() {
        super.onStart();
        mCoachRegistration = mCoachRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mCoachRegistration != null) {
            mCoachRegistration.remove();
            mCoachRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Listener for the Restaurant document ({@link #mCoachRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "coach:onEvent", e);
            return;
        }

        onCoachLoaded(snapshot.toObject(Coach.class));
    }

    private void onCoachLoaded(Coach coach) {

        final SimpleDateFormat FORMAT  = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.getDefault());

        Date current = Calendar.getInstance().getTime();

        mBinding.coachNameDetail.setText(coach.getCoachName());
        mBinding.coachTopicDetail.setText(coach.getTopic());
        mBinding.coachDescription.setText(coach.getDescription());

        mBinding.coachRatingDetail.setRating((float) coach.getAvgRating());
        mBinding.coachNumRatingDetail.setText(getString(R.string.fmt_num_ratings, coach.getNumRatings()));

        mBinding.coachAvailable.setText(FORMAT.format(coach.getAvailable()));

        if (FORMAT.format(current).compareTo(FORMAT.format(coach.getAvailable())) < 0) {
            mBinding.request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Firestore
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        Map<String, Object> coaching = new HashMap<>();
                        coaching.put("coachId", coach.getCoachId());
                        coaching.put("coachName", coach.getCoachName());
                        coaching.put("userId", user.getUid());
                        coaching.put("userName", user.getDisplayName());
                        coaching.put("status", "pending");
                        coaching.put("timestamp", coach.getAvailable());

                        mFirestore.collection("coaching")
                                .add(coaching)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(DetailCoachActivity.this, "Submit success", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    }
                }
            });
        } else {
            mBinding.request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailCoachActivity.this, "Coach not available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Background image
        Glide.with(mBinding.coachImageDetail.getContext())
                .load(coach.getPhoto())
                .into(mBinding.coachImageDetail);
    }
}