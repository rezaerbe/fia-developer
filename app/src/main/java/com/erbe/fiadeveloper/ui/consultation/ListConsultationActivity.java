package com.erbe.fiadeveloper.ui.consultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.adapter.ConsultationAdapter;
import com.erbe.fiadeveloper.databinding.ActivityListConsultationBinding;
import com.erbe.fiadeveloper.model.Consultant;
import com.erbe.fiadeveloper.model.Consultation;
import com.erbe.fiadeveloper.model.Rating;
import com.erbe.fiadeveloper.ui.fragment.RatingDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ListConsultationActivity extends AppCompatActivity implements ConsultationAdapter.OnConsultationSelectedListener, RatingDialogFragment.RatingListener {

    private static final String TAG = "ListConsultation";

    private ActivityListConsultationBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private ConsultationAdapter mAdapter;

    private RatingDialogFragment mRatingDialog;

    private String consultantId;

    private DocumentReference mConsultantRef, mConsultationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityListConsultationBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get consultation
        mQuery = mFirestore.collection("consultation").whereEqualTo("status", "accepted");

        // RecyclerView
        mAdapter = new ConsultationAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerConsultation.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerConsultation.setVisibility(View.VISIBLE);
                    mBinding.viewEmpty.setVisibility(View.GONE);
                }
                mBinding.progressLoading.setVisibility(View.GONE);
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: maaf terjadi kesalahan.", Snackbar.LENGTH_LONG).show();
                mBinding.progressLoading.setVisibility(View.GONE);
            }
        };

        mBinding.recyclerConsultation.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerConsultation.setAdapter(mAdapter);
        mBinding.progressLoading.setVisibility(View.GONE);

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
    public void onConsultationSelected(DocumentSnapshot consultation, Consultation model) {

        final SimpleDateFormat FORMAT = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.US);

        Date current = Calendar.getInstance().getTime();

        if (FORMAT.format(current).equals(FORMAT.format(model.getTimestamp()))) {

            // Go to the details page for the selected restaurant
            Intent intent = new Intent(ListConsultationActivity.this, ConsultationActivity.class);
            intent.putExtra(ConsultationActivity.KEY_CONSULTATION_ID, consultation.getId());

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

        } else if (FORMAT.format(current).compareTo(FORMAT.format(model.getTimestamp())) < 0) {
            Toast.makeText(ListConsultationActivity.this, "Chat is not available yet", Toast.LENGTH_SHORT).show();

        } else if (FORMAT.format(current).compareTo(FORMAT.format(model.getTimestamp())) > 0 && model.getStatus().equals("rate")) {
            consultantId = model.getConsultantId();

            // Get reference to the restaurant
            mConsultantRef = mFirestore.collection("consultant").document(consultantId);

            mConsultationRef = mFirestore.collection("consultation").document(consultation.getId());

            mRatingDialog.show(getSupportFragmentManager(), RatingDialogFragment.TAG);

        } else {
            Toast.makeText(this, "This consultation is alredy finished", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mConsultantRef, rating)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Rating added");

                        mConsultationRef
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

    private Task<Void> addRating(final DocumentReference consultantRef, final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = consultantRef.collection("ratings").document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Consultant consultant = transaction.get(consultantRef).toObject(Consultant.class);

                // Compute new number of ratings
                int newNumRatings = consultant.getNumRatings() + 1;

                // Compute new average rating
                double oldRatingTotal = consultant.getAvgRating() * consultant.getNumRatings();
                double newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;

                // Set new restaurant info
                consultant.setNumRatings(newNumRatings);
                consultant.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(consultantRef, consultant);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}