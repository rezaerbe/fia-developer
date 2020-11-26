package com.erbe.fiadeveloper.ui.coaching;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.AvailableAdapter;
import com.erbe.fiadeveloper.adapter.RatingAdapter;
import com.erbe.fiadeveloper.databinding.ActivityDetailCoachBinding;
import com.erbe.fiadeveloper.model.Available;
import com.erbe.fiadeveloper.model.Coach;
import com.erbe.fiadeveloper.ui.fragment.AvailableDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DetailCoachActivity extends AppCompatActivity implements EventListener<DocumentSnapshot>,
        AvailableAdapter.OnAvailableSelectedListener, AvailableDialogFragment.AvailableListener {

    private static final String TAG = "DetailCoach";

    public static final String KEY_COACH_ID = "key_coach_id";

    private ActivityDetailCoachBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mCoachRef;
    private ListenerRegistration mCoachRegistration;

    FirebaseUser user;

    private RatingAdapter mRatingAdapter;
    private AvailableAdapter mAvailableAdapter;

    // Todo: Coach Uncomment
    private AvailableDialogFragment mAvailableDialog;

    String coachId;

    private Coach coachModel;

    private Date current;

    private final SimpleDateFormat FORMAT  = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

    private final SimpleDateFormat TIME  = new SimpleDateFormat("HH:mm:ss", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailCoachBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Get coach ID from extras
        String coachId = getIntent().getExtras().getString(KEY_COACH_ID);
        if (coachId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_COACH_ID);
        }

        // Todo: Coach Uncomment
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAvailableDialog.show(getSupportFragmentManager(), AvailableDialogFragment.TAG);
            }
        });

        current = Calendar.getInstance().getTime();

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get reference to the coach
        mCoachRef = mFirestore.collection("coach").document(coachId);

        // Get ratings
        Query ratingsQuery = mCoachRef
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // Get available
        Query availableQuery = mCoachRef
                .collection("available")
                .orderBy("from", Query.Direction.DESCENDING)
                .whereGreaterThan("from", current);

        // RecyclerView
        mAvailableAdapter = new AvailableAdapter(availableQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerAvailable.setVisibility(View.GONE);
                    mBinding.available.setText("Not Available");
                } else {
                    mBinding.recyclerAvailable.setVisibility(View.VISIBLE);
                    mBinding.available.setText("Available");
                }
            }
        };
        mBinding.recyclerAvailable.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerAvailable.setAdapter(mAvailableAdapter);

        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mBinding.recyclerRatings.setVisibility(View.GONE);
                    mBinding.emptyReview.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerRatings.setVisibility(View.VISIBLE);
                    mBinding.emptyReview.setVisibility(View.GONE);
                }
            }
        };
        mBinding.recyclerRatings.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerRatings.setAdapter(mRatingAdapter);

        // Todo: Coach Uncomment
        mAvailableDialog = new AvailableDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        mRatingAdapter.startListening();
        mAvailableAdapter.startListening();
        mCoachRegistration = mCoachRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();
        mAvailableAdapter.stopListening();

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

        onCoachLoaded(Objects.requireNonNull(snapshot.toObject(Coach.class)));
    }

    @Override
    public void onAvailableSelected(DocumentSnapshot available, Available model) {

        // Todo: Coach Comment
//        DocumentReference docRef = mFirestore.collection("coach").document(coachId).collection("available").document(available.getId()).collection("user").document(user.getUid());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    assert document != null;
//                    if (document.exists()) {
//                        Toast.makeText(DetailCoachActivity.this, "This request is already taken", Toast.LENGTH_SHORT).show();
//                    } else {
//
//                        if (FORMAT.format(current).compareTo(FORMAT.format(model.getFrom())) < 0 && TIME.format(current).compareTo(TIME.format(model.getFrom())) + 1 < 0) {
//
//                            Map<String, Object> userId = new HashMap<>();
//                            userId.put("userId", user.getUid());
//
//                            docRef
//                                .set(userId)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(DetailCoachActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.w(TAG, "Error writing document", e);
//                                    }
//                                });
//
//                            Map<String, Object> coaching = new HashMap<>();
//                            coaching.put("coachId", coachModel.getCoachId());
//                            coaching.put("coachName", coachModel.getCoachName());
//                            coaching.put("userId", user.getUid());
//                            coaching.put("userName", user.getDisplayName());
//                            coaching.put("coachImage", coachModel.getPhoto());
//                            coaching.put("userImage", Objects.requireNonNull(user.getPhotoUrl()).toString());
//                            coaching.put("status", "accepted");
//                            coaching.put("from", model.getFrom());
//                            coaching.put("to", model.getTo());
//
//                            mFirestore.collection("coaching")
//                                    .add(coaching)
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference documentReference) {
//                                            Toast.makeText(DetailCoachActivity.this, "Submit success", Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w(TAG, "Error adding document", e);
//                                        }
//                                    });
//                        } else {
//                            Toast.makeText(DetailCoachActivity.this, "This request is not available", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
    }

    private void onCoachLoaded(Coach coach) {

        coachId = coach.getCoachId();

        coachModel = coach;

        mBinding.coachNameDetail.setText(coach.getCoachName());
        mBinding.coachDescription.setText(coach.getDescription());

        if (coach.getTopic() != null) {
            mBinding.coachTopicDetail.setText(coach.getTopic());
        } else {
            mBinding.coachTopicDetail.setText("Topic");
        }
        if (String.valueOf(coach.getAvgRating()) != null) {
            mBinding.coachRatingDetail.setRating((float) coach.getAvgRating());
        } else {
            mBinding.coachRatingDetail.setRating(0);
        }
        if (String.valueOf(coach.getNumRatings()) != null) {
            mBinding.coachNumRatingDetail.setText(getString(R.string.fmt_num_ratings, coach.getNumRatings()));
        } else {
            mBinding.coachNumRatingDetail.setText("(0)");
        }

        // Background image
        Glide.with(mBinding.coachImageDetail.getContext())
                .load(coach.getPhoto())
                .centerCrop()
                .placeholder(R.drawable.empty)
                .into(mBinding.coachImageDetail);

        mBinding.progressLoading.setVisibility(View.GONE);
    }

    // Todo: Coach Uncomment
    @Override
    public void onAvailable(Available available) {

        CollectionReference docRef = mFirestore.collection("coach").document(coachId).collection("available");
        Map<String, Object> data = new HashMap<>();
        data.put("from", available.getFrom());
        data.put("to", available.getTo());

        docRef
            .add(data)
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