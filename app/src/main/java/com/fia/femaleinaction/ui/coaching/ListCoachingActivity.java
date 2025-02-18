package com.fia.femaleinaction.ui.coaching;

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

import com.fia.femaleinaction.R;
import com.fia.femaleinaction.adapter.CoachingAdapter;
import com.fia.femaleinaction.databinding.ActivityListCoachingBinding;
import com.fia.femaleinaction.model.Coach;
import com.fia.femaleinaction.model.Coaching;
import com.fia.femaleinaction.model.Rating;
import com.fia.femaleinaction.ui.fragment.RatingDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

public class ListCoachingActivity extends AppCompatActivity implements CoachingAdapter.OnCoachingSelectedListener, RatingDialogFragment.RatingListener {

    private static final String TAG = "ListCoachingActivity";

    private ActivityListCoachingBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private CoachingAdapter mAdapter;

    // Todo: Coach Comment
    private RatingDialogFragment mRatingDialog;

    private String coachId;

    private DocumentReference mCoachRef, mCoachingRef;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityListCoachingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get coaching
        // Todo: Coach and Consultant Change
        mQuery = mFirestore.collection("coaching").whereEqualTo("status", "accepted").whereEqualTo("userId", user.getUid());

        // RecyclerView
        mAdapter = new CoachingAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerCoaching.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerCoaching.setVisibility(View.VISIBLE);
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

        mBinding.recyclerCoaching.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerCoaching.setAdapter(mAdapter);
        mBinding.progressLoading.setVisibility(View.GONE);

        // Todo: Coach Comment
        mRatingDialog = new RatingDialogFragment();
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
    public void onCoachingSelected(DocumentSnapshot coaching, Coaching model) {

        if (model.getStatus().equals("chat")) {

            // Go to the details page for the selected coaching
            Intent intent = new Intent(ListCoachingActivity.this, CoachingActivity.class);
            intent.putExtra(CoachingActivity.KEY_COACHING_ID, coaching.getId());

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

        } else if (model.getStatus().equals("pending")) {
            Toast.makeText(ListCoachingActivity.this, "Chat is not available yet", Toast.LENGTH_SHORT).show();

        }
        else if (model.getStatus().equals("rate")) {

            // Todo: Coach Comment
            coachId = model.getCoachId();

            // Get reference to the restaurant
            mCoachRef = mFirestore.collection("coach").document(coachId);

            mCoachingRef = mFirestore.collection("coaching").document(coaching.getId());

            mRatingDialog.show(getSupportFragmentManager(), RatingDialogFragment.TAG);

        }
        else {
            Toast.makeText(this, "This coaching is alredy finished", Toast.LENGTH_SHORT).show();
        }

    }

    // Todo: Coach Comment
    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mCoachRef, rating)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Rating added");

                        mCoachingRef
                                .update("status", "finished")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                        Toast.makeText(ListCoachingActivity.this, "Submit Success", Toast.LENGTH_SHORT).show();
                        // Hide keyboard and scroll to top
                        hideKeyboard();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add rating failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(mBinding.getRoot(), "Failed to add rating",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    // Todo: Coach Comment
    private Task<Void> addRating(final DocumentReference coachRef, final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = coachRef.collection("ratings").document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Coach coach = transaction.get(coachRef).toObject(Coach.class);

                // Compute new number of ratings
                assert coach != null;
                int newNumRatings = coach.getNumRatings() + 1;

                // Compute new average rating
                double oldRatingTotal = coach.getAvgRating() * coach.getNumRatings();
                double newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;

                // Set new restaurant info
                coach.setNumRatings(newNumRatings);
                coach.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(coachRef, coach);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }

    // Todo: Coach Comment
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}