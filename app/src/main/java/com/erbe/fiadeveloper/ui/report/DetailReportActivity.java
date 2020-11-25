package com.erbe.fiadeveloper.ui.report;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.erbe.fiadeveloper.R;
import com.erbe.fiadeveloper.databinding.ActivityDetailReportBinding;
import com.erbe.fiadeveloper.model.Report;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class DetailReportActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "DetailReport";

    public static final String KEY_REPORT_ID = "key_report_id";

    private ActivityDetailReportBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mReportRef;
    private ListenerRegistration mReportRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDetailReportBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.progressLoading.setVisibility(View.VISIBLE);

        // Get report ID from extras
        String reportId = getIntent().getExtras().getString(KEY_REPORT_ID);
        if (reportId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_REPORT_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the report
        mReportRef = mFirestore.collection("report").document(reportId);

    }

    @Override
    public void onStart() {
        super.onStart();

        mReportRegistration = mReportRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mReportRegistration != null) {
            mReportRegistration.remove();
            mReportRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * Listener for the Report document ({@link #mReportRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "report:onEvent", e);
            return;
        }

        onReportLoaded(snapshot.toObject(Report.class));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void onReportLoaded(Report report) {

        mBinding.status.setText(report.getStatus());
        mBinding.type.setText(report.getType());
        mBinding.phone.setText(report.getPhone());
        mBinding.chronology.setText(report.getChronology());

        mBinding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                intent.putExtra(ContactsContract.Intents.Insert.NAME, report.getUserName());
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, report.getPhone());
                startActivity(intent);
            }
        });

        mBinding.progressLoading.setVisibility(View.GONE);
    }
}
