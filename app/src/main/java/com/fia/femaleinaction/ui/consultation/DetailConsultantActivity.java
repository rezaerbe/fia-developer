package com.fia.femaleinaction.ui.consultation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fia.femaleinaction.R;
import com.fia.femaleinaction.adapter.AvailableAdapter;
import com.fia.femaleinaction.adapter.RatingAdapter;
import com.fia.femaleinaction.databinding.ActivityDetailConsultantBinding;
import com.fia.femaleinaction.model.Available;
import com.fia.femaleinaction.model.Consultant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class DetailConsultantActivity extends AppCompatActivity implements EventListener<DocumentSnapshot>,
        AvailableAdapter.OnAvailableSelectedListener {

    private static final String TAG = "DetailConsultant";

    public static final String KEY_CONSULTANT_ID = "key_consultant_id";

    private ActivityDetailConsultantBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mConsultantRef;
    private ListenerRegistration mConsultantRegistration;

    FirebaseUser user;

    private RatingAdapter mRatingAdapter;
    private AvailableAdapter mAvailableAdapter;

    // Todo: Consultant Uncomment
//    private AvailableDialogFragment mAvailableDialog;

    String consultantId;

    private Consultant consultantModel;

    private Date current;

    private final SimpleDateFormat FORMAT  = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

    private final SimpleDateFormat TIME  = new SimpleDateFormat("HH:mm:ss", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailConsultantBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Get consultant ID from extras
        String consultantId = getIntent().getExtras().getString(KEY_CONSULTANT_ID);
        if (consultantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_CONSULTANT_ID);
        }

        // Todo: Consultant Uncomment
//        mBinding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAvailableDialog.show(getSupportFragmentManager(), AvailableDialogFragment.TAG);
//            }
//        });

        current = Calendar.getInstance().getTime();

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get reference to the consultant
        mConsultantRef = mFirestore.collection("consultant").document(consultantId);

        // Get ratings
        Query ratingsQuery = mConsultantRef
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // Get available
        Query availableQuery = mConsultantRef
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

        // Todo: Consultant Uncomment
//        mAvailableDialog = new AvailableDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        mRatingAdapter.startListening();
        mAvailableAdapter.startListening();
        mConsultantRegistration = mConsultantRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();
        mAvailableAdapter.stopListening();

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

        onConsultantLoaded(Objects.requireNonNull(snapshot.toObject(Consultant.class)));
    }

    @Override
    public void onAvailableSelected(DocumentSnapshot available, Available model) {

        // Todo: Consultant Comment
        DocumentReference docRef = mFirestore.collection("consultant").document(consultantId).collection("available").document(available.getId()).collection("user").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Toast.makeText(DetailConsultantActivity.this, "This request is already taken", Toast.LENGTH_SHORT).show();
                    } else {

                        if (FORMAT.format(current).compareTo(FORMAT.format(model.getFrom())) <= 0 && (model.getFrom().getTime() - current.getTime()) / 3600 >= 1000) {
                            Map<String, Object> userId = new HashMap<>();
                            userId.put("userId", user.getUid());

                            docRef
                                .set(userId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(DetailConsultantActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                            Map<String, Object> consultation = new HashMap<>();
                            consultation.put("consultantId", consultantModel.getConsultantId());
                            consultation.put("consultantName", consultantModel.getConsultantName());
                            consultation.put("userId", user.getUid());
                            consultation.put("userName", user.getDisplayName());
                            consultation.put("consultantImage", consultantModel.getPhoto());
                            consultation.put("userImage", Objects.requireNonNull(user.getPhotoUrl()).toString());
                            consultation.put("status", "accepted");
                            consultation.put("from", model.getFrom());
                            consultation.put("to", model.getTo());

                            mFirestore.collection("consultation")
                                    .add(consultation)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(DetailConsultantActivity.this, "Submit success", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                        } else {
//                            Toast.makeText(DetailConsultantActivity.this, "This request is not available", Toast.LENGTH_SHORT).show();
                            Toast.makeText(DetailConsultantActivity.this, String.valueOf(FORMAT.format(current).compareTo(FORMAT.format(model.getFrom()))), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void onConsultantLoaded(Consultant consultant) {

        consultantId = consultant.getConsultantId();

        consultantModel = consultant;

        mBinding.consultantNameDetail.setText(consultant.getConsultantName());
        mBinding.consultantDescription.setText(consultant.getDescription());

        if (consultant.getTopic() != null) {
            mBinding.consultantTopicDetail.setText(consultant.getTopic());
        } else {
            mBinding.consultantTopicDetail.setText("Topic");
        }
        if (String.valueOf(consultant.getAvgRating()) != null) {
            mBinding.consultantRatingDetail.setRating((float) consultant.getAvgRating());
        } else {
            mBinding.consultantRatingDetail.setRating(0);
        }
        if (String.valueOf(consultant.getNumRatings()) != null) {
            mBinding.consultantNumRatingDetail.setText(getString(R.string.fmt_num_ratings, consultant.getNumRatings()));
        } else {
            mBinding.consultantNumRatingDetail.setText("(0)");
        }

        // Background image
        Glide.with(mBinding.consultantImageDetail.getContext())
                .load(consultant.getPhoto())
                .centerCrop()
                .placeholder(R.drawable.empty)
                .into(mBinding.consultantImageDetail);

        mBinding.progressLoading.setVisibility(View.GONE);
    }

    // Todo: Consultant Uncomment
//    @Override
//    public void onAvailable(Available available) {
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        CollectionReference docRef = mFirestore.collection("consultant").document(consultantId).collection("available");
//        Map<String, Object> data = new HashMap<>();
//        data.put("from", available.getFrom());
//        data.put("to", available.getTo());
//
//        docRef
//                .add(data)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(DetailCoachActivity.this, "Submit success", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//    }
}